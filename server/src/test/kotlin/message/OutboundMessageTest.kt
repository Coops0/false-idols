package com.cooper.message

import com.cooper.game.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class OutboundMessageTest {
    private lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setUp() {
        objectMapper = ObjectMapper().registerModule(KotlinModule.Builder().build())
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
    }

    @Test
    fun `test serialization and deserialization of AssignRole message`() {
        // Given
        val teammates = listOf(
            OutboundMessage.StrippedPlayer("player2", "icon2"),
            OutboundMessage.StrippedPlayer("player3", "icon3")
        )
        val satan = OutboundMessage.StrippedPlayer("satan", "devil_icon")

        val originalMessage = OutboundMessage.AssignRole(
            role = ComplexRole.DEMON,
            isChief = true,
            demonCount = 2,
            teammates = teammates,
            satan = satan
        )

        // When
        val json = objectMapper.writeValueAsString(originalMessage)
        val deserializedMessage = objectMapper.readValue<OutboundMessage.AssignRole>(json)

        // Then
        assertEquals("assign_role", json.substringAfter("\"type\":\"").substringBefore("\""))
        assertEquals(ComplexRole.DEMON, deserializedMessage.role)
        assertEquals(true, deserializedMessage.isChief)
        assertEquals(2, deserializedMessage.demonCount)
        assertEquals(2, deserializedMessage.teammates?.size)
        assertEquals("player2", deserializedMessage.teammates?.get(0)?.name)
        assertEquals("satan", deserializedMessage.satan?.name)
    }

    @Test
    fun `test serialization and deserialization of RequestActionChoice message`() {
        // Given
        val players = listOf(
            OutboundMessage.RequestActionChoice.ActionSupplementedPlayer(
                name = "player1",
                icon = "icon1",
                investigatable = true,
                electable = false
            ),
            OutboundMessage.RequestActionChoice.ActionSupplementedPlayer(
                name = "player2",
                icon = "icon2",
                investigatable = false,
                electable = true
            )
        )

        val originalMessage = OutboundMessage.RequestActionChoice(
            permittedActions = listOf(ActionChoice.INVESTIGATE, ActionChoice.NOMINATE),
            players = players
        )

        // When
        val json = objectMapper.writeValueAsString(originalMessage)
        val deserializedMessage = objectMapper.readValue<OutboundMessage.RequestActionChoice>(json)

        // Then
        assertEquals("request_action", json.substringAfter("\"type\":\"").substringBefore("\""))
        assertEquals(2, deserializedMessage.permittedActions.size)
        assertTrue(deserializedMessage.permittedActions.contains(ActionChoice.INVESTIGATE))
        assertTrue(deserializedMessage.permittedActions.contains(ActionChoice.NOMINATE))
        assertEquals(2, deserializedMessage.players.size)
        assertTrue(deserializedMessage.players[0].investigatable)
        assertFalse(deserializedMessage.players[0].electable)
        assertFalse(deserializedMessage.players[1].investigatable)
        assertTrue(deserializedMessage.players[1].electable)
    }

    @Test
    fun `test serialization and deserialization of RequestChiefCardDiscard message`() {
        // Given
        val cards = listOf(
            Card(1, "t", 1),
            Card(2, "bet", -2),
            Card(3, "tet", 0)
        )

        val originalMessage = OutboundMessage.RequestChiefCardDiscard(cards)

        // When
        val json = objectMapper.writeValueAsString(originalMessage)
        val deserializedMessage = objectMapper.readValue<OutboundMessage.RequestChiefCardDiscard>(json)

        // Then
        assertEquals("request_chief_card_discard", json.substringAfter("\"type\":\"").substringBefore("\""))
        assertEquals(3, deserializedMessage.cards.size)
        assertEquals(1, deserializedMessage.cards[0].id)
        assertEquals("t", deserializedMessage.cards[0].description)
        assertEquals(1, deserializedMessage.cards[0].consequence)
        assertEquals(2, deserializedMessage.cards[1].id)
        assertEquals("bet", deserializedMessage.cards[1].description)
        assertEquals(-2, deserializedMessage.cards[1].consequence)
        assertEquals(3, deserializedMessage.cards[2].id)
        assertEquals("tet", deserializedMessage.cards[2].description)
        assertEquals(0, deserializedMessage.cards[2].consequence)
    }

    @Test
    fun `test serialization and deserialization of RequestAdvisorCardChoice message`() {
        // Given
        val cards = listOf(
            Card(4, "joe", 1),
            Card(5, "tedt", -1)
        )

        val originalMessage = OutboundMessage.RequestAdvisorCardChoice(cards)

        // When
        val json = objectMapper.writeValueAsString(originalMessage)
        val deserializedMessage = objectMapper.readValue<OutboundMessage.RequestAdvisorCardChoice>(json)

        // Then
        assertEquals("request_advisor_card_choice", json.substringAfter("\"type\":\"").substringBefore("\""))
        assertEquals(2, deserializedMessage.cards.size)
        assertEquals(4, deserializedMessage.cards[0].id)
        assertEquals("joe", deserializedMessage.cards[0].description)
        assertEquals(1, deserializedMessage.cards[0].consequence)
        assertEquals(5, deserializedMessage.cards[1].id)
        assertEquals("tedt", deserializedMessage.cards[1].description)
    }

    @Test
    fun `test serialization and deserialization of InvestigationResult message`() {
        // Given
        val target = OutboundMessage.StrippedPlayer("player1", "icon1")
        val originalMessage = OutboundMessage.InvestigationResult(
            target = target,
            simpleRole = SimpleRole.ANGEL
        )

        // When
        val json = objectMapper.writeValueAsString(originalMessage)
        val deserializedMessage = objectMapper.readValue<OutboundMessage.InvestigationResult>(json)

        // Then
        assertEquals("investigation_result", json.substringAfter("\"type\":\"").substringBefore("\""))
        assertEquals("player1", deserializedMessage.target.name)
        assertEquals("icon1", deserializedMessage.target.icon)
        assertEquals(SimpleRole.ANGEL, deserializedMessage.simpleRole)
    }

    @Test
    fun `test serialization and deserialization of Disconnect message`() {
        // Given
        val originalMessage = OutboundMessage.Disconnect(
            reason = OutboundMessage.Disconnect.DisconnectionReason.HOST_RESET_PLAYERS
        )

        // When
        val json = objectMapper.writeValueAsString(originalMessage)
        val deserializedMessage = objectMapper.readValue<OutboundMessage.Disconnect>(json)

        // Then
        assertEquals("disconnect", json.substringAfter("\"type\":\"").substringBefore("\""))
        assertEquals(OutboundMessage.Disconnect.DisconnectionReason.HOST_RESET_PLAYERS, deserializedMessage.reason)
    }

    @Test
    fun `test RequestChiefCardDiscard with invalid card count throws exception`() {
        // Given
        val cards = listOf(
            Card(1, "t", 1),
            Card(2, "bet", -2)
        )

        // Then
        assertThrows<AssertionError> {
            OutboundMessage.RequestChiefCardDiscard(cards)
        }
    }

    @Test
    fun `test RequestAdvisorCardChoice with invalid card count throws exception`() {
        // Given
        val cards = listOf(
            Card(4, "joe", 1),
        )

        // Then
        assertThrows<AssertionError> {
            OutboundMessage.RequestAdvisorCardChoice(cards)
        }
    }

    @Test
    fun `test RequestActionChoice with empty players list throws exception`() {
        // Given
        val players = emptyList<OutboundMessage.RequestActionChoice.ActionSupplementedPlayer>()

        // Then
        assertThrows<AssertionError> {
            OutboundMessage.RequestActionChoice(
                permittedActions = listOf(ActionChoice.INVESTIGATE),
                players = players
            )
        }
    }

    @Test
    fun `test direct json parsing for AssignRole message`() {
        // Given
        val json = """
            {
                "type": "assign_role",
                "role": "ANGEL",
                "is_chief": false,
                "demon_count": 1
            }
        """.trimIndent()

        // When
        val message = objectMapper.readValue<OutboundMessage.AssignRole>(json)

        // Then
        assertEquals(ComplexRole.ANGEL, message.role)
        assertFalse(message.isChief)
        assertEquals(1, message.demonCount)
        assertNull(message.teammates)
        assertNull(message.satan)
    }
}
