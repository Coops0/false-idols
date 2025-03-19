import com.cooper.game.Card
import com.cooper.game.CardDeck
import com.cooper.game.positiveCards
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.math.absoluteValue

class CardDeckTest {
    @RepeatedTest(10)
    fun `test random distribution of card types`() {
        // Create multiple decks to verify random distribution
        val decks = List(20) { CardDeck(17) }

        // For each deck, play all cards and count types
        val typeCounts = decks.map { deck ->
            repeat(17) { deck.playOneBlind() }

            val positiveCount = deck.playedCards.count { it.consequenceQualifier == Card.CardConsequenceQualifier.POSITIVE }
            val negativeCount = deck.playedCards.count { it.consequenceQualifier == Card.CardConsequenceQualifier.NEGATIVE }
            val neutralCount = deck.playedCards.count { it.consequenceQualifier == Card.CardConsequenceQualifier.NEUTRAL }

            Triple(positiveCount, negativeCount, neutralCount)
        }

        // Verify that we have variation in the distributions
        val uniqueDistributions = typeCounts.toSet()
        assertTrue(
            uniqueDistributions.size > 1,
            "All decks have the same distribution: ${uniqueDistributions.first()}"
        )

        // Verify that distributions are within reasonable bounds
        typeCounts.forEach { (positive, negative, neutral) ->
            // Positive should be between 25%-45%
            assertTrue(positive in 4..7, "Positive count $positive is outside reasonable range")

            // Negative should be between 40%-60%
            assertTrue(negative in 6..10, "Negative count $negative is outside reasonable range")

            // Total should be 17
            assertEquals(17, positive + negative + neutral)
        }
    }

    @Test
    fun `test deck reshuffles when empty`() {
        // Create a small deck
        val deck = CardDeck(6)

        // Pick all cards
        deck.pickAndTakeThree()
        deck.pickAndTakeThree()

        assertEquals(6, deck.playedCards.size)

        // This should reshuffle the played cards back into the deck
        val newCards = deck.pickAndTakeThree()

        assertEquals(3, newCards.size)
        // Played cards should still contain all cards that have been played
        assertEquals(9, deck.playedCards.size)
    }
}
