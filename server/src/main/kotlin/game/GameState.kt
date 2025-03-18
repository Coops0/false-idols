package com.cooper.game

import com.cooper.SocketContentConverterSender
import com.cooper.message.OutboundMessage
import com.cooper.message.server.ServerOutboundMessage
import com.fasterxml.jackson.annotation.JsonIgnore

sealed class GameState {
    abstract val name: String
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
    ) : GameState() {
        override val name: String = "lobby"

        fun toGameInProgress() = GameInProgress(server, players)
    }

    class GameInProgress private constructor(
        override val server: SocketContentConverterSender<ServerOutboundMessage>,
        override val players: MutableList<GamePlayer>
    ) : GameState() {
        override val name: String = "game_in_progress"

        constructor(server: SocketContentConverterSender<ServerOutboundMessage>, originalPlayers: List<Player>) :
                this(server, assignPlayerRoles(originalPlayers))

        var innerGameState: InnerGameState = InnerGameState.PostRoleGracePeriod()
        val deck: CardDeck = CardDeck()

        var failedElections: Int = 0

        val chief: GamePlayer get() = players.first(GamePlayer::isChief)
        val satan: GamePlayer get() = players.first { it.role == ComplexRole.SATAN }
        val demons: List<GamePlayer> get() = players.filter { it.role == ComplexRole.DEMON }
        val angels: List<GamePlayer> get() = players.filter { it.role == ComplexRole.ANGEL }
        val alive: List<GamePlayer> get() = players.filter(GamePlayer::isAlive)

        /// 6+ cards played
        val isLateGame: Boolean get() = deck.absolutePoints >= 6

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
    ) : GameState() {
        override val name: String = "game_over"

        fun toLobby() = Lobby(server, players)
    }

    enum class GameOverReason {
        SATAN_KILLED,
        SATAN_ELECTED_ADVISOR_LATE_GAME,
        POSITIVE_THRESHOLD_REACHED,
        ALL_ANGELS_DEAD,
        DECK_EMPTY
    }
}

sealed class InnerGameState {
    abstract val name: String

    class PostRoleGracePeriod : InnerGameState() {
        override val name: String = "post_role_grace_period"
    }

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

    class AwaitingChiefCardDiscard(val cards: List<Card>, val advisorName: String) : InnerGameState() {
        override val name: String = "awaiting_chief_card_discard"
    }

    class AwaitingAdvisorCardChoice(val cards: List<Card>, val advisorName: String) : InnerGameState() {
        override val name: String = "awaiting_advisor_card_choice"
    }

    class AwaitingElectionResolution(val nominee: PlayerName) : InnerGameState() {
        override val name: String = "awaiting_election_outcome"
    }
}
