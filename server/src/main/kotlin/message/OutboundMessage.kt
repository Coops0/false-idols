package com.cooper.message

import com.cooper.game.*

@Suppress("MemberVisibilityCanBePrivate")
sealed class OutboundMessage(val type: String) {
    /// Sent individually to each player at the start of the game
    class AssignRole(
        val role: ComplexRole,

        /// Excluding satan
        val demonCount: Int,

        /// These will only be non-null if complex role == DEMON || isSmallGame && complex role == SATAN
        val demons: List<StrippedPlayer>? = null,
        val satan: StrippedPlayer? = null,

        /// If small game then satan knows who the demons are
        val isSmallGame: Boolean = false
    ) : OutboundMessage("assign_role")

    /// Sent to a player to request an action choice
    class RequestActionChoice(
        val action: ActionChoice,
        /// List of (alive) players to choose from (excluding self),
        /// coupled with eligibility for actions. Must be eligible in player and be a permitted action.
        val players: List<ActionSupplementedPlayer>,
    ) : OutboundMessage("request_action") {
        init {
            assert(players.isNotEmpty())
        }

        class ActionSupplementedPlayer(
            name: PlayerName,
            icon: PlayerIcon,
            val investigatable: Boolean,
            var electable: Boolean
        ) : StrippedPlayer(name, icon)
    }

    /// Sent to president when they must discard a card
    class RequestPresidentCardDiscard(val cards: List<Card>) :
            OutboundMessage("request_president_card_discard") {
        init {
            assert(cards.size == 3)
        }
    }

    /// Sent to advisor when they must choose a card to play
    class RequestAdvisorCardChoice(val cards: List<Card>, val vetoable: Boolean = false) :
            OutboundMessage("request_advisor_card_choice") {
        init {
            assert(cards.size == 2)
        }
    }

    /// Sent when a player (usually president) investigates another player.
    /// Includes simple role.
    class InvestigationResult(
        val target: StrippedPlayer,
        val simpleRole: SimpleRole,
    ) : OutboundMessage("investigation_result")

    /// Request/notification that the player has been disconnected
    class Disconnect(val reason: DisconnectionReason) : OutboundMessage("disconnect") {
        enum class DisconnectionReason {
            HOST_RESET_PLAYERS,
            SERVER_SHUTDOWN,
            KICKED,
        }
    }

    /// Sent on first join, 'welcome' message
    class AssignIcon(val icon: PlayerIcon) : OutboundMessage("assign_icon")

    /// When a fascist card action is played and policy peek is the action
    class PolicyPeek(val cards: List<Card>) : OutboundMessage("policy_peek")

    /// Utility class to serialize player name & icon
    open class StrippedPlayer(val name: String, val icon: PlayerIcon) {
        companion object {
            val Player.stripped get() = StrippedPlayer(this.name, this.icon)
        }
    }
}