package com.cooper.game.processor

import com.cooper.game.ActionChoice
import com.cooper.game.GameState
import com.cooper.game.InnerGameState
import com.cooper.game.Player

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