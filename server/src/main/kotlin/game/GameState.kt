package com.cooper.game

import com.cooper.SocketContentConverterSender
import com.cooper.message.OutboundMessage
import com.cooper.message.StrippedPlayer
import com.cooper.message.StrippedPlayer.Companion.stripped
import com.cooper.message.server.ServerOutboundMessage

sealed class GameState {
    abstract val name: String
    abstract val server: SocketContentConverterSender
    abstract val players: List<Player>

    suspend fun sendServer(message: ServerOutboundMessage) {
        server.send(message)
    }

    operator fun get(playerName: PlayerName): Player? {
        return players.firstOrNull { it.name == playerName }
    }

    suspend fun broadcast(message: OutboundMessage) {
        players.forEach { it.send(message) }
    }

    suspend fun send(playerName: PlayerName, message: OutboundMessage) {
        players.firstOrNull { it.name == playerName }?.send(message)
    }

    class Lobby(
        override val server: SocketContentConverterSender,
        override val players: MutableList<Player> = mutableListOf()
    ) : GameState() {
        override val name: String = "lobby"

        fun toGameInProgress() = GameInProgress(server, players)
    }

    class GameInProgress private constructor(
        override val server: SocketContentConverterSender,
        override val players: MutableList<GamePlayer>
    ) : GameState() {
        override val name: String = "game_in_progress"

        constructor(server: SocketContentConverterSender, originalPlayers: List<Player>) : this(
            server,
            assignPlayerRoles(originalPlayers)
        )

        var innerGameState: InnerGameState = InnerGameState.AwaitingPlayerActionChoice()
        val deck: CardDeck = CardDeck()

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
            get() = deck.absolutePoints >= 6

        fun toGameOver(winner: SimpleRole, cause: GameOverCause) = GameOver(
            server,
            players.toMutableList(),
            winner,
            satan.stripped,
            demons.map { StrippedPlayer(it.name, it.icon.iconName) },
            cause
        )

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
        override val server: SocketContentConverterSender,
        override val players: MutableList<Player>,
        val winner: SimpleRole,
        val satan: StrippedPlayer,
        val demons: List<StrippedPlayer>,
        val cause: GameOverCause
    ) : GameState() {
        override val name: String = "game_over"
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
