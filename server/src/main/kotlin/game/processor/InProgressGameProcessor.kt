package com.cooper.game.processor

import com.cooper.game.*
import com.cooper.game.SimpleRole.Companion.simple
import com.cooper.message.OutboundMessage
import com.cooper.message.OutboundMessage.StrippedPlayer.Companion.stripped

suspend fun GameState.GameInProgress.rotateChief() {
    val igs = this.innerGameState
    val wasInitialWaitPeriod = igs is InnerGameState.Idle && igs.initialWaitPeriod

    this.players.forEach { player -> player.wasAdvisorLastRound = false }
    if (igs is InnerGameState.AwaitingAdvisorCardChoice) {
        this[igs.advisorName]!!.wasAdvisorLastRound = true
    }

    val newChief = if (wasInitialWaitPeriod) {
        // Chief has already been randomly assigned
        this.chief
    } else {
        var currentChiefIndex = this.alive.indexOf(this.chief)
        if (currentChiefIndex == -1) {
            // Chief has been killed
            currentChiefIndex = this.players
                .filter { player -> player.isAlive || player == this.chief }
                .indexOf(this.chief)
        }

        val nextChief = this.alive[(currentChiefIndex + 1) % this.alive.size]
        nextChief
    }

    if (!wasInitialWaitPeriod) {
        this.players.forEach { player -> player.wasChiefLastRound = false }
        this.chief.wasChiefLastRound = true
        this.chief.isChief = false

        newChief.isChief = true
    }

    val permittedActions = if (this.isLateGame) ActionChoice.entries else listOf(ActionChoice.NOMINATE)

    this.innerGameState = InnerGameState.AwaitingPlayerActionChoice(
        permittedActions = permittedActions,
        cause = InnerGameState.AwaitingPlayerActionChoice.PlayerActionChoiceCause.NORMAL_CHIEF
    )

    val actionablePlayers = this.alive.filter { it != newChief }
        .map(OutboundMessage.RequestActionChoice.ActionSupplementedPlayer::fromGamePlayer)

    newChief.send(
        OutboundMessage.RequestActionChoice(
            permittedActions = permittedActions,
            players = actionablePlayers,
        )
    )
}

// throws GameOverThrowable
fun GameState.GameInProgress.checkGameOverConditions() {
    if (isLateGame) {
        val igs = innerGameState
        if (igs is InnerGameState.AwaitingChiefCardDiscard && igs.advisorName == satan.name) {
            throw GameOverThrowable(
                winner = SimpleRole.DEMON,
                reason = GameState.GameOverReason.SATAN_ELECTED_ADVISOR_LATE_GAME
            )
        }
    }

    if (deck.points > 8) {
        throw GameOverThrowable(winner = SimpleRole.ANGEL, reason = GameState.GameOverReason.POSITIVE_THRESHOLD_REACHED)
    }

    if (angels.isEmpty()) {
        throw GameOverThrowable(winner = SimpleRole.DEMON, reason = GameState.GameOverReason.ALL_ANGELS_DEAD)
    }

    if (!satan.isAlive) {
        throw GameOverThrowable(winner = SimpleRole.ANGEL, reason = GameState.GameOverReason.SATAN_KILLED)
    }
}

fun GameState.GameInProgress.idle() {
    this.innerGameState = InnerGameState.Idle()
}

suspend fun GameState.GameInProgress.handlePlayerActionChoice(
    aggressor: GamePlayer,
    action: ActionChoice,
    targetName: String
) {

    val igs = this.innerGameState

    require(igs is InnerGameState.AwaitingPlayerActionChoice) { "Game must be awaiting player action choice to choose action" }
    require(this.chief == aggressor) { "Player must be chief to execute action" }

    val permittedActions = igs.permittedActions
    require(action in permittedActions) { "Player must execute a permitted action" }

    val target = this[targetName] ?: throw IllegalArgumentException("Target not found")
    require(target.isAlive) { "Target must be alive" }
    require(target != aggressor) { "Cannot execute action upon self" }

    // todo check is late game
    when (action) {
        ActionChoice.INVESTIGATE -> {
            require(!target.isInvestigated) { "Target must not have already been investigated" }
            target.isInvestigated = true

            this.innerGameState = InnerGameState.AwaitingInvestigationAnalysis(targetName)
            aggressor.send(OutboundMessage.InvestigationResult(target.stripped, target.role.simple))
        }

        ActionChoice.KILL -> {
            target.isAlive = false
            if (target.role == ComplexRole.SATAN) {
                throw GameOverThrowable(winner = SimpleRole.DEMON, reason = GameState.GameOverReason.SATAN_KILLED)
            }

            this.checkGameOverConditions()
            this.idle()
        }

        ActionChoice.NOMINATE -> {
            require(igs.forcedElection || (!target.wasChiefLastRound && !target.wasAdvisorLastRound)) { "Target must not have been chief or advisor last round" }
            this.innerGameState = InnerGameState.AwaitingElectionResolution(
                nominee = target.name
            )

            // now the server will decide outcome
        }
    }
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

    val advisor = this[advisorName]!!
    advisor.send(OutboundMessage.RequestAdvisorCardChoice(newCards))
}

