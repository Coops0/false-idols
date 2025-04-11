package com.cooper.game.processor

import com.cooper.game.EndGameThrowable
import com.cooper.game.GameOverThrowable
import com.cooper.game.GameState
import com.cooper.game.InnerGameState
import com.cooper.message.OutboundMessage
import com.cooper.message.server.ServerInboundMessage
import com.cooper.message.server.ServerOutboundMessage

enum class ServerInboundMessageProcessorAction {
    START_GAME,
    BACK_TO_LOBBY
}

@Throws(GameOverThrowable::class, EndGameThrowable::class)
suspend fun GameState.handleServerInboundApplicationMessage(message: ServerInboundMessage): ServerInboundMessageProcessorAction? {
    when (message) {
        is ServerInboundMessage.ResetPlayers -> {
            require(this is GameState.Lobby) { "Game must be in lobby to reset players" }
            this.players.forEach { player ->
                player.disconnectAll(OutboundMessage.Disconnect.DisconnectionReason.HOST_RESET_PLAYERS)
            }
        }

        is ServerInboundMessage.Kick -> {
            require(this is GameState.Lobby) { "Must be in lobby to kick a player" }

            val player = this.players.firstOrNull { it.name == message.playerName }
                ?: throw IllegalArgumentException("Player not found")

            player.disconnectAll(OutboundMessage.Disconnect.DisconnectionReason.KICKED)
            this.players.remove(player)
        }

        is ServerInboundMessage.StartGame -> {
            require(this is GameState.Lobby) { "Game must be in lobby to start" }
            require(this.players.size >= 4) { "Must have at least 4 players to start a game" }
            return ServerInboundMessageProcessorAction.START_GAME
        }

        is ServerInboundMessage.ResolveElection -> {
            require(this is GameState.GameInProgress) { "Game must be in progress to resolve election" }
            when (val igs = this.innerGameState) {
                is InnerGameState.AwaitingAdvisorElectionResolution -> {
                    if (message.passed) {
                        this.passAdvisorElection(this[igs.nominee]!!)
                    } else {
                        this.failAdvisorElection()
                    }
                }

                is InnerGameState.AwaitingPresidentElectionResolution -> {
                    if (message.passed) {
                        this.previousPresidentIndex = this.players.indexOf(this.president)
                        this.players.forEach { player -> player.wasPresidentLastRound = false }
                        this.president.wasPresidentLastRound = true
                        this.president.isPresident = false

                        this[igs.nominee]!!.isPresident = true
                        this.requestPresidentAction()
                    } else {
                        this.rotatePresident()
                    }
                }

                else -> throw IllegalStateException("Game must be awaiting election resolution to resolve")
            }
        }

        is ServerInboundMessage.Skip -> {
            require(this is GameState.GameInProgress) { "Game must be in progress to skip" }
            when (val igs = this.innerGameState) {
                is InnerGameState.Idle -> this.rotatePresident()
                is InnerGameState.AwaitingPresidentCardDiscard ->
                    this.handlePresidentDiscardCard(this.president, igs.cards.random().id)

                is InnerGameState.AwaitingAdvisorCardChoice ->
                    this.handleAdvisorChooseCard(this[igs.advisorName]!!, igs.cards.random().id)

                is InnerGameState.AwaitingPresidentActionChoice -> this.rotatePresident()
                is InnerGameState.AwaitingInvestigationAnalysis -> this.rotatePresident()
                else -> throw IllegalStateException("Cannot skip in state ${igs.type}")
            }
        }

        is ServerInboundMessage.GoBackToLobby -> {
            require(this is GameState.GameOver) { "Game must be in progress to go back to lobby" }
            return ServerInboundMessageProcessorAction.BACK_TO_LOBBY
        }

        is ServerInboundMessage.Veto -> {
            require(this is GameState.GameInProgress) { "Game must be in progress to veto" }
            require(this.innerGameState is InnerGameState.AwaitingAdvisorCardChoice) { "Must be awaiting advisor choice to veto" }
            this.rotatePresident()
        }

        is ServerInboundMessage.Ping -> {
            if (message.requestState) {
                this.sendServer(ServerOutboundMessage.UpdateGameState(this))
            }
        }

        is ServerInboundMessage.EndGame -> {
            throw EndGameThrowable()
        }
    }

    return null
}