package com.cooper.game.processor

import com.cooper.game.*
import com.cooper.message.OutboundMessage

suspend fun GameState.GameInProgress.rotateChief() {
    val chief = if (this.innerGameState is InnerGameState.PostRoleGracePeriod) {
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

    this.players.forEach { player -> player.isChief = false }
    chief.isChief = true

    this.innerGameState = InnerGameState.AwaitingPlayerActionChoice(
        forcedAction = null, // TODO
        cause = InnerGameState.AwaitingPlayerActionChoice.PlayerActionChoiceCause.NORMAL_CHIEF
    )

    val actionablePlayers = this.alive.filter { it != chief }
        .map(OutboundMessage.RequestActionChoice.ActionSupplementedPlayer::fromGamePlayer)

    chief.send(
        OutboundMessage.RequestActionChoice(
            forcedAction = null,
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

suspend fun GameState.GameInProgress.handlePlayerActionChoice(
    player: Player,
    action: ActionChoice,
    targetName: String
) {
    require(this.innerGameState is InnerGameState.AwaitingPlayerActionChoice) { "Game must be awaiting player action choice to choose action" }
    require(this.chief == player) { "Player must be chief to execute action" }

    val forcedAction = (this.innerGameState as InnerGameState.AwaitingPlayerActionChoice).forcedAction
    require(forcedAction == null || forcedAction == action) { "Player must choose forced action" }

    val target = this[targetName] ?: throw IllegalArgumentException("Target not found")
    when (action) {
        ActionChoice.INVESTIGATE -> TODO()
        ActionChoice.KILL -> TODO()
        ActionChoice.NOMINATE -> TODO()
    }
}

suspend fun GameState.GameInProgress.handleChiefDiscardCard(player: Player, cardId: Int) {
    require(this.innerGameState is InnerGameState.AwaitingChiefCardDiscard) { "Game must be awaiting chief card discard to discard card" }
    require(this.chief.name == player.name)

    val igs = this.innerGameState as InnerGameState.AwaitingChiefCardDiscard
    val cards = igs.cards
    val advisorName = igs.advisorName

    require(cards.any { it.id == cardId }) { "Card not found" }

    val newCards = cards.filter { it.id != cardId }
    this.innerGameState = InnerGameState.AwaitingAdvisorCardChoice(newCards, advisorName)

    val advisor = this[advisorName] ?: throw IllegalArgumentException("Advisor not found")
    advisor.send(OutboundMessage.RequestAdvisorCardChoice(newCards))
}

suspend fun GameState.GameInProgress.handleAdvisorChooseCard(player: Player, cardId: Int) {
    require(this.innerGameState is InnerGameState.AwaitingAdvisorCardChoice) { "Game must be awaiting advisor card choice to choose card" }
    val igs = this.innerGameState as InnerGameState.AwaitingAdvisorCardChoice

    require(igs.advisorName == player.name) { "Player must be advisor" }
    val card = igs.cards.firstOrNull { it.id == cardId } ?: throw IllegalArgumentException("Card not found")

    this.deck.playedCards.add(card)
    // todo if chaos level xxx && check if game is over
    this.rotateChief()
}