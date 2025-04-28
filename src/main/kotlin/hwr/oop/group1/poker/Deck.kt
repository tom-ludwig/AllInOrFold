package hwr.oop.group1.poker

class Deck {
    private val cards = emptyList<Card>().toMutableList()

    fun resetDeck() {
        cards.clear()
        for(suit in CardSuit.entries){
            for (rank in CardRank.entries){
                cards += Card(rank, suit)
            }
        }
        shuffleDeck()
    }

    fun draw(): Card = cards.removeAt(0)

    fun shuffleDeck() {
        cards.shuffle()
    }
}