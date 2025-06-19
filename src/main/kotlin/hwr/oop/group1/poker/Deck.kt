package hwr.oop.group1.poker

class Deck(
    private val cards: MutableList<Card> = mutableListOf(),
) {


    init {
        // Fill and Shuffle the deck if it is empty
        if (cards.isEmpty()) {
            for (suit in CardSuit.values()) {
                for (rank in CardRank.values()) {
                    cards += Card(rank, suit)
                }
            }
            cards.shuffle()
        }
    }

    fun draw(): Card? = cards.removeFirstOrNull()
}