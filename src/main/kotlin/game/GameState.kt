package com.cooper.game

abstract class GameState(
    val name: String,
    open val players: MutableList<Player>
)

class LobbyGameState : GameState("lobby", mutableListOf())

class GameInProgressGameState(
    override val players: MutableList<Player>,
    val innerGameState: InnerGameState,

    val chief: PlayerName,

    val lastChief: PlayerName? = null,
    val lastAdvisor: PlayerName? = null,

    val deck: CardDeck,
    val points: Int = 0,
    val absolutePoints: Int = 0,

    val failedElections: Int = 0,

    val satan: PlayerName,
    val demons: List<PlayerName>,

    val investigated: List<PlayerName> = mutableListOf(),
    val killed: List<PlayerName> = mutableListOf(),
) : GameState("game_in_progress")

class GameOverGameState(
    val winner: SimpleRole,
    val satan: PlayerName,
    val demons: List<PlayerName>,
    val cause: GameOverCause
) : GameState("game_over") {
    enum class GameOverCause {
        SATAN_KILLED,
        SATAN_ELECTED,
        POINTS_REACHED,
        ALL_ANGELS_DEAD,
    }
}

abstract class InnerGameState(val name: String)

class AwaitingPlayerActionChoiceInnerGameState(
    val forcedAction: ActionChoice? = null,
    val cause: PlayerActionChoiceCause = PlayerActionChoiceCause.NORMAL_CHIEF,
) :
        InnerGameState("awaiting_chiefial_action_choice") {
    enum class PlayerActionChoiceCause {
        NORMAL_CHIEF,
        FORCED_CHIEF
    }
}

class AwaitingChiefialCardDiscardInnerGameState(val cards: Array<Card>) :
        InnerGameState("awaiting_chiefial_card_discard")

class AwaitingAdvisorCardChoiceInnerGameState(val cards: Array<Card>) :
        InnerGameState("awaiting_advisor_card_choice")

class AwaitingElectionOutcomeInnerGameState(val nominee: PlayerName) :
        InnerGameState("awaiting_election_outcome")