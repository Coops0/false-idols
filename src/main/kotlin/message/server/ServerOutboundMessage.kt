package com.cooper.message.server

import com.cooper.game.GameState
import com.cooper.game.Player

abstract class ServerInboundMessageOutboundMessage(val type: String)

class UpdatePlayersServerOutboundMessage(val players: List<Player>) :
        ServerInboundMessageOutboundMessage("update_players")

class UpdateGameStateServerOutboundMessage(val gameState: GameState) :
        ServerInboundMessageOutboundMessage("update_game_state")