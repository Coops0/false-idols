package com.cooper.message

import com.cooper.game.*

abstract class OutboundMessage(val type: String)

/// Sent individually to each player at the start of the game
class AssignRoleOutboundMessage(
    val complexRole: ComplexRole,
    /// After showing role, if this player will be chief
    val isChief: Boolean,

    /// These will only be non-null if complex role == DEMON
    val teammates: Array<StrippedPlayer>? = null,
    val satan: StrippedPlayer? = null
) : OutboundMessage("assign_role")

/// Sent to a player to request an action choice
class RequestActionChoiceOutboundMessage(
    /// Only during an event in which the player must do one action
    val forcedAction: ActionChoice? = null,
    /// List of (alive) players to choose from (excluding self),
    /// Coupled with eligibility for actions
    val players: Array<ActionSupplementedPlayer>
) : OutboundMessage("request_action") {
    class ActionSupplementedPlayer(
        name: PlayerName,
        icon: String,
        val investigatable: Boolean,
        val electable: Boolean
    ) : StrippedPlayer(name, icon) {
        companion object {
            fun fromGamePlayer(player: GamePlayer) = ActionSupplementedPlayer(
                player.name,
                player.icon.iconName,
                !player.isInvestigated,
                !player.wasChiefLastRound && !player.wasAdvisorLastRound
            )
        }
    }
}

/// Sent to chief when they must discard a card
class RequestChiefCardDiscardOutboundMessage(val cards: Array<Card>) :
        OutboundMessage("request_chief_card_discard") {
    init {
        assert(cards.size == 3)
    }
}

/// Sent to advisor when they must choose a card to play
class RequestAdvisorCardChoiceOutboundMessage(val cards: Array<Card>) :
        OutboundMessage("request_advisor_card_choice") {
    init {
        assert(cards.size == 2)
    }
}

/// Sent when a player (usually chief) investigates another player.
/// Includes simple role.
class InvestigationResultOutboundMessage(
    val target: StrippedPlayer,
    val simpleRole: SimpleRole,
) : OutboundMessage("investigation_result") {
    constructor(target: StrippedPlayer, complexRole: ComplexRole) : this(
        target,
        SimpleRole.fromComplexRole(complexRole)
    )
}

/// Request/notification that the player has been disconnected
class DisconnectOutboundMessage(val reason: DisconnectionReason) : OutboundMessage("disconnect") {
    enum class DisconnectionReason {
        HOST_RESET_PLAYERS,
    }
}

/// Utility class to serialize player name & icon
open class StrippedPlayer(val name: String, val icon: String) {
    companion object {
        fun fromPlayer(player: Player) = StrippedPlayer(player.name, player.icon.iconName)

        fun Player.toStrippedPlayer() = fromPlayer(this)
    }
}