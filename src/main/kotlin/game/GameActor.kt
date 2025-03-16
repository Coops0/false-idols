package com.cooper.game

import com.cooper.message.InboundMessage
import com.cooper.message.OutboundMessage
import com.cooper.message.server.ServerInboundMessage
import com.cooper.message.server.ServerOutboundMessage
import kotlinx.coroutines.channels.Channel

abstract class InnerApplicationMessage

class PlayerInboundApplicationMessage(val sessionId: SessionId, val inboundMessage: InboundMessage) :
        InnerApplicationMessage()

class ServerInboundApplicationMessage(val serverInboundMessage: ServerInboundMessage) : InnerApplicationMessage()

class PlayerJoinInnerApplicationMessage(
    val playerName: String,
    val channel: Channel<OutboundMessage>,
    /// One-shot channel to receive player's connection session ID or join error
    val callback: Channel<Result<SessionId>>
) : InnerApplicationMessage()

class PlayerDisconnectInnerApplicationMessage(val sessionId: SessionId) : InnerApplicationMessage()

val globalInnerApplicationChannel = Channel<InnerApplicationMessage>()

suspend fun launchGameActor(server: Channel<ServerOutboundMessage>) {
    var gameState: GameState<*> = LobbyGameState(server)

    for (message in globalInnerApplicationChannel) {
        when (message) {
            is PlayerInboundApplicationMessage -> {
                gameState.handlePlayerInboundMessage(message.sessionId, message.inboundMessage)
            }

            is ServerInboundApplicationMessage -> {
                gameState.handleServerInboundMessage(message.serverInboundMessage)
            }

            is PlayerJoinInnerApplicationMessage -> gameState.handlePlayerJoin(message)
            is PlayerDisconnectInnerApplicationMessage -> {
                gameState.handlePlayerDisconnect(message.sessionId)
            }
        }
    }
}

private suspend fun GameState<*>.handlePlayerJoin(message: PlayerJoinInnerApplicationMessage) {
    val existingPlayer = this.players.firstOrNull { it.name == message.playerName }

    val sessionId = "TODO" // todo generate

    if (existingPlayer != null) {
        existingPlayer.connect(sessionId, message.channel)
        return
    }

    if (this !is LobbyGameState) {
        message.callback.send(Result.failure(IllegalStateException("Cannot join game in progress")))
        return
    }

    val player = Player(message.playerName, "TODO", PlayerConnection(sessionId, message.channel)) // todo avatar
    this.players.add(player)

    message.callback.send(Result.success(sessionId))
}