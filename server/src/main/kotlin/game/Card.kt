package com.cooper.game

import com.cooper.game.Card.CardConsequenceQualifier.*
import kotlin.math.absoluteValue

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

class CardDeck {
    @Suppress("MemberVisibilityCanBePrivate") val cardStack: MutableList<Card> = mutableListOf()
    private var originalCards: List<Card> = listOf()
    val playedCards = mutableListOf<Card>()

    init {
        // get the largest possible deck size
        for (i in 17..1000) {
            originalCards = generateCards(i) ?: break
        }

        cardStack.addAll(originalCards)
    }

    private fun checkEmptyStack() {
        if (cardStack.size >= 3) return

        // don't clear played cards, they are used for scoring

        // prevent any duplicates
        cardStack.clear()
        cardStack.addAll(originalCards)

        cardStack.shuffle()
    }

    fun pickAndTakeThree(): List<Card> {
        checkEmptyStack()

        return List(3) {
            cardStack.removeAt(0)
        }
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

private const val POSITIVE_RATIO = 0.4
private const val NEGATIVE_RATIO = 0.5
private const val NEUTRAL_RATIO = 1 - POSITIVE_RATIO - NEGATIVE_RATIO

private fun generateCards(deckSize: Int): List<Card>? {
    val positiveCount = (deckSize * POSITIVE_RATIO).toInt()
    val negativeCount = (deckSize * NEGATIVE_RATIO).toInt()
    val neutralCount = deckSize - positiveCount - negativeCount

    if (positiveCount <= positiveCards.size) return null
    if (negativeCount <= negativeCards.size) return null
    if (neutralCount <= neutralCards.size) return null

    return listOf(
        *positiveCards.shuffled().take(positiveCount).toTypedArray(),
        *negativeCards.shuffled().take(negativeCount).toTypedArray(),
        *neutralCards.shuffled().take(neutralCount).toTypedArray(),
    ).shuffled()
}

// <editor-fold desc="Card definitions">
val cards = listOf(
    Card(
        id = 0,
        description = "Give everyone aids",
        consequence = -1
    ),
    Card(
        id = 1,
        description = "Kill Elon Musk",
        consequence = 1
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
        consequence = -1
    ),
    Card(
        id = 5,
        description = "Vandalize your badass high school",
        consequence = -1
    ),
    Card(
        id = 6,
        description = "Sign up for scientology email list",
        consequence = 1
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
        consequence = 1
    ),
    Card(
        id = 18,
        description = "Annoying Orange",
        consequence = -1
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
    ),
    Card(
        id = 32,
        description = "Camp in the woods",
        consequence = 0
    ),
    Card(
        id = 33,
        description = "Beach week!!!",
        consequence = 1
    ),
    Card(
        id = 34,
        description = "Bad card",
        consequence = -2
    ),
    Card(
        id = 35,
        description = "Gang attack",
        consequence = 0
    ),
    Card(
        id = 36,
        description = "Go to collage",
        consequence = 0
    ),
    Card(
        id = 37,
        description = "Mr. Kopple returns",
        consequence = 1
    ),
    Card(
        id = 38,
        description = "Dentist all day",
        consequence = -1
    ),
)
// </editor-fold>