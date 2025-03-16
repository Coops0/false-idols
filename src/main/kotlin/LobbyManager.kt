package com.cooper

import com.cooper.game.Player
import com.cooper.message.OutboundMessage
import kotlinx.coroutines.channels.Channel

typealias SessionId = String

abstract class PlayerChannelMessage

class PlayerJoinedChannelMessage(
    val playerName: String,
    val outboundMessageChannel: Channel<OutboundMessage>,
    val callback: Channel<Result<SessionId>>
) : PlayerChannelMessage()

class PlayerLeftChannelMessage(val sessionId: SessionId) : PlayerChannelMessage()

class PlayerConnection(
    val player: Player,
    sessionId: SessionId,
    outboundMessageChannel: Channel<OutboundMessage>,
    val sessions: MutableList<PlayerConnectionSession> = mutableListOf(
        PlayerConnectionSession(
            sessionId,
            outboundMessageChannel
        )
    )
) {
    class PlayerConnectionSession(
        val sessionId: String,
        val channel: Channel<OutboundMessage>,
    )
}

class LobbyManager {
    val connections = mutableListOf<PlayerConnection>()

    fun handlePlayerChannelMessage(message: PlayerChannelMessage) {
        if (message is PlayerLeftChannelMessage) {
            connections.forEach { connection ->
                connection.sessions.removeIf { it.sessionId == message.sessionId }
            }
            return
        }

        if (message !is PlayerJoinedChannelMessage) return

        val connection = this[message.playerName]
        if (connection != null) {
            connection.sessions.add(
                PlayerConnection.PlayerConnectionSession(
                    message.sessionId,
                    message.outboundMessageChannel
                )
            )
        } else {
            connections.add(
                PlayerConnection(
                    Player(message.playerName, ""), // todo picture
                    message.sessionId,
                    message.outboundMessageChannel
                )
            )
        }
    }

    operator fun get(playerName: String): PlayerConnection? {
        return connections.firstOrNull { it.player.name == playerName }
    }

    suspend fun send(playerName: String, message: OutboundMessage) {
        val connection = this[playerName] ?: return
        connection.sessions.forEach {
            it.channel.send(message)
        }
    }

    suspend fun broadcast(message: OutboundMessage) {
        connections.forEach { connection ->
            connection.sessions.forEach { session ->
                session.channel.send(message)
            }
        }
    }
}