package com.cooper.game

import com.cooper.SocketContentConverterSender
import com.cooper.message.InboundMessage
import com.cooper.message.server.ServerInboundMessage
import com.cooper.message.server.UpdatePlayersServerOutboundMessage
import io.viascom.nanoid.NanoId
import kotlinx.coroutines.channels.Channel

abstract class InnerApplicationMessage

class PlayerInboundApplicationMessage(val sessionId: SessionId, val inboundMessage: InboundMessage) :
        InnerApplicationMessage()

class ServerInboundApplicationMessage(val serverInboundMessage: ServerInboundMessage) : InnerApplicationMessage()

class PlayerJoinInnerApplicationMessage(
    val playerName: String,
    val channel: SocketContentConverterSender,
    /// One-shot channel to receive player's connection session ID or join error
    val callback: Channel<Result<SessionId>>
) : InnerApplicationMessage()

class PlayerDisconnectInnerApplicationMessage(val playerName: PlayerName, val sessionId: SessionId) :
        InnerApplicationMessage()

val globalInnerApplicationChannel = Channel<InnerApplicationMessage>()

class GameOverThrowable(val newState: GameState.GameOver) : Throwable()

suspend fun launchGameActor(server: SocketContentConverterSender) {
    var gameState: GameState = GameState.Lobby(server)

    for (message in globalInnerApplicationChannel) {
        when (message) {
            is PlayerInboundApplicationMessage -> {
                try {
//                gameState.handlePlayerInboundMessage(message.sessionId, message.inboundMessage)
                } catch (e: GameOverThrowable) {
                    gameState = e.newState
                }
            }

            is ServerInboundApplicationMessage -> {
//                gameState.handleServerInboundMessage(message.serverInboundMessage)
            }

            is PlayerJoinInnerApplicationMessage -> gameState.handlePlayerJoin(message)
            is PlayerDisconnectInnerApplicationMessage -> gameState.handlePlayerDisconnect(message)
        }
    }
}

private suspend fun GameState.handlePlayerJoin(message: PlayerJoinInnerApplicationMessage) {
    val existingPlayer = this.players.firstOrNull { it.name == message.playerName }

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

    val playerIcon = PlayerIcon.fromIndex(this.players.size)

    val player = Player(message.playerName, playerIcon, PlayerConnection(sessionId, message.channel))
    this.players.add(player)

    message.callback.send(Result.success(sessionId))
}

private suspend fun GameState.handlePlayerDisconnect(message: PlayerDisconnectInnerApplicationMessage) {
    val player = this[message.playerName] ?: return
    player.disconnect(message.sessionId)

    if (this is GameState.Lobby && player.channels.isEmpty()) {
        players.remove(player)
        sendServer(UpdatePlayersServerOutboundMessage(players))
    }
}