package com.cooper

import com.cooper.game.PlayerName

@Suppress("MemberVisibilityCanBePrivate", "CanBeParameter") class FalseIdolsError(
    val errorType: ErrorType,
    val playerName: PlayerName?,
    val message: String
) {
    init {
        println("Error: ${errorType.errorName()} - ${playerName ?: "server"} - $message")
    }

    enum class ErrorType {
        ASSERTION,
        ILLEGAL_ARGUMENT,
        ILLEGAL_STATE;

        fun errorName() = when (this) {
            ASSERTION -> "Assertion Error"
            ILLEGAL_ARGUMENT -> "Illegal Argument Error"
            ILLEGAL_STATE -> "Illegal State Error"
        }
    }

    companion object {
        fun assertionError(player: PlayerName? = null, message: String?) =
            FalseIdolsError(ErrorType.ASSERTION, player, message ?: "?")

        fun illegalArgument(player: PlayerName? = null, message: String?) =
            FalseIdolsError(ErrorType.ILLEGAL_ARGUMENT, player, message ?: "?")

        fun illegalState(player: PlayerName? = null, message: String?) =
            FalseIdolsError(ErrorType.ILLEGAL_STATE, player, message ?: "?")
    }
}