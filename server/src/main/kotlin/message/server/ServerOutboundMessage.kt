package com.cooper.message.server

import com.cooper.game.GameState
import com.cooper.game.Player

abstract class ServerOutboundMessage(val type: String)

class UpdateGameStateServerOutboundMessage(val gameState: GameState) :
        ServerOutboundMessage("update_game_state")