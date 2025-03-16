package com.cooper.game.processor

import com.cooper.game.ActionChoice
import com.cooper.game.GameState
import com.cooper.game.InnerGameState
import com.cooper.game.Player
import com.cooper.message.RequestAdvisorCardChoiceOutboundMessage

suspend fun GameState.GameInProgress.rotatePresident() {
    TODO()
}

suspend fun GameState.GameInProgress.handlePlayerActionChoice(
    player: Player,
    action: ActionChoice,
    targetName: String
) {
    require(this.innerGameState is InnerGameState.AwaitingPlayerActionChoice) { "Game must be awaiting player action choice to choose action" }

    val forcedAction = (this.innerGameState as InnerGameState.AwaitingPlayerActionChoice).forcedAction
    if (forcedAction != null && forcedAction != action) {
        throw IllegalArgumentException("Player must choose forced action")
    }

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
    advisor.send(RequestAdvisorCardChoiceOutboundMessage(newCards))
}

suspend fun GameState.GameInProgress.handleAdvisorChooseCard(player: Player, cardId: Int) {
    require(this.innerGameState is InnerGameState.AwaitingAdvisorCardChoice) { "Game must be awaiting advisor card choice to choose card" }
    val igs = this.innerGameState as InnerGameState.AwaitingAdvisorCardChoice

    require(igs.advisorName == player.name) { "Player must be advisor" }
    val card = igs.cards.firstOrNull { it.id == cardId } ?: throw IllegalArgumentException("Card not found")

    this.deck.playedCards.add(card)
    // todo if chaos level xxx && check if game is over
    this.rotatePresident()
}