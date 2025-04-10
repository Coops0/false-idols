package com.cooper.game

import com.cooper.message.OutboundMessage
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import kotlinx.coroutines.flow.MutableSharedFlow
import java.util.*

typealias SessionId = UUID
typealias PlayerName = String

class PlayerConnection(
    val sessionId: SessionId,
    @JsonIgnore val flow: MutableSharedFlow<OutboundMessage>,
)

open class Player(
    val name: PlayerName,
    val icon: PlayerIcon,
    @JsonIgnore val flows: MutableList<PlayerConnection>
) {
    constructor(name: PlayerName, icon: PlayerIcon, connection: PlayerConnection) :
            this(name, icon, mutableListOf(connection))

    override operator fun equals(other: Any?): Boolean = other is Player && this.name == other.name

    override fun hashCode() = name.hashCode()

    fun connect(sessionId: SessionId, flow: MutableSharedFlow<OutboundMessage>) {
        flows.add(PlayerConnection(sessionId, flow))
    }

    suspend fun disconnectAll(reason: OutboundMessage.Disconnect.DisconnectionReason) {
        flows.forEach { it.flow.emit(OutboundMessage.Disconnect(reason)) }
        flows.clear()
    }

    fun disconnect(sessionId: SessionId) {
        flows.removeIf { it.sessionId == sessionId }
    }

    suspend fun emit(message: OutboundMessage) {
        flows.forEach { it.flow.emit(message) }
    }
}

class GamePlayer(player: Player, val role: ComplexRole) :
        Player(player.name, player.icon, player.flows) {
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