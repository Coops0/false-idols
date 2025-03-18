package com.cooper.message.server

import com.cooper.game.GameState

sealed class ServerOutboundMessage(val type: String) {
    class UpdateGameState(val gameState: GameState) :
            ServerOutboundMessage("update_game_state")
}