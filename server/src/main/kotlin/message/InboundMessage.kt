package com.cooper.message

import com.cooper.game.ActionChoice
import com.cooper.game.CardId
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
    JsonSubTypes.Type(value = InboundMessage.ChooseCard::class, name = "choose_card")
)
sealed class InboundMessage {
    /// Chief chooses a player to investigate, kill, or nominate
    class ChooseActionOnPlayer(
        val action: ActionChoice,
        val target: String
    ) : InboundMessage()

    /// Chief chooses one card to discard from deck (of 3) before
    /// being handed to advisor to select
    class DiscardOneCard(val cardId: CardId) : InboundMessage()

    /// Advisor picks one of two cards to play
    class ChooseCard(val cardId: CardId) : InboundMessage()

}

