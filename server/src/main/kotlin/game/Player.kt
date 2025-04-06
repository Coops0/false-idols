package com.cooper.game

import com.cooper.SocketContentConverterSender
import com.cooper.message.OutboundMessage
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import java.util.*

typealias SessionId = UUID
typealias PlayerName = String

class PlayerConnection(
    val sessionId: SessionId,
    @JsonIgnore val channel: SocketContentConverterSender<OutboundMessage>,
)

open class Player(
    val name: PlayerName,
    val icon: PlayerIcon,
    @JsonIgnore val channels: MutableList<PlayerConnection>
) {
    /// These messages are important enough to re-send on any future reconnection
    @JsonIgnore val queue = mutableListOf<OutboundMessage>()

    constructor(name: PlayerName, icon: PlayerIcon, connection: PlayerConnection) :
            this(name, icon, mutableListOf(connection))

    override operator fun equals(other: Any?): Boolean = other is Player && this.name == other.name

    override fun hashCode() = name.hashCode()

    fun connect(sessionId: SessionId, channel: SocketContentConverterSender<OutboundMessage>) {
        channels.add(PlayerConnection(sessionId, channel))
    }

    suspend fun disconnectAll(reason: OutboundMessage.Disconnect.DisconnectionReason) {
        channels.forEach { it.channel.send(OutboundMessage.Disconnect(reason)) }
        channels.clear()
    }

    fun disconnect(sessionId: SessionId) {
        channels.removeIf { it.sessionId == sessionId }
    }

    suspend fun send(message: OutboundMessage, significant: Boolean = false) {
        if (significant) {
            queue.add(message)
        }

        channels.forEach { it.channel.send(message) }
    }

    fun clearQueue() {
        queue.clear()
    }
}

class GamePlayer(player: Player, val role: ComplexRole) :
        Player(player.name, player.icon, player.channels) {
    var isAlive = true
    var isInvestigated = false

    var isPresident = false

    var wasPresidentLastRound = false
    var wasAdvisorLastRound = false
}

enum class PlayerIcon(val iconName: String) {
    BEAR("bear"),
    REINDEER("reindeer"),
    GERBIL("gerbil"),
    RABBIT("rabbit"),
    FOX("fox"),
    PIG("pig"),
    MOUSE("mouse"),
    DOG("dog"),
    PANDA("panda"),
    KOALA("koala");

    companion object {
        operator fun get(index: Int) = entries[index % entries.size]
    }
}

class PlayerIconSerializer : JsonSerializer<PlayerIcon>() {
    override fun serialize(value: PlayerIcon, gen: JsonGenerator?, serializers: SerializerProvider?) {
        gen?.writeString(value.iconName)
    }
}