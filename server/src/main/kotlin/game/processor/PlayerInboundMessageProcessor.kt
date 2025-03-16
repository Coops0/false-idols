package com.cooper.game.processor

import com.cooper.game.GameOverThrowable
import com.cooper.game.GameState
import com.cooper.game.InnerGameState
import com.cooper.game.PlayerName
import com.cooper.message.ChooseActionOnPlayerInboundMessage
import com.cooper.message.ChooseCardInboundMessage
import com.cooper.message.DiscardOneCardInboundMessage
import com.cooper.message.InboundMessage

@Throws(GameOverThrowable::class)
suspend fun GameState.handlePlayerInboundApplicationMessage(playerName: PlayerName, message: InboundMessage) {
    require(this is GameState.GameInProgress) { "Game must be in progress to process player inbound messages" }

    val player = this[playerName] ?: throw IllegalStateException("Player not found")
    when (message) {
        is ChooseActionOnPlayerInboundMessage -> {
            this.handlePlayerActionChoice(player, message.action, message.target)
        }

        is DiscardOneCardInboundMessage -> {

        }

        is ChooseCardInboundMessage -> {

        }
        else -> throw IllegalArgumentException("Unknown player inbound message type: $message")
    }
}