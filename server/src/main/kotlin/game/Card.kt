package com.cooper.game

import com.cooper.game.Card.Consequence.*

typealias CardId = Int

data class Card(
    val id: CardId,
    val description: String,
    val consequence: Consequence,
) {
    enum class Consequence {
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
        for (i in 3..1000) {
            originalCards = generateCards(i) ?: break
        }

        require(originalCards.isNotEmpty()) { "No cards found to generate a deck" }

        cardStack.addAll(originalCards)
    }

    private fun checkEmptyStack() {
        if (cardStack.size > 3) return

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

    val positiveCardsPlayed: Int get() = playedCards.count { card -> card.consequence == POSITIVE }
    val negativeCardsPlayed: Int get() = playedCards.count { card -> card.consequence == NEGATIVE }
}

val positiveCards get() = cards.filter { it.consequence == POSITIVE }
val negativeCards get() = cards.filter { it.consequence == NEGATIVE }
val neutralCards get() = cards.filter { it.consequence == NEUTRAL }

private const val POSITIVE_RATIO = 0.4
private const val NEGATIVE_RATIO = 0.5

private fun generateCards(deckSize: Int): List<Card>? {
    val positiveCount = (deckSize * POSITIVE_RATIO).toInt()
    val negativeCount = (deckSize * NEGATIVE_RATIO).toInt()
    val neutralCount = deckSize - positiveCount - negativeCount

    if (positiveCount > positiveCards.size) return null
    if (negativeCount > negativeCards.size) return null
    if (neutralCount > neutralCards.size) return null

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
        consequence = NEUTRAL,
    ),
    Card(
        id = 1,
        description = "Kill Elon Musk",
        consequence = POSITIVE
    ),
    Card(
        id = 2,
        description = "Bust a kid for vaping in the bathroom",
        consequence = POSITIVE
    ),
    Card(
        id = 3,
        description = "Get McDonald's",
        consequence = NEUTRAL
    ),
    Card(
        id = 4,
        description = "Hate crime",
        consequence = NEGATIVE
    ),
    Card(
        id = 5,
        description = "Vandalize your badass high school",
        consequence = NEGATIVE
    ),
    Card(
        id = 6,
        description = "Sign up for scientology email list",
        consequence = POSITIVE
    ),
    Card(
        id = 7,
        description = "Eat chip",
        consequence = POSITIVE
    ),
    Card(
        id = 8,
        description = "Play this card if you're a demon",
        consequence = NEGATIVE
    ),
    Card(
        id = 9,
        description = "Buy and sell marbles",
        consequence = POSITIVE
    ),
    Card(
        id = 10,
        description = "Impersonate a cop and pull someone over",
        consequence = POSITIVE
    ),
    Card(
        id = 11,
        description = "Get a job",
        consequence = POSITIVE
    ),
    Card(
        id = 12,
        description = "Steal pinky and the brain statue from your dorm",
        consequence = NEGATIVE
    ),
    Card(
        id = 13,
        description = "Get a tattoo (low class)",
        consequence = NEUTRAL
    ),
    Card(
        id = 14,
        description = "Minecraft in class",
        consequence = POSITIVE
    ),
    Card(
        id = 15,
        description = "You should smile more",
        consequence = POSITIVE
    ),
    Card(
        id = 16,
        description = "Reddit all day long",
        consequence = NEGATIVE
    ),
    Card(
        id = 17,
        description = "Nerd fight!!",
        consequence = POSITIVE
    ),
    Card(
        id = 18,
        description = "Annoying Orange",
        consequence = NEGATIVE
    ),
    Card(
        id = 19,
        description = "Steal his clothes",
        consequence = NEGATIVE
    ),
    Card(
        id = 20,
        description = "Minecraft hoodie",
        consequence = POSITIVE,
    ),
    Card(
        id = 21,
        description = "Fight a cop",
        consequence = NEUTRAL
    ),
    Card(
        id = 22,
        description = "Buy lenovo laptop",
        consequence = NEUTRAL
    ),
    Card(
        id = 23,
        description = "Go to bathroom",
        consequence = POSITIVE
    ),
    Card(
        id = 24,
        description = "Reach for the stars!",
        consequence = NEUTRAL
    ),
    Card(
        id = 25,
        description = "Mike's hard lemonade all night long",
        consequence = POSITIVE
    ),
    Card(
        id = 26,
        description = "Kill beaver",
        consequence = NEUTRAL
    ),
    Card(
        id = 27,
        description = "Buy dog",
        consequence = NEUTRAL
    ),
    Card(
        id = 28,
        description = "Garfield birthday cake",
        consequence = POSITIVE
    ),
    Card(
        id = 29,
        description = "Go to the Minecraft movie with the great merge",
        consequence = POSITIVE
    ),
    Card(
        id = 30,
        description = "Get caught hitting a cart during the Minecraft movie",
        consequence = NEGATIVE
    ),
    Card(
        id = 31,
        description = "Minions movie (GRU)",
        consequence = NEGATIVE
    ),
    Card(
        id = 32,
        description = "Camp in the woods",
        consequence = NEUTRAL
    ),
    Card(
        id = 33,
        description = "Beach week!!!",
        consequence = POSITIVE
    ),
    Card(
        id = 34,
        description = "Bad card",
        consequence = NEGATIVE
    ),
    Card(
        id = 35,
        description = "Gang attack",
        consequence = NEUTRAL
    ),
    Card(
        id = 36,
        description = "Go to collage",
        consequence = NEUTRAL
    ),
    Card(
        id = 37,
        description = "Mr. Kopple returns",
        consequence = POSITIVE
    ),
    Card(
        id = 38,
        description = "Dentist all day",
        consequence = NEGATIVE
    ),
    Card(
        id = 39,
        description = "Noah Wagner would you rather",
        consequence = NEGATIVE
    ),
    Card(
        id = 40,
        description = "Drake fan",
        consequence = NEGATIVE
    ),
    Card(
        id = 41,
        description = "Android phone",
        consequence = NEGATIVE
    ),
    Card(
        id = 42,
        description = "positive card",
        consequence = NEGATIVE
    )
)
// </editor-fold>