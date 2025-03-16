package com.cooper.game

import com.cooper.message.OutboundMessage
import com.cooper.message.server.ServerOutboundMessage
import kotlinx.coroutines.channels.Channel
import kotlin.math.absoluteValue

sealed class GameState {
    abstract val name: String
    abstract val server: Channel<ServerOutboundMessage>
    abstract val players: List<Player>

    suspend fun sendServer(message: ServerOutboundMessage) {
        server.send(message)
    }

    operator fun get(playerName: PlayerName): Player? {
        return players.firstOrNull { it.name == playerName }
    }

    class Lobby(
        override val server: Channel<ServerOutboundMessage>,
        override val players: MutableList<Player> = mutableListOf()
    ) : GameState() {
        override val name: String = "lobby"

        suspend fun broadcast(message: OutboundMessage) {
            players.forEach { it.send(message) }
        }

        suspend fun send(playerName: PlayerName, message: OutboundMessage) {
            players.firstOrNull { it.name == playerName }?.send(message)
        }

        fun toGameInProgress(): GameInProgress {
            return GameInProgress(server, players)
        }
    }

    class GameInProgress private constructor(
        override val server: Channel<ServerOutboundMessage>,
        override val players: MutableList<GamePlayer>
    ) : GameState() {
        override val name: String = "game_in_progress"

        constructor(server: Channel<ServerOutboundMessage>, originalPlayers: List<Player>) : this(
            server,
            assignPlayerRoles(originalPlayers)
        )

        var innerGameState: InnerGameState = InnerGameState.AwaitingPlayerActionChoice()
        val deck: CardDeck = CardDeck()

        var points: Int = 0
            private set
        var absolutePoints: Int = 0
            private set

        var failedElections: Int = 0

        val chief: GamePlayer
            get() = players.first(GamePlayer::isChief)

        val satan: GamePlayer
            get() = players.first { it.role == ComplexRole.SATAN }

        val demons: List<GamePlayer>
            get() = players.filter { it.role == ComplexRole.DEMON }

        val angels: List<GamePlayer>
            get() = players.filter { it.role == ComplexRole.ANGEL }

        val alive: List<GamePlayer>
            get() = players.filter(GamePlayer::isAlive)

        /// 6+ cards played
        val isLateGame: Boolean
            get() = absolutePoints >= 6

        fun addPoints(points: Int) {
            this.points += points
            this.absolutePoints += points.absoluteValue
        }

        suspend fun broadcast(message: OutboundMessage) {
            players.forEach { it.send(message) }
        }

        suspend fun send(playerName: PlayerName, message: OutboundMessage) {
            players.firstOrNull { it.name == playerName }?.send(message)
        }

        fun toGameOver(winner: SimpleRole, cause: GameOverCause): GameOver {
            return GameOver(
                server,
                players.toMutableList(),
                winner,
                satan.name,
                demons.map(GamePlayer::name),
                cause
            )
        }

        private companion object {
            private fun assignPlayerRoles(originalPlayers: List<Player>): MutableList<GamePlayer> {
                val demonCount = when (originalPlayers.size) {
                    3, 4 -> 1
                    5, 6 -> 2
                    7, 8 -> 3
                    else -> 4
                }

                val shuffledPlayers = originalPlayers.shuffled().toMutableList()
                val demons = List(demonCount) { shuffledPlayers.removeAt(0) }
                val satan = shuffledPlayers.removeAt(0)

                val players = mutableListOf<GamePlayer>()

                players.add(GamePlayer(satan, ComplexRole.SATAN))
                players.addAll(demons.map { GamePlayer(it, ComplexRole.DEMON) })
                players.addAll(shuffledPlayers.map { GamePlayer(it, ComplexRole.ANGEL) })

                return players.shuffled().toMutableList()
            }
        }
    }

    class GameOver(
        override val server: Channel<ServerOutboundMessage>,
        override val players: MutableList<Player>,
        val winner: SimpleRole,
        val satan: PlayerName,
        val demons: List<PlayerName>,
        val cause: GameOverCause
    ) : GameState() {
        override val name: String = "game_over"

        suspend fun broadcast(message: OutboundMessage) {
            players.forEach { it.send(message) }
        }

        suspend fun send(playerName: PlayerName, message: OutboundMessage) {
            players.firstOrNull { it.name == playerName }?.send(message)
        }
    }

    enum class GameOverCause {
        SATAN_KILLED,
        SATAN_ELECTED,
        POSITIVE_THRESHOLD_REACHED,
        ALL_ANGELS_DEAD,
    }
}

sealed class InnerGameState {
    abstract val name: String

    class AwaitingPlayerActionChoice(
        val forcedAction: ActionChoice? = null,
        val cause: PlayerActionChoiceCause = PlayerActionChoiceCause.NORMAL_CHIEF,
    ) : InnerGameState() {
        override val name: String = "awaiting_chief_action_choice"

        enum class PlayerActionChoiceCause {
            NORMAL_CHIEF,
            FORCED_CHIEF
        }
    }

    class AwaitingChiefCardDiscard(val cards: Array<Card>) : InnerGameState() {
        override val name: String = "awaiting_chief_card_discard"
    }

    class AwaitingAdvisorCardChoice(val cards: Array<Card>) : InnerGameState() {
        override val name: String = "awaiting_advisor_card_choice"
    }

    class AwaitingElectionOutcome(val nominee: PlayerName) : InnerGameState() {
        override val name: String = "awaiting_election_outcome"
    }
}
