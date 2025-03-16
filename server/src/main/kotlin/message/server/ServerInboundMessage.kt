package com.cooper.message.server

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = ResetPlayersServerInboundMessage::class, name = "reset_players"),
    JsonSubTypes.Type(value = StartGameServerInboundMessage::class, name = "start_game"),
    JsonSubTypes.Type(value = ResolveElectionServerInboundMessage::class, name = "resolve_election"),
    JsonSubTypes.Type(value = SkipServerInboundMessage::class, name = "skip"),
    JsonSubTypes.Type(value = GoBackToLobbyServerInboundMessage::class, name = "go_back_to_lobby")
)
abstract class ServerInboundMessage

class ResetPlayersServerInboundMessage : ServerInboundMessage()

class StartGameServerInboundMessage : ServerInboundMessage()

class ResolveElectionServerInboundMessage(val passed: Boolean) : ServerInboundMessage()

class SkipServerInboundMessage(val passed: Boolean) : ServerInboundMessage()

class GoBackToLobbyServerInboundMessage : ServerInboundMessage()