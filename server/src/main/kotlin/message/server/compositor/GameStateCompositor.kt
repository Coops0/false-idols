package com.cooper.message.server.compositor

import com.cooper.game.CardDeck
import com.cooper.game.InnerGameState

class GameStateCompositor(
    val name: String,
    val players: List<PlayerCompositor>,
    var innerGameState: InnerGameState = InnerGameState.PostRoleGracePeriod(),
    val deck: CardDeckCompositor
) {

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
        fun fromPlayer(player: com.cooper.game.Player) = PlayerCompositor(
            player.name,
            player.icon.iconName
        )

        fun fromGamePlayer(player: com.cooper.game.GamePlayer) = PlayerCompositor(
            player.name,
            player.icon.iconName,
            player.isAlive,
            player.isInvestigated,
            player.isChief,
            player.wasChiefLastRound,
            player.wasAdvisorLastRound
        )
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
    val cause: String? = null
) {
    companion object {
        fun fromInnerGameState(innerGameState: InnerGameState): InnerGameStateCompositor  {
            when(innerGameState) {
                is InnerGameState.PostRoleGracePeriod -> return InnerGameStateCompositor("post_role_grace_period")
                is InnerGameState.AwaitingPlayerActionChoice -> {
                    return InnerGameStateCompositor(
                        "awaiting_player_action_choice",
                        forcedAction = innerGameState.forcedAction?.name,
                        cause = innerGameState.cause.name
                    )
                }
                is InnerGameState.AwaitingPlayerActionChoice -> {

                }
                is InnerGameState.AwaitingChiefCardDiscard -> {

                }
                is InnerGameState.AwaitingAdvisorCardChoice -> {

                }

                else -> throw IllegalArgumentException("Unknown inner game state type: $innerGameState")
            }
        }
    }
}