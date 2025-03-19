package com.cooper.game

import com.cooper.SocketContentConverterSender
import com.cooper.message.OutboundMessage
import com.cooper.message.server.ServerOutboundMessage
import com.fasterxml.jackson.annotation.JsonIgnore

const val MIN_ABS_POINTS_TO_INVESTIGATE = 4
const val MIN_ABS_POINTS_TO_KILL = 8
const val POSITIVE_THRESHOLD_WIN = 10
const val NEGATIVE_THRESHOLD_WIN = -6
const val NEGATIVE_THRESHOLD_SATAN_WIN = -2

sealed class GameState(val name: String) {
    @get:JsonIgnore abstract val server: SocketContentConverterSender<ServerOutboundMessage>
    abstract val players: List<Player>

    suspend fun sendServer(message: ServerOutboundMessage) {
        server.send(message)
    }

    open operator fun get(playerName: PlayerName): Player? {
        return players.firstOrNull { it.name == playerName }
    }

    fun getBySessionId(sessionId: SessionId): Player? {
        return players.firstOrNull { player ->
            player.channels.any { channel ->
                channel.sessionId == sessionId
            }
        }
    }

    suspend fun broadcast(message: OutboundMessage) {
        players.forEach { it.send(message) }
    }

    suspend fun send(playerName: PlayerName, message: OutboundMessage) {
        this[playerName]?.send(message)
    }

    class Lobby(
        override val server: SocketContentConverterSender<ServerOutboundMessage>,
        override val players: MutableList<Player> = mutableListOf()
    ) : GameState("lobby") {
        fun toGameInProgress() = GameInProgress(server, players)
    }

    class GameInProgress private constructor(
        override val server: SocketContentConverterSender<ServerOutboundMessage>,
        override val players: MutableList<GamePlayer>
    ) : GameState("game_in_progress") {
        @Suppress("UNUSED_PARAMETER", "LocalVariableName") constructor(
            server: SocketContentConverterSender<ServerOutboundMessage>,
            originalPlayers: List<Player>,
            /// Need this to prevent constructor declaration clash
            _fakeArg: Unit? = null
        ) : this(server, assignPlayerRoles(originalPlayers))

        var innerGameState: InnerGameState = InnerGameState.Idle()
        val deck: CardDeck = CardDeck()

        var failedElections: Int = 0

        val chief: GamePlayer get() = players.first(GamePlayer::isChief)
        val satan: GamePlayer get() = players.first { it.role == ComplexRole.SATAN }
        val demons: List<GamePlayer> get() = players.filter { it.role == ComplexRole.DEMON }
        val angels: List<GamePlayer> get() = players.filter { it.role == ComplexRole.ANGEL }
        val alive: List<GamePlayer> get() = players.filter(GamePlayer::isAlive)

        /// 3 failed elections in a row
        val isChaos: Boolean get() = failedElections >= 3

        fun toGameOver(winner: SimpleRole, cause: GameOverReason) = GameOver(
            server,
            players.toMutableList(),
            winner.name,
            satan.name,
            demons.map(Player::name),
            cause
        )

        override operator fun get(playerName: PlayerName): GamePlayer? {
            return players.firstOrNull { it.name == playerName }
        }

        private companion object {
            private fun assignPlayerRoles(originalPlayers: List<Player>): MutableList<GamePlayer> {
                // OG game rules:
                // 5, 6 -> 1
                // 7, 8 -> 2
                // 9, 10 -> 3
                val demonCount = when (originalPlayers.size) {
                    4, 5 -> 1
                    6, 7 -> 2
                    8, 9 -> 3
                    else -> 4
                }

                val shuffledPlayers = originalPlayers.shuffled().toMutableList()
                val demons = List(demonCount) { shuffledPlayers.removeAt(0) }
                val satan = shuffledPlayers.removeAt(0)

                val players = mutableListOf<GamePlayer>()

                players.add(GamePlayer(satan, ComplexRole.SATAN))
                players.addAll(demons.map { GamePlayer(it, ComplexRole.DEMON) })
                players.addAll(shuffledPlayers.map { GamePlayer(it, ComplexRole.ANGEL) })

                players.shuffle()
                players.random().isChief = true

                return players
            }
        }
    }

    class GameOver(
        override val server: SocketContentConverterSender<ServerOutboundMessage>,
        override val players: MutableList<Player>,
        val winner: PlayerName,
        val satan: PlayerName,
        val demons: List<PlayerName>,
        val reason: GameOverReason
    ) : GameState("game_over") {
        fun toLobby() = Lobby(server, players)
    }

    enum class GameOverReason {
        SATAN_KILLED,
        SATAN_ELECTED_ADVISOR_LATE_GAME,
        POSITIVE_THRESHOLD_REACHED,
        NEGATIVE_THRESHOLD_REACHED,
        ALL_ANGELS_DEAD
    }
}

sealed class InnerGameState(val name: String) {
    class Idle(
        val initialWaitPeriod: Boolean = false
    ) : InnerGameState("idle")

    class AwaitingPlayerActionChoice(
        val permittedActions: List<ActionChoice> = ActionChoice.entries
    ) : InnerGameState("awaiting_chief_action_choice")

    class AwaitingChiefCardDiscard(val cards: List<Card>, val advisorName: String) :
            InnerGameState("awaiting_chief_card_discard")

    class AwaitingAdvisorCardChoice(val cards: List<Card>, val advisorName: String) :
            InnerGameState("awaiting_advisor_card_choice")

    class AwaitingElectionResolution(val nominee: PlayerName) : InnerGameState("awaiting_election_outcome")
    class AwaitingInvestigationAnalysis(val target: PlayerName) : InnerGameState("awaiting_investigation_analysis")
}
