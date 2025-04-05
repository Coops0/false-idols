package com.cooper.message.server

import com.cooper.FalseIdolsError
import com.cooper.game.GameState
import com.cooper.game.PlayerName

sealed class ServerOutboundMessage(val type: String) {
    class UpdateGameState(val gameState: GameState) :
            ServerOutboundMessage("update_game_state")

    class Error(val error: FalseIdolsError) :
            ServerOutboundMessage("error")

    class PolicyPeeking :
            ServerOutboundMessage("policy_peeking")
}