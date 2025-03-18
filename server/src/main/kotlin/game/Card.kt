package com.cooper.game

import kotlin.math.absoluteValue

typealias CardId = Int

data class Card(
    val id: CardId,
    val description: String,
    val consequence: Int,
) {
    val consequenceQualifier: CardConsequenceQualifier = CardConsequenceQualifier.fromConsequence(consequence)

    enum class CardConsequenceQualifier {
        POSITIVE,
        NEGATIVE,
        NEUTRAL;

        companion object {
            fun fromConsequence(consequence: Int): CardConsequenceQualifier = when {
                consequence > 0 -> POSITIVE
                consequence < 0 -> NEGATIVE
                else -> NEUTRAL
            }
        }
    }
}

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
        description = "",
        consequence = 1
    )
    // TODO add more cards
)

class CardDeck(totalCardCount: Int = 17) {
    private val cardStack: MutableList<Card> = cards.shuffled().take(totalCardCount).toMutableList()
    val playedCards = mutableListOf<Card>()

    fun pickAndTakeThree(): List<Card> {
        if (cardStack.size < 3) {
            cardStack.addAll(playedCards)
            playedCards.clear()

            cardStack.shuffle()
        }

        val cards = List(3) {
            cardStack.removeAt(0)
        }

        playedCards.addAll(cards)
        return cards
    }

    val points: Int get() = playedCards.sumOf(Card::consequence)

    val absolutePoints: Int get() = playedCards.sumOf { it.consequence.absoluteValue }
}