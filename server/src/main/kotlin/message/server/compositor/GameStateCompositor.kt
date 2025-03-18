package com.cooper.message.server.compositor

import com.cooper.game.*
import com.cooper.game.GameState.GameOverReason
import com.cooper.message.StrippedPlayer

class GameStateCompositor(
    val name: String,
    val players: List<PlayerCompositor>,

    // in game
    val innerGameState: InnerGameStateCompositor? = null,
    val deck: CardDeckCompositor? = null,
    var failedElections: Int? = null,

    // game over
    val winner: SimpleRole? = null,
    val satan: StrippedPlayer? = null,
    val demons: List<StrippedPlayer>? = null,
    val reason: GameOverReason? = null,
) {
    companion object {
        fun fromGameState(gameState: GameState): GameStateCompositor {
            return when (gameState) {
                is GameState.Lobby -> GameStateCompositor(
                    gameState.name,
                    gameState.players.map(PlayerCompositor.Companion::fromPlayer),
                )

                is GameState.GameInProgress -> GameStateCompositor(
                    gameState.name,
                    gameState.players.map(PlayerCompositor.Companion::fromPlayer),
                    InnerGameStateCompositor.fromInnerGameState(gameState.innerGameState),
                    CardDeckCompositor.fromCardDeck(gameState.deck)
                )

                is GameState.GameOver -> GameStateCompositor(
                    gameState.name,
                    gameState.players.map(PlayerCompositor.Companion::fromPlayer),
                    winner = gameState.winner,
                    satan = gameState.satan,
                    demons = gameState.demons,
                    reason = gameState.reason
                )
            }
        }
    }
}

class PlayerCompositor(
    val name: String,
    val icon: String,
    val isAlive: Boolean? = null,
    val isInvestigated: Boolean? = null,
    val isChief: Boolean? = null,
    val wasChiefLastRound: Boolean? = null,
    val wasAdvisorLastRound: Boolean? = null
) {
    companion object {
        fun fromPlayer(player: Player): PlayerCompositor {
            return if (player is GamePlayer) PlayerCompositor(
                player.name,
                player.icon.iconName,
                player.isAlive,
                player.isInvestigated,
                player.isChief,
                player.wasChiefLastRound,
                player.wasAdvisorLastRound
            ) else PlayerCompositor(
                player.name,
                player.icon.iconName
            )
        }
    }
}

class CardDeckCompositor(
    val points: Int,
    val absolutePoints: Int
) {
    companion object {
        fun fromCardDeck(deck: CardDeck) = CardDeckCompositor(
            deck.points,
            deck.absolutePoints
        )
    }
}

class InnerGameStateCompositor(
    val type: String,
    val forcedAction: String? = null,
    val cause: String? = null,
    val advisorName: String? = null
) {
    companion object {
        fun fromInnerGameState(innerGameState: InnerGameState): InnerGameStateCompositor {
            when (innerGameState) {
                is InnerGameState.PostRoleGracePeriod -> return InnerGameStateCompositor("post_role_grace_period")
                is InnerGameState.AwaitingPlayerActionChoice -> return InnerGameStateCompositor(
                    "awaiting_player_action_choice",
                    forcedAction = innerGameState.forcedAction?.name,
                    cause = innerGameState.cause.name
                )

                is InnerGameState.AwaitingChiefCardDiscard -> return InnerGameStateCompositor(
                    "awaiting_chief_card_discard",
                    cause = innerGameState.advisorName,
                    advisorName = innerGameState.advisorName
                )

                is InnerGameState.AwaitingAdvisorCardChoice -> return InnerGameStateCompositor(
                    "awaiting_advisor_card_choice",
                    advisorName = innerGameState.advisorName,
                )

                is InnerGameState.AwaitingElectionResolution -> return InnerGameStateCompositor(
                    "awaiting_election_resolution",
                    advisorName = innerGameState.nominee
                )
            }
        }
    }
}