package com.cooper.message.server

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = ServerInboundMessage.ResetPlayers::class, name = "reset_players"),
    JsonSubTypes.Type(value = ServerInboundMessage.Kick::class, name = "kick"),
    JsonSubTypes.Type(value = ServerInboundMessage.StartGame::class, name = "start_game"),
    JsonSubTypes.Type(value = ServerInboundMessage.ResolveElection::class, name = "resolve_election"),
    JsonSubTypes.Type(value = ServerInboundMessage.Skip::class, name = "skip"),
    JsonSubTypes.Type(value = ServerInboundMessage.GoBackToLobby::class, name = "go_back_to_lobby"),
    JsonSubTypes.Type(value = ServerInboundMessage.Ping::class, name = "ping"),
)
sealed class ServerInboundMessage {
    class ResetPlayers : ServerInboundMessage()

    class Kick(val playerName: String) : ServerInboundMessage()

    class StartGame : ServerInboundMessage()

    class ResolveElection(val passed: Boolean) : ServerInboundMessage()

    class Skip : ServerInboundMessage()

    class GoBackToLobby : ServerInboundMessage()

    /// After 5 negative cards, after the advisor receives their cards,
    /// if the president and advisor agree, they can just nullify all the cards
    class Veto : ServerInboundMessage()

    class Ping(val requestState: Boolean = false) : ServerInboundMessage()
}