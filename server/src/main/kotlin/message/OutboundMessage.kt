package com.cooper.message

import com.cooper.game.*

@Suppress("MemberVisibilityCanBePrivate")
sealed class OutboundMessage(val type: String) {
    /// Sent individually to each player at the start of the game
    class AssignRole(
        val role: ComplexRole,
        /// After showing role, if this player will be chief
        val isChief: Boolean,

        /// Excluding satan
        val demonCount: Int,

        /// These will only be non-null if complex role == DEMON
        val teammates: List<StrippedPlayer>? = null,
        val satan: StrippedPlayer? = null,
    ) : OutboundMessage("assign_role")

    /// Sent to a player to request an action choice
    class RequestActionChoice(
        /// Sometimes actions are forced, and early game you can only elect
        val permittedActions: List<ActionChoice> = ActionChoice.entries,
        /// List of (alive) players to choose from (excluding self),
        /// coupled with eligibility for actions. Must be eligible in player and be a permitted action.
        val players: List<ActionSupplementedPlayer>,
    ) : OutboundMessage("request_action") {
        init {
            assert(players.isNotEmpty())
        }

        class ActionSupplementedPlayer(
            name: PlayerName,
            icon: String,
            val investigatable: Boolean,
            val electable: Boolean
        ) : StrippedPlayer(name, icon) {
            companion object {
                fun fromGamePlayer(playersSize: Int, player: GamePlayer): ActionSupplementedPlayer {
                    val electable =
                        if (playersSize <= 5) !player.wasAdvisorLastRound else (!player.wasChiefLastRound && !player.wasAdvisorLastRound)
                    return ActionSupplementedPlayer(
                        player.name,
                        player.icon.iconName,
                        !player.isInvestigated,
                        electable
                    )
                }
            }
        }
    }

    /// Sent to chief when they must discard a card
    class RequestChiefCardDiscard(val cards: List<Card>) :
            OutboundMessage("request_chief_card_discard") {
        init {
            assert(cards.size == 3)
        }
    }

    /// Sent to advisor when they must choose a card to play
    class RequestAdvisorCardChoice(val cards: List<Card>) :
            OutboundMessage("request_advisor_card_choice") {
        init {
            assert(cards.size == 2)
        }
    }

    /// Sent when a player (usually chief) investigates another player.
    /// Includes simple role.
    class InvestigationResult(
        val target: StrippedPlayer,
        val simpleRole: SimpleRole,
    ) : OutboundMessage("investigation_result")

    /// Request/notification that the player has been disconnected
    class Disconnect(val reason: DisconnectionReason) : OutboundMessage("disconnect") {
        enum class DisconnectionReason {
            HOST_RESET_PLAYERS,
        }
    }

    /// Sent on first join, 'welcome' message
    class AssignIcon(val icon: PlayerIcon) : OutboundMessage("assign_icon")

    /// Utility class to serialize player name & icon
    open class StrippedPlayer(val name: String, val icon: String) {
        companion object {
            val Player.stripped get() = StrippedPlayer(this.name, this.icon.iconName)
        }
    }
}