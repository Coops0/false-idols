package com.cooper.game.processor

import com.cooper.game.*
import com.cooper.game.SimpleRole.Companion.simple
import com.cooper.message.OutboundMessage
import com.cooper.message.OutboundMessage.RequestActionChoice.ActionSupplementedPlayer
import com.cooper.message.OutboundMessage.StrippedPlayer.Companion.stripped

suspend fun GameState.GameInProgress.rotatePresident() {
    val igs = this.innerGameState

    val newPresident = if (igs is InnerGameState.AwaitingRoleConfirmations) {
        // President has already been randomly assigned
        this.president
    } else {
        var currentPresidentIndex = this.alive.indexOf(this.president)

        // President was elected, so we need to return back to the previous order
        if (this.presidentialElectionPreviousPresidentIndex != -1) {
            currentPresidentIndex = this.presidentialElectionPreviousPresidentIndex
            this.presidentialElectionPreviousPresidentIndex = -1
        }

        if (currentPresidentIndex == -1) {
            // President has been killed (shouldn't be possible)
            currentPresidentIndex = this.players
                // Find who was next in line to be president
                .filter { player -> player.isAlive || player == this.president }
                .indexOf(this.president)
        }

        // Find president or wrap around to the first alive player
        this.alive[(currentPresidentIndex + 1) % this.alive.size]
    }

    if (igs !is InnerGameState.AwaitingRoleConfirmations) {
        this.players.forEach { player -> player.wasPresidentLastRound = false }
        this.president.wasPresidentLastRound = true
        this.president.isPresident = false

        newPresident.isPresident = true
    }

    this.requestPresidentAction()
}

suspend fun GameState.GameInProgress.requestPresidentAction() {
    this.innerGameState = InnerGameState.AwaitingPresidentActionChoice(
        action = ActionChoice.NOMINATE
    )

    val actionablePlayers = this.alive.filter { it != this.president }
        .map(this::playerToActionSupplementedPlayer)

    this.president.emit(
        OutboundMessage.RequestActionChoice(
            action = ActionChoice.NOMINATE,
            players = actionablePlayers,
        )
    )
}

@Throws(GameOverThrowable::class)
fun GameState.GameInProgress.checkGameOverConditions() {
    if (deck.negativeCardsPlayed >= NEGATIVE_CARD_COUNT_SATAN_ELECTION_WIN) {
        val igs = innerGameState
        if (igs is InnerGameState.AwaitingPresidentCardDiscard && igs.advisorName == satan.name) {
            throw GameOverThrowable(
                winner = SimpleRole.DEMON,
                reason = GameState.GameOver.Reason.SATAN_ELECTED_ADVISOR_LATE_GAME
            )
        }
    }

    if (deck.positiveCardsPlayed >= POSITIVE_CARD_COUNT_WIN) {
        throw GameOverThrowable(
            winner = SimpleRole.ANGEL,
            reason = GameState.GameOver.Reason.POSITIVE_THRESHOLD_REACHED
        )
    }

    if (deck.negativeCardsPlayed >= NEGATIVE_CARD_COUNT_WIN) {
        throw GameOverThrowable(
            winner = SimpleRole.DEMON,
            reason = GameState.GameOver.Reason.NEGATIVE_THRESHOLD_REACHED
        )
    }

    if (angels.none(GamePlayer::isAlive)) {
        throw GameOverThrowable(winner = SimpleRole.DEMON, reason = GameState.GameOver.Reason.ALL_ANGELS_DEAD)
    }

    if (!satan.isAlive) {
        throw GameOverThrowable(winner = SimpleRole.ANGEL, reason = GameState.GameOver.Reason.SATAN_KILLED)
    }
}

fun GameState.GameInProgress.idle() {
    this.innerGameState = InnerGameState.Idle()
}

