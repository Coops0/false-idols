package com.cooper.message

import com.cooper.game.ActionChoice
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class InboundMessageTest {
    private lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setUp() {
        objectMapper = ObjectMapper().registerModule(KotlinModule.Builder().build())
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
    }

    @Test
    fun `test serialization and deserialization of ChooseActionOnPlayer message`() {
        // Given
        val originalMessage = InboundMessage.ChooseActionOnPlayer(
            action = ActionChoice.INVESTIGATE,
            target = "player1"
        )

        // When
        val json = objectMapper.writeValueAsString(originalMessage)
        val deserializedMessage = objectMapper.readValue<InboundMessage>(json)

        // Then
        assertEquals("choose_action", json.substringAfter("\"type\":\"").substringBefore("\""))
        assertEquals(originalMessage.action, (deserializedMessage as InboundMessage.ChooseActionOnPlayer).action)
        assertEquals(originalMessage.target, deserializedMessage.target)
    }

    @Test
    fun `test serialization and deserialization of DiscardOneCard message`() {
        // Given
        val cardId = 5
        val originalMessage = InboundMessage.DiscardOneCard(cardId)

        // When
        val json = objectMapper.writeValueAsString(originalMessage)
        val deserializedMessage = objectMapper.readValue<InboundMessage>(json)

        // Then
        assertEquals("discard_one_card", json.substringAfter("\"type\":\"").substringBefore("\""))
        assertEquals(cardId, (deserializedMessage as InboundMessage.DiscardOneCard).cardId)
    }

    @Test
    fun `test serialization and deserialization of ChooseCard message`() {
        // Given
        val cardId = 3
        val originalMessage = InboundMessage.ChooseCard(cardId)

        // When
        val json = objectMapper.writeValueAsString(originalMessage)
        val deserializedMessage = objectMapper.readValue<InboundMessage>(json)

        // Then
        assertEquals("choose_card", json.substringAfter("\"type\":\"").substringBefore("\""))
        assertEquals(cardId, (deserializedMessage as InboundMessage.ChooseCard).cardId)
    }

    @Test
    fun `test deserialization with invalid type throws exception`() {
        // Given
        val invalidJson = """{"type":"invalid_type","some_field":"someValue"}"""

        // Then
        assertThrows<Exception> {
            objectMapper.readValue<InboundMessage>(invalidJson)
        }
    }

    @Test
    fun `test ChooseActionOnPlayer with all possible action choices`() {
        for (action in ActionChoice.entries) {
            // Given
            val originalMessage = InboundMessage.ChooseActionOnPlayer(
                action = action,
                target = "player2"
            )

            // When
            val json = objectMapper.writeValueAsString(originalMessage)
            val deserializedMessage = objectMapper.readValue<InboundMessage>(json)

            // Then
            assertEquals(action, (deserializedMessage as InboundMessage.ChooseActionOnPlayer).action)
            assertEquals("player2", deserializedMessage.target)
        }
    }

    @Test
    fun `test ChooseActionOnPlayer with empty target`() {
        // Given
        val originalMessage = InboundMessage.ChooseActionOnPlayer(
            action = ActionChoice.KILL,
            target = ""
        )

        // When
        val json = objectMapper.writeValueAsString(originalMessage)
        val deserializedMessage = objectMapper.readValue<InboundMessage>(json)

        // Then
        assertEquals(ActionChoice.KILL, (deserializedMessage as InboundMessage.ChooseActionOnPlayer).action)
        assertEquals("", deserializedMessage.target)
    }

    @Test
    fun `test direct json parsing for ChooseActionOnPlayer`() {
        // Given
        val json = """{"type":"choose_action","action":"INVESTIGATE","target":"player3"}"""

        // When
        val message = objectMapper.readValue<InboundMessage>(json)

        // Then
        message as InboundMessage.ChooseActionOnPlayer
        assertEquals(ActionChoice.INVESTIGATE, message.action)
        assertEquals("player3", message.target)
    }

    @Test
    fun `test direct json parsing for DiscardOneCard`() {
        // Given
        val json = """{"type":"discard_one_card","card_id":66}"""

        // When
        val message = objectMapper.readValue<InboundMessage>(json)

        // Then
        message as InboundMessage.DiscardOneCard
        assertEquals(66, message.cardId)
    }

    @Test
    fun `test direct json parsing for ChooseCard`() {
        // Given
        val json = """{"type":"choose_card","card_id":5}"""

        // When
        val message = objectMapper.readValue<InboundMessage>(json)

        // Then
        message as InboundMessage.ChooseCard
        assertEquals(5, message.cardId)
    }
}
