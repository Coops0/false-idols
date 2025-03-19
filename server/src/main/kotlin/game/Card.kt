package com.cooper.game

import com.cooper.game.Card.CardConsequenceQualifier.*
import kotlin.math.absoluteValue
import kotlin.random.Random

typealias CardId = Int

data class Card(
    val id: CardId,
    val description: String,
    val consequence: Int,
) {
    val consequenceQualifier: CardConsequenceQualifier = when {
        consequence > 0 -> POSITIVE
        consequence < 0 -> NEGATIVE
        else -> NEUTRAL
    }

    enum class CardConsequenceQualifier {
        POSITIVE,
        NEGATIVE,
        NEUTRAL
    }
}

private const val VARIATION_RANGE = 0.1
private fun randomVariation() = Random.nextDouble(-VARIATION_RANGE, VARIATION_RANGE)

private const val BASE_POSITIVE_RATIO = 0.35
private const val BASE_NEGATIVE_RATIO = 0.5

class CardDeck(totalCardCount: Int = 17) {
    private val cardStack: MutableList<Card> = mutableListOf()
    val playedCards = mutableListOf<Card>()

    init {
        val positiveRatio = BASE_POSITIVE_RATIO + randomVariation()
        val negativeRatio = BASE_NEGATIVE_RATIO + randomVariation()

        val positiveCount = (totalCardCount * positiveRatio).toInt()
        val negativeCount = (totalCardCount * negativeRatio).toInt()
        val neutralCount = totalCardCount - positiveCount - negativeCount

        assert(positiveCount <= positiveCards.size) { "Not enough positive cards" }
        assert(negativeCount <= negativeCards.size) { "Not enough negative cards" }
        assert(neutralCount <= neutralCards.size) { "Not enough neutral cards" }

        cardStack.addAll(positiveCards.shuffled().take(positiveCount))
        cardStack.addAll(negativeCards.shuffled().take(negativeCount))
        cardStack.addAll(neutralCards.shuffled().take(neutralCount))

        assert(cardStack.size == totalCardCount) { "Card deck size mismatch" }

        cardStack.shuffle()
    }

    private fun checkEmptyStack(len: Int = 1) {
        if (cardStack.size >= len) return

        // don't clear played cards, they are used for scoring
        cardStack.addAll(playedCards)
        cardStack.shuffle()
    }

    fun pickAndTakeThree(): List<Card> {
        checkEmptyStack(3)

        val cards = List(3) {
            cardStack.removeAt(0)
        }

        playedCards.addAll(cards)
        return cards
    }

    fun playOneBlind() {
        checkEmptyStack()
        playedCards.add(cardStack.removeAt(0))
    }

    val points: Int get() = playedCards.sumOf(Card::consequence)
    val absolutePoints: Int get() = playedCards.sumOf { it.consequence.absoluteValue }
}

val positiveCards get() = cards.filter { it.consequenceQualifier == POSITIVE }
val negativeCards get() = cards.filter { it.consequenceQualifier == NEGATIVE }
val neutralCards get() = cards.filter { it.consequenceQualifier == NEUTRAL }

val cards = listOf(
    Card(
        id = 0,
        description = "Give everyone aids",
        consequence = -3
    ),
    Card(
        id = 1,
        description = "Kill Elon Musk",
        consequence = 2
    ),
    Card(
        id = 2,
        description = "Bust a kid for vaping in the bathroom",
        consequence = 1
    ),
    Card(
        id = 3,
        description = "Get McDonald's",
        consequence = 0
    ),
    Card(
        id = 4,
        description = "Hate crime",
        consequence = -2
    ),
    Card(
        id = 5,
        description = "Vandalize your badass high school",
        consequence = -1
    ),
    Card(
        id = 6,
        description = "Sign up for scientology email list",
        consequence = 2
    ),
    Card(
        id = 7,
        description = "Eat chip",
        consequence = 1
    ),
    Card(
        id = 8,
        description = "Play this card if you're a demon",
        consequence = -1
    ),
    Card(
        id = 9,
        description = "Buy and sell marbles",
        consequence = 1
    ),
    Card(
        id = 10,
        description = "Impersonate a cop and pull someone over",
        consequence = 1
    ),
    Card(
        id = 11,
        description = "Get a job",
        consequence = 1
    ),
    Card(
        id = 12,
        description = "Steal pinky and the brain statue from your dorm",
        consequence = -1
    ),
    Card(
        id = 13,
        description = "Get a tattoo (low class)",
        consequence = 0
    ),
    Card(
        id = 14,
        description = "Minecraft in class",
        consequence = 1
    ),
    Card(
        id = 15,
        description = "You should smile more",
        consequence = 1
    ),
    Card(
        id = 16,
        description = "Reddit all day long",
        consequence = -1
    ),
    Card(
        id = 17,
        description = "Nerd fight!!",
        consequence = 2
    ),
    Card(
        id = 18,
        description = "Annoying Orange",
        consequence = -5
    ),
    Card(
        id = 19,
        description = "Steal his clothes",
        consequence = -1
    ),
    Card(
        id = 20,
        description = "Minecraft hoodie",
        consequence = 1,
    ),
    Card(
        id = 21,
        description = "Fight a cop",
        consequence = 0
    ),
    Card(
        id = 22,
        description = "Buy lenovo laptop",
        consequence = 0
    ),
    Card(
        id = 23,
        description = "Go to bathroom",
        consequence = 1
    ),
    Card(
        id = 24,
        description = "Reach for the stars!",
        consequence = 0
    ),
    Card(
        id = 25,
        description = "Mike's hard lemonade all night long",
        consequence = 1
    ),
    Card(
        id = 26,
        description = "Kill beaver",
        consequence = 0
    ),
    Card(
        id = 27,
        description = "Buy dog",
        consequence = 0
    ),
    Card(
        id = 28,
        description = "Garfield birthday cake",
        consequence = 1
    ),
    Card(
        id = 29,
        description = "Go to the Minecraft movie with the great merge",
        consequence = 1
    ),
    Card(
        id = 30,
        description = "Get caught hitting a cart during the Minecraft movie",
        consequence = -1
    ),
    Card(
        id = 31,
        description = "Minions movie (GRU)",
        consequence = -1
    )
)