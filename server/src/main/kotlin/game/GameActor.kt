package com.cooper.game

import com.cooper.SocketContentConverterSender
import com.cooper.game.processor.ServerInboundMessageProcessorAction
import com.cooper.game.processor.handlePlayerInboundApplicationMessage
import com.cooper.game.processor.handleServerInboundApplicationMessage
import com.cooper.message.InboundMessage
import com.cooper.message.OutboundMessage
import com.cooper.message.server.ServerInboundMessage
import com.cooper.message.server.ServerOutboundMessage
import com.cooper.message.server.UpdateGameStateServerOutboundMessage
import io.viascom.nanoid.NanoId
import kotlinx.coroutines.channels.Channel

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

val globalInnerApplicationChannel = Channel<InnerApplicationMessage>()

class GameOverThrowable(val newState: GameState.GameOver) : Throwable("game over")

suspend fun launchGameActor(server: SocketContentConverterSender<ServerOutboundMessage>) {
    var gameState: GameState = GameState.Lobby(server)

    for (message in globalInnerApplicationChannel) {
        when (message) {
            is PlayerInboundApplicationMessage -> {
                val player = gameState.getBySessionId(message.sessionId) ?: continue

                try {
                    gameState.handlePlayerInboundApplicationMessage(player.name, message.inboundMessage)
                } catch (e: GameOverThrowable) {
                    gameState = e.newState
                } catch (e: IllegalStateException) {
                    println("Failed to process player message ${e.message}")
                }
            }

            is ServerInboundApplicationMessage -> {
                val action = gameState.handleServerInboundApplicationMessage(message.serverInboundMessage)
                when (action) {
                    ServerInboundMessageProcessorAction.START_GAME -> {
                        gameState = (gameState as GameState.Lobby).toGameInProgress()
                    }

                    ServerInboundMessageProcessorAction.BACK_TO_LOBBY -> {
                        gameState = (gameState as GameState.GameOver).toLobby()
                    }

                    null -> {}
                }
            }

            is PlayerJoinInnerApplicationMessage -> gameState.handlePlayerJoin(message)
            is PlayerDisconnectInnerApplicationMessage -> gameState.handlePlayerDisconnect(message)
        }

        gameState.sendServer(UpdateGameStateServerOutboundMessage(gameState))
    }
}

private suspend fun GameState.handlePlayerJoin(message: PlayerJoinInnerApplicationMessage) {
    val existingPlayer = this[message.playerName]

    val sessionId = NanoId.generate()

    if (existingPlayer != null) {
        existingPlayer.connect(sessionId, message.channel)
        message.callback.send(Result.success(sessionId))
        return
    }

    if (this !is GameState.Lobby) {
        message.callback.send(Result.failure(IllegalStateException("Cannot join game in progress")))
        return
    }

    val playerIcon = PlayerIcon[this.players.size]

    val player = Player(message.playerName, playerIcon, PlayerConnection(sessionId, message.channel))
    this.players.add(player)

    message.callback.send(Result.success(sessionId))
}

private suspend fun GameState.handlePlayerDisconnect(message: PlayerDisconnectInnerApplicationMessage) {
    val player = this[message.playerName] ?: return
    player.disconnect(message.sessionId)

    if (this is GameState.Lobby && player.channels.isEmpty()) {
        players.remove(player)
        sendServer(UpdateGameStateServerOutboundMessage(this))
    }
}