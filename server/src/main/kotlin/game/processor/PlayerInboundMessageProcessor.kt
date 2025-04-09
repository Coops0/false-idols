package com.cooper.game.processor

import com.cooper.game.GameOverThrowable
import com.cooper.game.GameState
import com.cooper.game.PlayerName
import com.cooper.message.InboundMessage
import com.cooper.message.OutboundMessage

@Throws(GameOverThrowable::class)
suspend fun GameState.handlePlayerInboundApplicationMessage(playerName: PlayerName, message: InboundMessage) {
    if (message is InboundMessage.Ping) {
        val player = this[playerName]!!
        if (message.requestIcon) {
            this[playerName]?.emit(OutboundMessage.AssignIcon(player.icon))
        }

        if (message.isIdle && player.queue.isNotEmpty()) {
            player.queue.forEach { player.emit(it) }
        }
        return
    }

    require(this is GameState.GameInProgress) { "Game must be in progress to process player inbound messages" }

    val player = this[playerName]!!
    when (message) {
        is InboundMessage.ChooseActionOnPlayer -> this.handlePlayerActionChoice(player, message.target)
        is InboundMessage.DiscardOneCard -> this.handlePresidentDiscardCard(player, message.cardId)
        is InboundMessage.ChooseCard -> this.handleAdvisorChooseCard(player, message.cardId)
        else -> throw IllegalArgumentException("Unknown message type: $message")
    }
}