package com.cooper

import com.cooper.game.GameInProgressGameState
import com.cooper.game.GameState
import com.cooper.game.LobbyGameState
import com.cooper.game.Player
import com.cooper.message.InboundMessage
import com.cooper.message.OutboundMessage
import com.cooper.message.server.ServerInboundMessage
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.selects.select



val playerChannel = Channel<PlayerChannelMessage>()

val inboundMessageChannel = Channel<InboundMessage>()
val serverInboundMessageChannel = Channel<ServerInboundMessage>()

class GameContext(
    val lobbyManager: LobbyManager,
    val gameState: GameState
) {
    val players: List<Player>
        get() = lobbyManager.connections.map { it.player }

    suspend fun send(playerName: String, message: OutboundMessage) {
        lobbyManager.send(playerName, message)
    }

    suspend fun broadcast(message: OutboundMessage) {
        lobbyManager.broadcast(message)
    }
}

suspend fun gameStateWorker() {
    val lobbyManager = LobbyManager()
    var gameState: GameState = LobbyGameState()


    while (true) {
        select<Unit> {
            playerChannel.onReceive { msg ->
                lobbyManager.handlePlayerChannelMessage(msg)
            }
            inboundMessageChannel.onReceive { inboundMessage ->
                if (gameState is GameInProgressGameState) {
                    val ctx = GameContext(lobbyManager, gameState)
                    when (inboundMessage) {
                        is InboundMessage.ChooseActionOnPlayerInboundMessage -> {
                            // TODO
                        }
                        is InboundMessage.DiscardOneCardInboundMessage -> {
                            // TODO
                        }
                        is InboundMessage.ChooseCardInboundMessage -> {
                            // TODO
                        }
                }
            }
            serverInboundMessageChannel.onReceive { serverInboundMessage ->
                when (serverInboundMessage) {
                    is ServerInboundMessage.UpdateGameStateServerInboundMessage -> {
                        gameState = serverInboundMessage.gameState
                        lobbyManager.broadcast(OutboundMessage.UpdateGameStateOutboundMessage(gameState))
                    }
                }
            }
        }
    }
}