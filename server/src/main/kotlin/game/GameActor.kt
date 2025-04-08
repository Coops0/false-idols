package com.cooper.game

import com.cooper.FalseIdolsError
import com.cooper.SocketContentConverterSender
import com.cooper.game.processor.ServerInboundMessageProcessorAction
import com.cooper.game.processor.handlePlayerInboundApplicationMessage
import com.cooper.game.processor.handleServerInboundApplicationMessage
import com.cooper.message.InboundMessage
import com.cooper.message.OutboundMessage
import com.cooper.message.OutboundMessage.StrippedPlayer.Companion.stripped
import com.cooper.message.server.ServerInboundMessage
import com.cooper.message.server.ServerOutboundMessage
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.Channel
import java.util.*

sealed class InnerApplicationMessage {
    class PlayerInboundMessageInner(val sessionId: SessionId, val inboundMessage: InboundMessage) :
            InnerApplicationMessage()

    class ServerInboundMessageInner(val serverInboundMessage: ServerInboundMessage) : InnerApplicationMessage()

    class PlayerJoin(
        val playerName: PlayerName,
        val channel: SocketContentConverterSender<OutboundMessage>,
        /// One-shot channel to receive player's connection session ID or join error
        val completable: CompletableDeferred<Result<SessionId>>
    ) : InnerApplicationMessage()

    class PlayerDisconnect(val playerName: PlayerName, val sessionId: SessionId) :
            InnerApplicationMessage()

    class NewServerConnection(val server: SocketContentConverterSender<ServerOutboundMessage>) :
            InnerApplicationMessage()

    class Shutdown(val completable: CompletableDeferred<Unit>) : InnerApplicationMessage()
}

class GameOverThrowable(val winner: SimpleRole, val reason: GameState.GameOver.Reason) : Throwable()

