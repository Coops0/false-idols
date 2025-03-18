package com.cooper.game.processor

import com.cooper.game.GameState
import com.cooper.game.InnerGameState
import com.cooper.message.OutboundMessage
import com.cooper.message.server.ServerInboundMessage

enum class ServerInboundMessageProcessorAction {
    START_GAME,
    BACK_TO_LOBBY
}

suspend fun GameState.handleServerInboundApplicationMessage(message: ServerInboundMessage): ServerInboundMessageProcessorAction? {
    when (message) {
        is ServerInboundMessage.ResetPlayers -> {
            require(this is GameState.Lobby) { "Game must be in lobby to reset players" }
            this.players.forEach { player ->
                player.disconnectAll(OutboundMessage.Disconnect.DisconnectionReason.HOST_RESET_PLAYERS)
            }
        }

        is ServerInboundMessage.StartGame -> {
            require(this is GameState.Lobby) { "Game must be in lobby to start" }
            return ServerInboundMessageProcessorAction.START_GAME
        }

        is ServerInboundMessage.ResolveElection -> {
            require(this is GameState.GameInProgress) { "Game must be in progress to resolve election" }
            require(this.innerGameState is InnerGameState.AwaitingElectionResolution) { "Game must be awaiting election resolution to resolve election" }
            val player = this[(this.innerGameState as InnerGameState.AwaitingElectionResolution).nominee]
                ?: throw IllegalStateException("Nominee not found")

            if (!message.passed) {
                this.failedElections++
                this.rotateChief()
                return null
            }

            val cards = this.deck.pickAndTakeThree()
            this.innerGameState = InnerGameState.AwaitingChiefCardDiscard(cards, player.name)

            this.chief.send(OutboundMessage.RequestChiefCardDiscard(cards))
        }

        is ServerInboundMessage.Skip -> {
            require(this is GameState.GameInProgress) { "Game must be in progress to skip" }
            when (this.innerGameState) {
                is InnerGameState.PostRoleGracePeriod -> TODO()
                is InnerGameState.AwaitingChiefCardDiscard -> TODO()
                is InnerGameState.AwaitingAdvisorCardChoice -> TODO()
                else -> return null
            }
        }

        is ServerInboundMessage.GoBackToLobby -> {
            require(this is GameState.GameOver) { "Game must be in progress to go back to lobby" }
            return ServerInboundMessageProcessorAction.BACK_TO_LOBBY
        }

        else -> throw IllegalArgumentException("Unknown server inbound message type: $message")
    }

    return null
}