suspend fun GameState.GameInProgress.handlePlayerActionChoice(aggressor: GamePlayer, targetName: PlayerName) {
    val igs = this.innerGameState

    require(igs is InnerGameState.AwaitingPresidentActionChoice) { "Game must be awaiting player action choice to choose action" }
    require(this.president == aggressor) { "Player must be president to execute action" }

    val action = igs.action

    val target = this[targetName] ?: throw IllegalArgumentException("Target not found")
    require(target.isAlive) { "Target must be alive" }
    require(target != aggressor) { "Cannot execute action upon self" }

    when (action) {
        ActionChoice.INVESTIGATE -> {
            require(!target.isInvestigated) { "Target must not have already been investigated" }

            target.isInvestigated = true

            this.innerGameState = InnerGameState.AwaitingInvestigationAnalysis(targetName)
            aggressor.emit(OutboundMessage.InvestigationResult(target.stripped, target.role.simple))
        }

        ActionChoice.KILL -> {
            target.isAlive = false
            if (target.role == ComplexRole.SATAN) {
                throw GameOverThrowable(winner = SimpleRole.ANGEL, reason = GameState.GameOver.Reason.SATAN_KILLED)
            }

            this.checkGameOverConditions()
            this.idle()
        }

        ActionChoice.NOMINATE -> {
            if (this.isSmallGame) {
                require(!target.wasAdvisorLastRound) { "Target must not have been advisor last round" }
            } else {
                require(!target.wasPresidentLastRound && !target.wasAdvisorLastRound) { "Target must not have been president or advisor last round" }
            }

            this.innerGameState = InnerGameState.AwaitingAdvisorElectionResolution(
                nominee = target.name
            )

            // now the host will decide outcome
        }

        ActionChoice.NOMINATE_PRESIDENT -> {
            this.innerGameState = InnerGameState.AwaitingPresidentElectionResolution(
                nominee = target.name
            )
        }
    }

    this.players.forEach { player -> player.wasAdvisorLastRound = false }
}

suspend fun GameState.GameInProgress.handlePresidentDiscardCard(player: GamePlayer, cardId: Int) {
    val igs = this.innerGameState
    require(igs is InnerGameState.AwaitingPresidentCardDiscard) { "Game must be awaiting president card discard to discard card" }
    require(this.president == player)

    val cards = igs.cards
    val advisorName = igs.advisorName

    val newCards = cards.filter { it.id != cardId }
    require(newCards.size != cards.size) { "Card ID not found" }

    this.innerGameState = InnerGameState.AwaitingAdvisorCardChoice(newCards, advisorName)

    val advisor = this[advisorName]!!
    advisor.emit(
        OutboundMessage.RequestAdvisorCardChoice(
            cards = newCards,
            vetoable = this.deck.negativeCardsPlayed >= NEGATIVE_CARD_COUNT_VETO
        )
    )
}

@Throws(GameOverThrowable::class)
suspend fun GameState.GameInProgress.handleAdvisorChooseCard(player: GamePlayer, cardId: CardId) {
    val igs = this.innerGameState
    require(igs is InnerGameState.AwaitingAdvisorCardChoice) { "Game must be awaiting advisor card choice to choose card" }

    require(igs.advisorName == player.name) { "Player must be advisor" }
    val card = igs.cards.firstOrNull { it.id == cardId } ?: throw IllegalArgumentException("Card ID not found")

    this.deck.playedCards.add(card)
    this.checkGameOverConditions()

    this.players.forEach { p -> p.wasAdvisorLastRound = false }
    player.wasAdvisorLastRound = true

    if (card.consequence == Card.Consequence.NEGATIVE) {
        if (this.handleNegativeCardAction()) {
            return
        }
    }

    this.idle()
}

/// Returns true if to prevent setting to an idle state
suspend fun GameState.GameInProgress.handleNegativeCardAction(): Boolean {
    val negativeCards = this.deck.negativeCardsPlayed
    val action = when (this.players.size) {
        4, 5, 6 -> when (negativeCards) {
            3 -> null
            4, 5 -> ActionChoice.KILL
            else -> return false
        }

        7, 8 -> when (negativeCards) {
            2 -> ActionChoice.INVESTIGATE
            3 -> ActionChoice.NOMINATE_PRESIDENT
            4, 5 -> ActionChoice.KILL
            else -> return false
        }

        else -> when (negativeCards) {
            1, 2 -> ActionChoice.INVESTIGATE
            3 -> ActionChoice.NOMINATE_PRESIDENT
            4, 5 -> ActionChoice.KILL
            else -> return false
        }
    }

    // Policy peek
    if (action == null) {
        this.innerGameState = InnerGameState.AwaitingPolicyPeek()

        val cards = this.deck.cardStack.take(3)
        this.president.emit(OutboundMessage.PolicyPeek(cards))
        return true
    }

    this.innerGameState = InnerGameState.AwaitingPresidentActionChoice(action)

    this.president.emit(
        OutboundMessage.RequestActionChoice(
            action,
            players = this.alive.filter { it != this.president }
                .map { p ->
                    /// Forced presidential election bypasses any prior restrictions
                    this.playerToActionSupplementedPlayer(p, bypassElection = true)
                }
        )
    )

    return true
}

