package com.cooper.game

import com.cooper.message.OutboundMessage
import com.cooper.message.server.ServerOutboundMessage
import kotlinx.coroutines.channels.Channel
import kotlin.math.absoluteValue

abstract class GameState<P : Player>(
    val name: String,
    val server: Channel<ServerOutboundMessage>,
    open val players: MutableList<P>
) {
    suspend fun broadcast(message: OutboundMessage) {
        players.forEach { it.send(message) }
    }

    suspend fun send(playerName: PlayerName, message: OutboundMessage) {
        players.firstOrNull { it.name == playerName }?.send(message)
    }

    suspend fun sendServer(message: ServerOutboundMessage) {
        server.send(message)
    }
}

class LobbyGameState(server: Channel<ServerOutboundMessage>) : GameState<Player>("lobby", server, mutableListOf())

class GameInProgressGameState private constructor(
    server: Channel<ServerOutboundMessage>,
    override val players: MutableList<GamePlayer>
) : GameState<GamePlayer>("game_in_progress", server, players) {
    constructor(previousState: GameState<Player>) : this(
        previousState.server,
        assignPlayerRoles(previousState.players)
    )

    var innerGameState: InnerGameState = AwaitingPlayerActionChoiceInnerGameState()
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

    companion object {
        abstract class InnerGameState(val name: String)

        class AwaitingPlayerActionChoiceInnerGameState(
            val forcedAction: ActionChoice? = null,
            val cause: PlayerActionChoiceCause = PlayerActionChoiceCause.NORMAL_CHIEF,
        ) : InnerGameState("awaiting_chief_action_choice") {
            enum class PlayerActionChoiceCause {
                NORMAL_CHIEF,
                FORCED_CHIEF
            }
        }

        class AwaitingChiefCardDiscardInnerGameState(val cards: Array<Card>) :
                InnerGameState("awaiting_chief_card_discard")

        class AwaitingAdvisorCardChoiceInnerGameState(val cards: Array<Card>) :
                InnerGameState("awaiting_advisor_card_choice")

        class AwaitingElectionOutcomeInnerGameState(val nominee: PlayerName) :
                InnerGameState("awaiting_election_outcome")

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

class GameOverGameState(
    server: Channel<ServerOutboundMessage>,
    players: MutableList<Player>,
    val winner: SimpleRole,
    val satan: PlayerName,
    val demons: List<PlayerName>,
    val cause: GameOverCause
) : GameState<Player>("game_over", server, players) {
    constructor(gameInProgress: GameInProgressGameState, winner: SimpleRole, cause: GameOverCause) : this(
        gameInProgress.server,
        players = gameInProgress.players.toMutableList(),
        winner,
        satan = gameInProgress.satan.name,
        demons = gameInProgress.demons.map(GamePlayer::name),
        cause
    )

    enum class GameOverCause {
        SATAN_KILLED,
        SATAN_ELECTED,
        POSITIVE_THRESHOLD_REACHED,
        ALL_ANGELS_DEAD,
    }
}