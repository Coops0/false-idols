package com.cooper.message.server

import com.cooper.FalseIdolsError
import com.cooper.game.GameState

sealed class ServerOutboundMessage(val type: String) {
    class UpdateGameState(val gameState: GameState) :
            ServerOutboundMessage("update_game_state")

    class Error(val error: FalseIdolsError) :
            ServerOutboundMessage("error") {
    }
}