package com.cooper.game.processor

import com.cooper.game.*
import com.cooper.game.SimpleRole.Companion.simple
import com.cooper.message.OutboundMessage
import com.cooper.message.OutboundMessage.StrippedPlayer.Companion.stripped

suspend fun GameState.GameInProgress.rotateChief() {
    val igs = this.innerGameState
    val wasInitialWaitPeriod = igs is InnerGameState.Idle && igs.initialWaitPeriod

    this.players.forEach { player -> player.clearQueue() }

    val newChief = if (wasInitialWaitPeriod) {
        // Chief has already been randomly assigned
        this.chief
    } else {
        var currentChiefIndex = this.alive.indexOf(this.chief)
        if (currentChiefIndex == -1) {
            // Chief has been killed (shouldn't be possible)
            currentChiefIndex = this.players
                // Find who the next chief would be if the chief was alive
                .filter { player -> player.isAlive || player == this.chief }
                .indexOf(this.chief)
        }

        // Find next chief in line, or wrap around to the first alive player
        this.alive[(currentChiefIndex + 1) % this.alive.size]
    }

    if (!wasInitialWaitPeriod) {
        this.players.forEach { player -> player.wasChiefLastRound = false }
        this.chief.wasChiefLastRound = true
        this.chief.isChief = false

        newChief.isChief = true
    }

    this.requestChiefAction()
}

suspend fun GameState.GameInProgress.requestChiefAction() {
    val permittedActions = mutableListOf(ActionChoice.NOMINATE)
    if (this.deck.absolutePoints >= MIN_ABS_POINTS_TO_INVESTIGATE) permittedActions.add(ActionChoice.INVESTIGATE)
    if (this.deck.absolutePoints >= MIN_ABS_POINTS_TO_KILL) permittedActions.add(ActionChoice.KILL)

    this.innerGameState = InnerGameState.AwaitingChiefActionChoice(
        permittedActions = permittedActions
    )

    val playersSize = this.players.size
    val actionablePlayers = this.alive.filter { it != this.chief }
        .map { OutboundMessage.RequestActionChoice.ActionSupplementedPlayer.fromGamePlayer(playersSize, it) }

    this.chief.send(
        OutboundMessage.RequestActionChoice(
            permittedActions = permittedActions,
            players = actionablePlayers,
        ),
        queued = true
    )
}

