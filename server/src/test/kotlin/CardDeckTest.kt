import com.cooper.game.Card
import com.cooper.game.CardDeck
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.math.absoluteValue

class CardDeckTest {
    @Test
    fun `test initial deck creation`() {
        val deck = CardDeck()
        assertEquals(0, deck.playedCards.size)
        assertEquals(0, deck.points)
        assertEquals(0, deck.absolutePoints)
    }

    @ParameterizedTest
    @ValueSource(ints = [10, 17, 25])
    fun `test deck balance with different sizes`(deckSize: Int) {
        // Create multiple decks to test balance statistically
        val decks = List(100) { CardDeck(deckSize) }

        // For each deck, pick all cards and check balance
        decks.forEach { deck ->
            // Pick all cards
            repeat(deckSize / 3) {
                deck.pickAndTakeThree()
            }

            // Pick remaining cards one by one
            repeat(deckSize % 3) {
                deck.playOne()
            }

            // Check that the total points are within a reasonable range
            assertTrue(
                deck.points.absoluteValue <= deckSize / 2,
                "Deck with $deckSize cards has unbalanced points: ${deck.points}"
            )

            // Check distribution of card types
            val positiveCards =
                deck.playedCards.count { it.consequenceQualifier == Card.CardConsequenceQualifier.POSITIVE }
            val negativeCards =
                deck.playedCards.count { it.consequenceQualifier == Card.CardConsequenceQualifier.NEGATIVE }
            val neutralCards =
                deck.playedCards.count { it.consequenceQualifier == Card.CardConsequenceQualifier.NEUTRAL }

            // Check that positive and negative cards are roughly balanced
            assertTrue(
                (positiveCards - negativeCards).absoluteValue <= deckSize / 3,
                "Deck has unbalanced positive/negative distribution: +$positiveCards/-$negativeCards"
            )
        }
    }

    @Test
    fun `test pickAndTakeThree returns three cards`() {
        val deck = CardDeck()
        val pickedCards = deck.pickAndTakeThree()

        assertEquals(3, pickedCards.size)
        assertEquals(3, deck.playedCards.size)
    }

    @Test
    fun `test playOne adds one card to played cards`() {
        val deck = CardDeck()
        deck.playOne()

        assertEquals(1, deck.playedCards.size)
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
        assertEquals(3, deck.playedCards.size)
    }

    @RepeatedTest(10)
    fun `test statistical balance of consequences`() {
        // Create multiple decks to analyze statistically
        val totalDecks = 100
        val deckSize = 17
        val decks = List(totalDecks) { CardDeck(deckSize) }

        // Calculate average points across all decks
        val averagePoints = decks.map { deck ->
            // Pick all cards
            repeat(deckSize / 3) { deck.pickAndTakeThree() }
            repeat(deckSize % 3) { deck.playOne() }
            deck.points
        }.average()

        // The average across many decks should be close to zero for a balanced deck
        assertTrue(
            averagePoints.absoluteValue <= 1.0,
            "Average points across $totalDecks decks is $averagePoints, which is not balanced"
        )
    }

    @Test
    fun `test points calculation`() {
        val deck = CardDeck()

        val pickedCards = deck.pickAndTakeThree()
        val expectedPoints = pickedCards.sumOf(Card::consequence)

        assertEquals(expectedPoints, deck.points)
    }

    @Test
    fun `test absolutePoints calculation`() {
        val deck = CardDeck()

        val pickedCards = deck.pickAndTakeThree()
        val expectedAbsPoints = pickedCards.sumOf { it.consequence.absoluteValue }

        assertEquals(expectedAbsPoints, deck.absolutePoints)
    }
}
