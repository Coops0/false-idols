package com.cooper.game

import com.cooper.FalseIdolsError
import com.cooper.game.processor.ServerInboundMessageProcessorAction
import com.cooper.game.processor.handlePlayerInboundApplicationMessage
import com.cooper.game.processor.handleServerInboundApplicationMessage
import com.cooper.game.processor.playerMessageFromState
import com.cooper.message.InboundMessage
import com.cooper.message.OutboundMessage
import com.cooper.message.OutboundMessage.StrippedPlayer.Companion.stripped
import com.cooper.message.server.ServerInboundMessage
import com.cooper.message.server.ServerOutboundMessage
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import java.util.*

sealed class InnerApplicationMessage {
    class PlayerInboundMessageInner(val sessionId: SessionId, val inboundMessage: InboundMessage) :
            InnerApplicationMessage()

    class ServerInboundMessageInner(val serverInboundMessage: ServerInboundMessage) : InnerApplicationMessage()

    class PlayerJoin(
        val playerName: PlayerName,
        val flow: MutableSharedFlow<OutboundMessage>,
        /// Completable to receive player's connection session ID or join error
        val completable: CompletableDeferred<Result<SessionId>>
    ) : InnerApplicationMessage()

    class PlayerDisconnect(val playerName: PlayerName, val sessionId: SessionId) :
            InnerApplicationMessage()

    class NewServerConnection(val server: MutableSharedFlow<ServerOutboundMessage>?) :
            InnerApplicationMessage()

    class Shutdown(val completable: CompletableDeferred<Unit>) : InnerApplicationMessage()
}

class GameOverThrowable(val winner: SimpleRole, val reason: GameState.GameOver.Reason) : Throwable()

suspend fun gameActor(innerApplicationFlow: SharedFlow<InnerApplicationMessage>) {
    var gameState: GameState = GameState.Lobby(null)

    innerApplicationFlow.collect { message ->
        when (message) {
            is InnerApplicationMessage.PlayerInboundMessageInner -> {
                val player = gameState.getBySessionId(message.sessionId) ?: return@collect

                //@formatter:off
                try {
                    gameState.handlePlayerInboundApplicationMessage(player.name, message.inboundMessage)
                } catch (e: GameOverThrowable) {
                    gameState = (gameState as GameState.GameInProgress).toGameOver(e.winner, e.reason)
                } catch (e: IllegalStateException) {
                    gameState.sendServer(ServerOutboundMessage.Error(FalseIdolsError.illegalState(player.name, e.message)))
                } catch (e: IllegalArgumentException) {
                    gameState.sendServer(ServerOutboundMessage.Error(FalseIdolsError.illegalArgument(player.name, e.message)))
                } catch (e: AssertionError) {
                    gameState.sendServer(ServerOutboundMessage.Error(FalseIdolsError.assertionError(player.name, e.message)))
                }
                //@formatter:on
            }

            is InnerApplicationMessage.ServerInboundMessageInner -> {
                val action = try {
                    gameState.handleServerInboundApplicationMessage(message.serverInboundMessage)
                } catch (e: GameOverThrowable) {
                    gameState = (gameState as GameState.GameInProgress).toGameOver(e.winner, e.reason)
                    null
                } catch (e: IllegalStateException) {
                    gameState.sendServer(ServerOutboundMessage.Error(FalseIdolsError.illegalState(message = e.message)))
                    null
                } catch (e: IllegalArgumentException) {
                    gameState.sendServer(ServerOutboundMessage.Error(FalseIdolsError.illegalArgument(message = e.message)))
                    null
                } catch (e: AssertionError) {
                    gameState.sendServer(ServerOutboundMessage.Error(FalseIdolsError.assertionError(message = e.message)))
                    null
                }

                when (action) {
                    ServerInboundMessageProcessorAction.START_GAME -> {
                        gameState = (gameState as GameState.Lobby).toGameInProgress()
                        (gameState as GameState.GameInProgress).sendPlayerRoles()
                    }

                    ServerInboundMessageProcessorAction.BACK_TO_LOBBY -> {
                        gameState = (gameState as GameState.GameOver).toLobby()
                    }

                    null -> {}
                }
            }

            is InnerApplicationMessage.PlayerJoin -> gameState.handlePlayerJoin(message)
            is InnerApplicationMessage.PlayerDisconnect -> gameState.handlePlayerDisconnect(message)
            is InnerApplicationMessage.NewServerConnection -> {
                gameState.server = message.server
                gameState.sendServer(ServerOutboundMessage.UpdateGameState(gameState))
            }

            is InnerApplicationMessage.Shutdown -> {
                try {
                    gameState.players.forEach { player -> player.disconnectAll(OutboundMessage.Disconnect.DisconnectionReason.SERVER_SHUTDOWN) }
                    gameState.server?.emit(ServerOutboundMessage.Shutdown())
                    gameState.server = null
                } finally {
                    message.completable.complete(Unit)
                }
                // We should be cancelled
                return@collect
            }
        }

        // if it is ping, no change -> no need to resend state
        if (
            (message is InnerApplicationMessage.PlayerInboundMessageInner && message.inboundMessage is InboundMessage.Ping) ||
            (message is InnerApplicationMessage.ServerInboundMessageInner && message.serverInboundMessage is ServerInboundMessage.Ping)
        ) {
            return@collect
        }

        gameState.sendServer(ServerOutboundMessage.UpdateGameState(gameState))
    }
}