suspend fun GameState.GameInProgress.handleAdvisorChooseCard(player: GamePlayer, cardId: Int) {
    require(this.innerGameState is InnerGameState.AwaitingAdvisorCardChoice) { "Game must be awaiting advisor card choice to choose card" }
    val igs = this.innerGameState as InnerGameState.AwaitingAdvisorCardChoice

    require(igs.advisorName == player.name) { "Player must be advisor" }
    val card = igs.cards.firstOrNull { it.id == cardId } ?: throw IllegalArgumentException("Card not found")

    this.deck.playedCards.add(card)
    this.checkGameOverConditions()

    this.idle()
}

suspend fun GameState.GameInProgress.passElection(advisor: GamePlayer) {
    val cards = this.deck.pickAndTakeThree()
    this.innerGameState = InnerGameState.AwaitingChiefCardDiscard(cards, advisor.name)

    this.checkGameOverConditions()

    this.chief.send(OutboundMessage.RequestChiefCardDiscard(cards))
}

suspend fun GameState.GameInProgress.failElection() {
    // todo if chaos level xxx && check if game is over
    this.failedElections++

    if (!this.isChaos) {
        this.idle()
    }

    when (this.failedElections) {
        3 -> {
            // force investigate
            val actionablePlayers = this.alive.filter { it != this.chief }
                .map(OutboundMessage.RequestActionChoice.ActionSupplementedPlayer::fromGamePlayer)

            if (actionablePlayers.count(OutboundMessage.RequestActionChoice.ActionSupplementedPlayer::investigatable) == 0) {
                // no players to investigate, just skip
                return this.rotateChief()
            }

            this.innerGameState = InnerGameState.AwaitingPlayerActionChoice(
                permittedActions = listOf(ActionChoice.INVESTIGATE),
                cause = InnerGameState.AwaitingPlayerActionChoice.PlayerActionChoiceCause.CHAOS
            )

            this.chief.send(
                OutboundMessage.RequestActionChoice(
                    permittedActions = listOf(ActionChoice.INVESTIGATE),
                    players = actionablePlayers,
                )
            )
        }

        4 -> {
            // force kill
            val actionablePlayers = this.alive.filter { it != this.chief }
                .map(OutboundMessage.RequestActionChoice.ActionSupplementedPlayer::fromGamePlayer)

            this.innerGameState = InnerGameState.AwaitingPlayerActionChoice(
                permittedActions = listOf(ActionChoice.KILL),
                cause = InnerGameState.AwaitingPlayerActionChoice.PlayerActionChoiceCause.CHAOS
            )

            this.chief.send(
                OutboundMessage.RequestActionChoice(
                    permittedActions = listOf(ActionChoice.KILL),
                    players = actionablePlayers,
                )
            )
        }

        5 -> {
            // force elect
            val actionablePlayers = this.alive
                .filter { it != this.chief }
                .map { player ->
                    OutboundMessage.RequestActionChoice.ActionSupplementedPlayer(
                        name = player.name,
                        icon = player.icon.iconName,
                        investigatable = !player.isInvestigated,
                        // in this case, we don't care if they were chief/advisor last round
                        electable = true
                    )
                }

            this.innerGameState = InnerGameState.AwaitingPlayerActionChoice(
                permittedActions = listOf(ActionChoice.NOMINATE),
                cause = InnerGameState.AwaitingPlayerActionChoice.PlayerActionChoiceCause.CHAOS,
                forcedElection = true
            )

            this.chief.send(
                OutboundMessage.RequestActionChoice(
                    permittedActions = listOf(ActionChoice.NOMINATE),
                    players = actionablePlayers,
                )
            )
        }
    }
}