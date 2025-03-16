package com.cooper.game

typealias CardId = Int

data class Card(
    val id: CardId,
    val description: String,
    val consequence: Int,
    val consequenceQualifier: CardConsequenceQualifier = CardConsequenceQualifier.fromConsequence(consequence)
) {
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
    )
)

class CardDeck(totalCardCount: Int = 17) {
    private val deck: MutableList<Card> = cards.shuffled().take(totalCardCount).toMutableList()

    fun takeThree(): List<Card> {
        require(deck.size >= 3) { "Not enough cards in deck" }
        return List(3) {
            deck.removeAt(0)
        }
    }
}