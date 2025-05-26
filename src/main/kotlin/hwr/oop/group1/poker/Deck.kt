package hwr.oop.group1.poker

class Deck {
    val cards = createDeck()

    fun draw(): Card = cards.removeFirst()

    fun createDeck(): MutableList<Card> {
        val deck = emptyList<Card>().toMutableList()
        for (suit in CardSuit.entries) {
            for (rank in CardRank.entries) {
                deck += Card(rank, suit)
            }
        }
        deck.shuffle()
        return deck
    }
}