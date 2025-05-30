package com.cooper.game

import com.cooper.message.server.ServerOutboundMessage
import com.fasterxml.jackson.annotation.JsonIgnore
import kotlinx.coroutines.flow.MutableSharedFlow
import java.security.SecureRandom

const val POSITIVE_CARD_COUNT_WIN = 5
const val NEGATIVE_CARD_COUNT_WIN = 6

const val NEGATIVE_CARD_COUNT_SATAN_ELECTION_WIN = 4
const val NEGATIVE_CARD_COUNT_VETO = 5

const val FAILED_ELECTIONS_CHAOS = 4
const val SMALL_GAME_PLAYER_SIZE = 6

sealed class GameState(val type: String) {
    @get:JsonIgnore abstract var server: MutableSharedFlow<ServerOutboundMessage>?
    abstract val players: List<Player>

    suspend fun sendServer(message: ServerOutboundMessage) {
        try {
            server?.emit(message)
        } catch (_: Throwable) {
            server = null
        }
    }

    open operator fun get(playerName: PlayerName): Player? {
        return players.firstOrNull { it.name == playerName }
    }

    fun getBySessionId(sessionId: SessionId): Player? {
        return players.firstOrNull { player ->
            player.flows.any { flow ->
                flow.sessionId == sessionId
            }
        }
    }

    class Lobby(
        override var server: MutableSharedFlow<ServerOutboundMessage>?,
        override val players: MutableList<Player> = mutableListOf()
    ) : GameState("lobby") {
        fun toGameInProgress() = GameInProgress(server, players, _fakeArg = null)
    }

    class GameInProgress private constructor(
        override var server: MutableSharedFlow<ServerOutboundMessage>?,
        override val players: MutableList<GamePlayer>
    ) : GameState("game_in_progress") {
        @Suppress("UNUSED_PARAMETER", "LocalVariableName") constructor(
            server: MutableSharedFlow<ServerOutboundMessage>?,
            originalPlayers: List<Player>,
            // Need this to prevent constructor declaration clash
            _fakeArg: Unit?
        ) : this(server, assignPlayerRoles(originalPlayers))

        var innerGameState: InnerGameState = InnerGameState.AwaitingRoleConfirmations(mutableListOf())
        val deck: CardDeck = CardDeck()

        var failedElections: Int = 0

        val president: GamePlayer get() = players.first(GamePlayer::isPresident)
        val satan: GamePlayer get() = players.first { it.role == ComplexRole.SATAN }
        val demons: List<GamePlayer> get() = players.filter { it.role == ComplexRole.DEMON }
        val angels: List<GamePlayer> get() = players.filter { it.role == ComplexRole.ANGEL }
        val alive: List<GamePlayer> get() = players.filter(GamePlayer::isAlive)

        val isSmallGame: Boolean get() = players.size <= SMALL_GAME_PLAYER_SIZE

        /// If a president is forced to elect the next president, then we must return to the proper order after
        var presidentialElectionPreviousPresidentIndex: Int = -1

        fun toGameOver(winner: SimpleRole, cause: GameOver.Reason) = GameOver(server, players.toMutableList(), winner.name, satan.name, demons.map(Player::name), cause)

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
                    else -> 3
                }

                val shuffledPlayers = originalPlayers.toMutableList()
                shuffledPlayers.shuffle(SecureRandom.getInstanceStrong())

                val demons = List(demonCount) { shuffledPlayers.removeAt(0) }
                val satan = shuffledPlayers.removeAt(0)

                val players = mutableListOf<GamePlayer>()

                players.add(GamePlayer(satan, ComplexRole.SATAN))
                players.addAll(demons.map { GamePlayer(it, ComplexRole.DEMON) })
                players.addAll(shuffledPlayers.map { GamePlayer(it, ComplexRole.ANGEL) })

                players.shuffle(SecureRandom.getInstanceStrong())
                players[0].isPresident = true

                return players
            }
        }
    }

    class GameOver(
        override var server: MutableSharedFlow<ServerOutboundMessage>?,
        override val players: MutableList<Player>,
        val winner: String,
        val satan: PlayerName,
        val demons: List<PlayerName>,
        val reason: Reason
    ) : GameState("game_over") {
        fun toLobby() = Lobby(server, players)

        enum class Reason {
            SATAN_KILLED,
            SATAN_ELECTED_ADVISOR_LATE_GAME,
            POSITIVE_THRESHOLD_REACHED,
            NEGATIVE_THRESHOLD_REACHED,
            ALL_ANGELS_DEAD
        }
    }
}

sealed class InnerGameState(val type: String) {
    class Idle() : InnerGameState("idle")

    class AwaitingRoleConfirmations(val confirmed: MutableList<PlayerName>) : InnerGameState("awaiting_role_confirmations")

    class AwaitingPresidentActionChoice(val action: ActionChoice) :
            InnerGameState("awaiting_president_action_choice")

    class AwaitingPresidentCardDiscard(val cards: List<Card>, val advisorName: PlayerName) :
            InnerGameState("awaiting_president_card_discard")

    class AwaitingAdvisorCardChoice(val cards: List<Card>, val advisorName: PlayerName) :
            InnerGameState("awaiting_advisor_card_choice")

    class AwaitingAdvisorElectionResolution(val nominee: PlayerName) :
            InnerGameState("awaiting_advisor_election_outcome")

    class AwaitingPresidentElectionResolution(val nominee: PlayerName) :
            InnerGameState("awaiting_president_election_outcome")

    class AwaitingInvestigationAnalysis(val target: PlayerName) : InnerGameState("awaiting_investigation_analysis")

    class AwaitingPolicyPeek() : InnerGameState("awaiting_policy_peek")
}
