package com.cooper.game

import com.cooper.SocketContentConverterSender
import com.cooper.message.OutboundMessage

typealias SessionId = String
typealias PlayerName = String

class PlayerConnection(
    val sessionId: SessionId,
    val channel: SocketContentConverterSender,
)

open class Player(
    val name: PlayerName,
    val icon: PlayerIcon,
    val channels: MutableList<PlayerConnection>
) {
    constructor(name: PlayerName, icon: PlayerIcon, connections: PlayerConnection) : this(
        name,
        icon,
        mutableListOf(connections)
    )

    fun connect(sessionId: SessionId, channel: SocketContentConverterSender) {
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
        Player(player.name, player.icon, player.channels) {
    var isAlive = true
    var isInvestigated = false

    var isChief = false

    var wasChiefLastRound = false
    var wasAdvisorLastRound = false

    val simpleRole: SimpleRole
        get() = SimpleRole.fromComplexRole(role)
}

enum class PlayerIcon(val iconName: String) {
    ZEBRA("zebra"),
    CAT("cat"),
    MOUSE("mouse"),
    FOX("fox"),
    RACOON("racoon"),
    RABBIT("rabbit"),
    DOG("dog"),
    PIG("pig"),
    BEAR("bear"),
    LION("lion");


    companion object {
        fun fromIndex(index: Int) = entries[index]
    }
}