// throws GameOverThrowable
fun GameState.GameInProgress.checkGameOverConditions() {
    if (deck.points <= NEGATIVE_THRESHOLD_SATAN_WIN && deck.absolutePoints >= MIN_ABS_POINTS_THRESHOLD_SATAN_WIN) {
        val igs = innerGameState
        if (igs is InnerGameState.AwaitingChiefCardDiscard && igs.advisorName == satan.name) {
            throw GameOverThrowable(
                winner = SimpleRole.DEMON,
                reason = GameState.GameOver.Reason.SATAN_ELECTED_ADVISOR_LATE_GAME
            )
        }
    }

    if (deck.points >= POSITIVE_THRESHOLD_WIN) {
        throw GameOverThrowable(
            winner = SimpleRole.ANGEL,
            reason = GameState.GameOver.Reason.POSITIVE_THRESHOLD_REACHED
        )
    }

    if (deck.points <= NEGATIVE_THRESHOLD_WIN) {
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
    this.players.forEach(Player::clearQueue)
}

suspend fun GameState.GameInProgress.handlePlayerActionChoice(
    aggressor: GamePlayer,
    action: ActionChoice,
    targetName: String
) {
    val igs = this.innerGameState

    require(igs is InnerGameState.AwaitingChiefActionChoice) { "Game must be awaiting player action choice to choose action" }
    require(this.chief == aggressor) { "Player must be chief to execute action" }

    val permittedActions = igs.permittedActions
    require(action in permittedActions) { "Player must execute a permitted action" }

    val target = this[targetName] ?: throw IllegalArgumentException("Target not found")
    require(target.isAlive) { "Target must be alive" }
    require(target != aggressor) { "Cannot execute action upon self" }

    when (action) {
        ActionChoice.INVESTIGATE -> {
            require(this.deck.absolutePoints >= MIN_ABS_POINTS_TO_INVESTIGATE) { "Must have enough absolute points to investigate" }
            require(!target.isInvestigated) { "Target must not have already been investigated" }

            target.isInvestigated = true

            this.innerGameState = InnerGameState.AwaitingInvestigationAnalysis(targetName)
            aggressor.send(OutboundMessage.InvestigationResult(target.stripped, target.role.simple), queued = true)
        }

        ActionChoice.KILL -> {
            require(this.deck.absolutePoints >= MIN_ABS_POINTS_TO_KILL) { "Must have enough absolute points to kill" }

            target.isAlive = false
            if (target.role == ComplexRole.SATAN) {
                throw GameOverThrowable(winner = SimpleRole.DEMON, reason = GameState.GameOver.Reason.SATAN_KILLED)
            }

            this.checkGameOverConditions()
            this.idle()
        }

        ActionChoice.NOMINATE -> {
            if (this.players.size <= 5) {
                require(!target.wasAdvisorLastRound) { "Target must not have been advisor last round" }
            } else {
                require(!target.wasChiefLastRound && !target.wasAdvisorLastRound) { "Target must not have been chief or advisor last round" }
            }
            this.innerGameState = InnerGameState.AwaitingElectionResolution(
                nominee = target.name
            )

            // now the host will decide outcome
        }
    }

    this.players.forEach { player -> player.wasAdvisorLastRound = false }
}

suspend fun GameState.GameInProgress.handleChiefDiscardCard(player: GamePlayer, cardId: Int) {
    require(this.innerGameState is InnerGameState.AwaitingChiefCardDiscard) { "Game must be awaiting chief card discard to discard card" }
    require(this.chief == player)

    val igs = this.innerGameState as InnerGameState.AwaitingChiefCardDiscard
    val cards = igs.cards
    val advisorName = igs.advisorName

    require(cards.any { it.id == cardId }) { "Card not found" }

    val newCards = cards.filter { it.id != cardId }
    this.innerGameState = InnerGameState.AwaitingAdvisorCardChoice(newCards, advisorName)

    this.chief.clearQueue()

    val advisor = this[advisorName]!!
    advisor.send(OutboundMessage.RequestAdvisorCardChoice(newCards), queued = true)
}

fun GameState.GameInProgress.handleAdvisorChooseCard(player: GamePlayer, cardId: Int) {
    require(this.innerGameState is InnerGameState.AwaitingAdvisorCardChoice) { "Game must be awaiting advisor card choice to choose card" }
    val igs = this.innerGameState as InnerGameState.AwaitingAdvisorCardChoice

    require(igs.advisorName == player.name) { "Player must be advisor" }
    val card = igs.cards.firstOrNull { it.id == cardId } ?: throw IllegalArgumentException("Card not found")

    this.deck.playedCards.add(card)
    this.checkGameOverConditions()

    this.players.forEach { p -> p.wasAdvisorLastRound = false }

    player.wasAdvisorLastRound = true

    this.idle()
}

suspend fun GameState.GameInProgress.passElection(advisor: GamePlayer) {
    this.failedElections = 0

    val cards = this.deck.pickAndTakeThree()
    this.innerGameState = InnerGameState.AwaitingChiefCardDiscard(cards, advisor.name)

    this.checkGameOverConditions()

    this.chief.send(OutboundMessage.RequestChiefCardDiscard(cards), queued = true)
}

fun GameState.GameInProgress.failElection() {
    this.failedElections++

    if (this.isChaos) {
        this.failedElections = 0
        this.deck.playOneBlind()
    } else {
        this.idle()
    }
}