private suspend fun GameState.handlePlayerJoin(message: InnerApplicationMessage.PlayerJoin) {
    val flow = message.flow
    val existingPlayer = this[message.playerName]

    val sessionId = UUID.randomUUID()

    println("player ${message.playerName} joined with session ID $sessionId (existing: ${existingPlayer != null})")

    if (existingPlayer != null) {
        existingPlayer.connect(sessionId, flow)

        flow.emit(OutboundMessage.AssignIcon(existingPlayer.icon))

        // If player already exists, send them any important messages
        if (this is GameState.GameInProgress) {
            val playerMessage = this.playerMessageFromState(existingPlayer as GamePlayer)
            if (playerMessage != null) {
                flow.emit(playerMessage)
            }
        }

        message.completable.complete(Result.success(sessionId))
        return
    }

    if (this !is GameState.Lobby) {
        message.completable.complete(Result.failure(IllegalStateException("Cannot join game in progress")))
        return
    }

    // Players can reconnect causing icon weirdness.
    // Just try to find the first least used icon, this should still maintain default order.
    val playerIcon = PlayerIcon.entries
        .minByOrNull { icon -> this.players.count { p -> p.icon == icon } }
        ?: PlayerIcon.entries[0]

    println("${this.players.size} -> $playerIcon")

    val player = Player(message.playerName, playerIcon, PlayerConnection(sessionId, flow))
    this.players.add(player)

    message.completable.complete(Result.success(sessionId))

    // Send them their icon
    flow.emit(OutboundMessage.AssignIcon(playerIcon))
}

private fun GameState.handlePlayerDisconnect(message: InnerApplicationMessage.PlayerDisconnect) {
    this[message.playerName]?.disconnect(message.sessionId)
}

private fun GameState.GameInProgress.generateAssignRoleMessage(player: GamePlayer): OutboundMessage.AssignRole {
    if (player.role == ComplexRole.ANGEL) {
        return OutboundMessage.AssignRole(
            role = player.role,
            demonCount = demons.size,
            isSmallGame = isSmallGame
        )
    }

    if (player.role == ComplexRole.DEMON) {
        return OutboundMessage.AssignRole(
            role = player.role,
            demonCount = demons.size,
            demons = demons
                .filter { it != player }
                .map { it.stripped },
            satan = satan.stripped,
            isSmallGame = isSmallGame
        )
    }

    assert(player.role == ComplexRole.SATAN)

    if (this.isSmallGame) {
        return OutboundMessage.AssignRole(
            role = player.role,
            demonCount = demons.size,
            demons = demons.map { it.stripped },
            isSmallGame = true
        )
    }

    return OutboundMessage.AssignRole(
        role = player.role,
        demonCount = demons.size,
        isSmallGame = false
    )
}

suspend fun GameState.GameInProgress.sendPlayerRoles() {
    players.forEach { player -> player.emit(this.generateAssignRoleMessage(player)) }
}