suspend fun GameState.GameInProgress.passAdvisorElection(advisor: GamePlayer) {
    this.failedElections = 0

    val cards = this.deck.pickAndTakeThree()
    this.innerGameState = InnerGameState.AwaitingPresidentCardDiscard(cards, advisor.name)

    /// If advisor is Satan and enough negative cards have been played, game ends
    this.checkGameOverConditions()

    this.president.emit(OutboundMessage.RequestPresidentCardDiscard(cards))
}

fun GameState.GameInProgress.failAdvisorElection() {
    this.failedElections++

    if (this.failedElections >= FAILED_ELECTIONS_CHAOS) {
        this.failedElections = 0
        this.deck.playOneBlind()
    } else {
        this.idle()
    }
}

fun GameState.GameInProgress.playerMessageFromState(player: GamePlayer): OutboundMessage? {
    when (val igs = this.innerGameState) {
        is InnerGameState.AwaitingPresidentElectionResolution -> {}
        is InnerGameState.AwaitingAdvisorElectionResolution -> {}
        is InnerGameState.Idle -> {}

        is InnerGameState.AwaitingRoleConfirmations -> {
            if (player.name !in igs.confirmed) {
                return generateAssignRoleMessage(player)
            }
        }

        is InnerGameState.AwaitingAdvisorCardChoice -> {
            if (igs.advisorName == player.name) {
                return OutboundMessage.RequestAdvisorCardChoice(
                    cards = igs.cards,
                    vetoable = this.deck.negativeCardsPlayed >= NEGATIVE_CARD_COUNT_VETO
                )
            }
        }

        is InnerGameState.AwaitingInvestigationAnalysis -> {
            if (this.president == player) {
                val target = this[igs.target] ?: return null
                return OutboundMessage.InvestigationResult(target.stripped, target.role.simple)
            }
        }

        is InnerGameState.AwaitingPolicyPeek -> {
            if (this.president == player) {
                val cards = this.deck.cardStack.take(3)
                return OutboundMessage.PolicyPeek(cards)
            }
        }

        is InnerGameState.AwaitingPresidentActionChoice -> {
            if (this.president != player) {
                return null
            }

            val actionSupplementedPlayers = this.alive
                .filter { it != this.president }
                .map { p -> playerToActionSupplementedPlayer(p, bypassElection = true) }

            return OutboundMessage.RequestActionChoice(
                action = igs.action,
                players = actionSupplementedPlayers
            )
        }

        is InnerGameState.AwaitingPresidentCardDiscard -> {
            if (this.president == player) {
                return OutboundMessage.RequestPresidentCardDiscard(igs.cards)
            }
        }
    }

    return null
}

fun GameState.GameInProgress.playerToActionSupplementedPlayer(
    player: GamePlayer,
    /// This should only be set to true when this is a presidential election,
    // since we don't care about any prior restrictions
    bypassElection: Boolean = false
): ActionSupplementedPlayer {
    val electable = bypassElection || if (this.isSmallGame) {
        !player.wasAdvisorLastRound
    } else {
        (!player.wasPresidentLastRound && !player.wasAdvisorLastRound)
    }

    return ActionSupplementedPlayer(
        player.name,
        player.icon,
        !player.isInvestigated,
        electable
    )
}

fun GameState.GameInProgress.handlePlayerConfirmation(player: GamePlayer) {
    when (val igs = this.innerGameState) {
        is InnerGameState.AwaitingRoleConfirmations -> {
            require(player.name !in igs.confirmed) { "Player must not have already confirmed role" }

            igs.confirmed.add(player.name)
            if (igs.confirmed.size == this.players.size) {
                this.idle()
            }
        }

        is InnerGameState.AwaitingInvestigationAnalysis -> {
            require(this.president == player) { "Player must be president to confirm investigation" }
            this.idle()
        }

        is InnerGameState.AwaitingPolicyPeek -> {
            require(this.president == player) { "Player must be president to confirm policy peek" }
            this.idle()
        }

        else -> throw IllegalStateException("Got unexpected confirmation")
    }
}