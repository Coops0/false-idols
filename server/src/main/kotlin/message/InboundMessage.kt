package com.cooper.message

import com.cooper.game.CardId
import com.cooper.game.PlayerName
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = InboundMessage.ChooseActionOnPlayer::class, name = "choose_action"),
    JsonSubTypes.Type(value = InboundMessage.DiscardOneCard::class, name = "discard_one_card"),
    JsonSubTypes.Type(value = InboundMessage.ChooseCard::class, name = "choose_card"),
    JsonSubTypes.Type(value = InboundMessage.Ping::class, name = "ping"),
)
sealed class InboundMessage {
    /// President chooses a player to execute an action upon
    class ChooseActionOnPlayer(val target: PlayerName) : InboundMessage()

    /// President chooses one card to discard from deck (of 3) before
    /// being handed to advisor to select
    class DiscardOneCard(val cardId: CardId) : InboundMessage()

    /// Advisor picks one of two cards to play
    class ChooseCard(val cardId: CardId) : InboundMessage()

    class Ping(val requestIcon: Boolean = false, val isIdle: Boolean = false) : InboundMessage()
}