suspend fun gameActor(innerApplicationChannel: Channel<InnerApplicationMessage>) {
    var gameState: GameState = GameState.Lobby(null)

    for (message in innerApplicationChannel) {
        when (message) {
            is InnerApplicationMessage.PlayerInboundMessageInner -> {
                val player = gameState.getBySessionId(message.sessionId) ?: continue

                //@formatter:off
                try {
                    gameState.handlePlayerInboundApplicationMessage(player.name, message.inboundMessage)
                } catch (e: GameOverThrowable) {
                    gameState = (gameState as GameState.GameInProgress).toGameOver(e.winner, e.reason)
                } catch (e: IllegalStateException) {
                    gameState.sendServer(ServerOutboundMessage.Error(FalseIdolsError.illegalState(player.name, e.message)))
                } catch (e: IllegalArgumentException) {
                    gameState.sendServer(ServerOutboundMessage.Error(FalseIdolsError.illegalArgument(player.name, e.message)))
                } catch (e: AssertionError) {
                    gameState.sendServer(ServerOutboundMessage.Error(FalseIdolsError.assertionError(player.name, e.message)))
                }
                //@formatter:on
            }

            is InnerApplicationMessage.ServerInboundMessageInner -> {
                val action = try {
                    gameState.handleServerInboundApplicationMessage(message.serverInboundMessage)
                } catch (e: GameOverThrowable) {
                    gameState = (gameState as GameState.GameInProgress).toGameOver(e.winner, e.reason)
                    null
                } catch (e: IllegalStateException) {
                    gameState.sendServer(ServerOutboundMessage.Error(FalseIdolsError.illegalState(message = e.message)))
                    null
                } catch (e: IllegalArgumentException) {
                    gameState.sendServer(ServerOutboundMessage.Error(FalseIdolsError.illegalArgument(message = e.message)))
                    null
                } catch (e: AssertionError) {
                    gameState.sendServer(ServerOutboundMessage.Error(FalseIdolsError.assertionError(message = e.message)))
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

            is InnerApplicationMessage.PlayerJoin -> gameState.handlePlayerJoin(message)
            is InnerApplicationMessage.PlayerDisconnect -> gameState.handlePlayerDisconnect(message)
            is InnerApplicationMessage.NewServerConnection -> {
                gameState.server = message.server
                gameState.sendServer(ServerOutboundMessage.UpdateGameState(gameState))
            }

            is InnerApplicationMessage.Shutdown -> {
                try {
                    gameState.players.forEach { player -> player.disconnectAll(OutboundMessage.Disconnect.DisconnectionReason.SERVER_SHUTDOWN) }
                    gameState.server?.close()
                    gameState.server = null
                    innerApplicationChannel.close()
                } finally {
                    message.completable.complete(Unit)
                }
                break
            }
        }

        // if it is ping, no change -> no need to resend state
        if (
            (message is InnerApplicationMessage.PlayerInboundMessageInner && message.inboundMessage is InboundMessage.Ping) ||
            (message is InnerApplicationMessage.ServerInboundMessageInner && message.serverInboundMessage is ServerInboundMessage.Ping) ||
            // Or if it's a shutdown
            (message is InnerApplicationMessage.Shutdown)
        ) {
            continue
        }

        gameState.sendServer(ServerOutboundMessage.UpdateGameState(gameState))
    }

    println("Game actor channel closed")
}

private suspend fun GameState.handlePlayerJoin(message: InnerApplicationMessage.PlayerJoin) {
    val channel = message.channel
    val existingPlayer = this[message.playerName]

    val sessionId = UUID.randomUUID()

    println("player ${message.playerName} joined with session ID $sessionId (existing: ${existingPlayer != null})")

    if (existingPlayer != null) {
        existingPlayer.connect(sessionId, channel)
        // If player already exists, send them (important) queued messages
        existingPlayer.queue.forEach { channel.send(it) }
        message.completable.complete(Result.success(sessionId))

        // And also send them their icon
        channel.send(OutboundMessage.AssignIcon(existingPlayer.icon))
        return
    }

    if (this !is GameState.Lobby) {
        message.completable.complete(Result.failure(IllegalStateException("Cannot join game in progress")))
        return
    }

    val playerIcon = PlayerIcon[this.players.size]

    val player = Player(message.playerName, playerIcon, PlayerConnection(sessionId, channel))
    this.players.add(player)

    // Send them their icon
    channel.send(OutboundMessage.AssignIcon(playerIcon))

    message.completable.complete(Result.success(sessionId))
}

private suspend fun GameState.handlePlayerDisconnect(message: InnerApplicationMessage.PlayerDisconnect) {
    val player = this[message.playerName] ?: return
    player.disconnect(message.sessionId)

    if (this is GameState.Lobby && player.channels.isEmpty()) {
        players.remove(player)
        sendServer(ServerOutboundMessage.UpdateGameState(this))
    }
}

private fun GameState.GameInProgress.generateAssignRoleMessage(player: GamePlayer): OutboundMessage.AssignRole {
    if (player.role == ComplexRole.ANGEL) {
        return OutboundMessage.AssignRole(
            role = player.role,
            demonCount = demons.size,
            isSmallGame = this.isSmallGame
        )
    }

    if (player.role == ComplexRole.DEMON) {
        return OutboundMessage.AssignRole(
            role = player.role,
            demonCount = demons.size,
            demons = demons
                .filter { it != player }
                .map { it.stripped },
            satan = satan.stripped,
            isSmallGame = this.isSmallGame
        )
    }

    assert(player.role == ComplexRole.SATAN)

    if (this.isSmallGame) {
        return OutboundMessage.AssignRole(
            role = player.role,
            demonCount = demons.size,
            demons = demons
                .filter { it != player }
                .map { it.stripped },
            isSmallGame = true
        )
    }

    return OutboundMessage.AssignRole(
        role = player.role,
        demonCount = demons.size,
        isSmallGame = false
    )
}

suspend fun GameState.GameInProgress.sendPlayerRoles() {
    players.forEach { player -> player.send(this.generateAssignRoleMessage(player)) }
}