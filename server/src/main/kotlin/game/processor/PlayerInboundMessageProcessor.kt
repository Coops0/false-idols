package com.cooper.game.processor

import com.cooper.game.GameOverThrowable
import com.cooper.game.GameState
import com.cooper.game.PlayerName
import com.cooper.message.InboundMessage

@Throws(GameOverThrowable::class)
suspend fun GameState.handlePlayerInboundApplicationMessage(playerName: PlayerName, message: InboundMessage) {
    require(this is GameState.GameInProgress) { "Game must be in progress to process player inbound messages" }

    val player = this[playerName] ?: throw IllegalStateException("Player not found")
    when (message) {
        is InboundMessage.ChooseActionOnPlayer -> {
            this.handlePlayerActionChoice(player, message.action, message.target)
        }

        is InboundMessage.DiscardOneCard -> {
            this.handleChiefDiscardCard(player, message.cardId)
        }

        is InboundMessage.ChooseCard -> {
            this.handleAdvisorChooseCard(player, message.cardId)
        }

        else -> throw IllegalArgumentException("Unknown player inbound message type: $message")
    }
}