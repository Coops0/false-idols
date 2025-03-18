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
            val igs = this.innerGameState

            require(igs is InnerGameState.AwaitingElectionResolution) { "Game must be awaiting election resolution to resolve election" }

            if (message.passed) {
                this.passElection(this[igs.nominee]!!)
            } else {
                this.failElection()
            }
        }

        is ServerInboundMessage.Skip -> {
            require(this is GameState.GameInProgress) { "Game must be in progress to skip" }
            when (val igs = this.innerGameState) {
                is InnerGameState.Idle -> this.rotateChief()
                is InnerGameState.AwaitingChiefCardDiscard ->
                    this.handleChiefDiscardCard(this.chief, igs.cards.random().id)

                is InnerGameState.AwaitingAdvisorCardChoice ->
                    this.handleAdvisorChooseCard(this[igs.advisorName]!!, igs.cards.random().id)

                is InnerGameState.AwaitingPlayerActionChoice -> this.rotateChief()
                is InnerGameState.AwaitingInvestigationAnalysis -> this.rotateChief()
                else -> throw IllegalStateException("Cannot skip in state ${igs.name}")
            }
        }

        is ServerInboundMessage.GoBackToLobby -> {
            require(this is GameState.GameOver) { "Game must be in progress to go back to lobby" }
            return ServerInboundMessageProcessorAction.BACK_TO_LOBBY
        }
    }

    return null
}