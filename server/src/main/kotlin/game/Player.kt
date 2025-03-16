package com.cooper.game

import com.cooper.OutgoingSerializerHelper
import com.cooper.message.OutboundMessage

typealias SessionId = String
typealias PlayerName = String

class PlayerConnection(
    val sessionId: SessionId,
    val channel: OutgoingSerializerHelper,
)

open class Player(
    val name: PlayerName,
    val picture: String, // TODO
    val channels: MutableList<PlayerConnection>
) {
    constructor(name: PlayerName, picture: String, connections: PlayerConnection) : this(
        name,
        picture,
        mutableListOf(connections)
    )

    fun connect(sessionId: SessionId, channel: OutgoingSerializerHelper) {
        channels.add(PlayerConnection(sessionId, channel))
    }

    fun disconnect(sessionId: SessionId) {
        channels.removeIf { it.sessionId == sessionId }
    }

    suspend fun send(message: OutboundMessage) {
        channels.forEach { it.channel.send(message) }
    }
}

class GamePlayer(player: Player, val role: ComplexRole) :
        Player(player.name, player.picture, player.channels) {
    var isAlive = true
    var isInvestigated = false

    var isChief = false

    var wasChiefLastRound = false
    var wasAdvisorLastRound = false

    val simpleRole: SimpleRole
        get() = SimpleRole.fromComplexRole(role)
}