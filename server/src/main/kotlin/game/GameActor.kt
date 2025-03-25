package com.cooper.game

import com.cooper.SocketContentConverterSender
import com.cooper.game.processor.ServerInboundMessageProcessorAction
import com.cooper.game.processor.handlePlayerInboundApplicationMessage
import com.cooper.game.processor.handleServerInboundApplicationMessage
import com.cooper.message.InboundMessage
import com.cooper.message.OutboundMessage
import com.cooper.message.OutboundMessage.StrippedPlayer.Companion.stripped
import com.cooper.message.server.ServerInboundMessage
import com.cooper.message.server.ServerOutboundMessage
import kotlinx.coroutines.channels.Channel
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

abstract class InnerApplicationMessage

class PlayerInboundApplicationMessage(val sessionId: SessionId, val inboundMessage: InboundMessage) :
        InnerApplicationMessage()

class ServerInboundApplicationMessage(val serverInboundMessage: ServerInboundMessage) : InnerApplicationMessage()

class PlayerJoinInnerApplicationMessage(
    val playerName: String,
    val channel: SocketContentConverterSender<OutboundMessage>,
    /// One-shot channel to receive player's connection session ID or join error
    val callback: Channel<Result<SessionId>>
) : InnerApplicationMessage()

class PlayerDisconnectInnerApplicationMessage(val playerName: PlayerName, val sessionId: SessionId) :
        InnerApplicationMessage()

class NewServerConnectionInnerApplicationMessage(val server: SocketContentConverterSender<ServerOutboundMessage>) :
        InnerApplicationMessage()

val globalInnerApplicationChannel = Channel<InnerApplicationMessage>()

class GameOverThrowable(
    val winner: SimpleRole,
    val reason: GameState.GameOverReason
) : Throwable("game over")

private val isActorActive: AtomicBoolean = AtomicBoolean(false)

suspend fun launchGameActor(server: SocketContentConverterSender<ServerOutboundMessage>) {
    if (!isActorActive.compareAndSet(false, true)) {
        println("game actor already active")
        return globalInnerApplicationChannel.send(
            NewServerConnectionInnerApplicationMessage(server)
        )
    }

    println("launched game actor")

    var gameState: GameState = GameState.Lobby(server)

    for (message in globalInnerApplicationChannel) {
        when (message) {
            is PlayerInboundApplicationMessage -> {
                val player = gameState.getBySessionId(message.sessionId) ?: continue

                try {
                    gameState.handlePlayerInboundApplicationMessage(player.name, message.inboundMessage)
                } catch (e: GameOverThrowable) {
                    gameState = (gameState as GameState.GameInProgress).toGameOver(e.winner, e.reason)
                } catch (e: IllegalStateException) {
                    println("Failed to process player message ${e.message}")
                }
            }

            is ServerInboundApplicationMessage -> {
                val action = try {
                    gameState.handleServerInboundApplicationMessage(message.serverInboundMessage)
                } catch (e: GameOverThrowable) {
                    gameState = (gameState as GameState.GameInProgress).toGameOver(e.winner, e.reason)
                    null
                } catch (e: IllegalStateException) {
                    println("Failed to process server message ${e.message}")
                    null
                }

                when (action) {
                    ServerInboundMessageProcessorAction.START_GAME -> {
                        gameState = (gameState as GameState.Lobby).toGameInProgress()
                        gameState.sendPlayerRoles()
                    }

                    ServerInboundMessageProcessorAction.BACK_TO_LOBBY -> {
                        gameState = (gameState as GameState.GameOver).toLobby()
                    }

                    null -> {}
                }
            }

            is PlayerJoinInnerApplicationMessage -> gameState.handlePlayerJoin(message)
            is PlayerDisconnectInnerApplicationMessage -> gameState.handlePlayerDisconnect(message)
            is NewServerConnectionInnerApplicationMessage -> {
                gameState.server = message.server
            }
        }

        gameState.sendServer(ServerOutboundMessage.UpdateGameState(gameState))
    }

    isActorActive.set(false)
}

private suspend fun GameState.handlePlayerJoin(message: PlayerJoinInnerApplicationMessage) {
    val channel = message.channel
    val existingPlayer = this[message.playerName]

    val sessionId = UUID.randomUUID()

    println("player ${message.playerName} joined with session ID $sessionId (existing: ${existingPlayer != null})")

    if (existingPlayer != null) {
        existingPlayer.connect(sessionId, channel)
        // If player already exists, send them (important) queued messages
        existingPlayer.queue.forEach { channel.send(it) }
        // And also send them their icon
        channel.send(OutboundMessage.AssignIcon(existingPlayer.icon))

        return message.callback.send(Result.success(sessionId))
    }

    if (this !is GameState.Lobby) {
        return message.callback.send(Result.failure(IllegalStateException("Cannot join game in progress")))
    }

    val playerIcon = PlayerIcon[this.players.size]

    val player = Player(message.playerName, playerIcon, PlayerConnection(sessionId, channel))
    this.players.add(player)

    // Send them their icon
    channel.send(OutboundMessage.AssignIcon(playerIcon))

    message.callback.send(Result.success(sessionId))
}

private suspend fun GameState.handlePlayerDisconnect(message: PlayerDisconnectInnerApplicationMessage) {
    val player = this[message.playerName] ?: return
    player.disconnect(message.sessionId)

    if (this is GameState.Lobby && player.channels.isEmpty()) {
        players.remove(player)
        sendServer(ServerOutboundMessage.UpdateGameState(this))
    }
}

suspend fun GameState.GameInProgress.sendPlayerRoles() {
    players.forEach { player ->
        player.send(
            if (player.role == ComplexRole.DEMON) OutboundMessage.AssignRole(
                role = player.role,
                demonCount = demons.size,
                teammates = demons
                    .filter { it != player }
                    .map { it.stripped },
                satan = satan.stripped
            ) else OutboundMessage.AssignRole(
                role = player.role,
                demonCount = demons.size,
            ),
            queued = true
        )
    }
}