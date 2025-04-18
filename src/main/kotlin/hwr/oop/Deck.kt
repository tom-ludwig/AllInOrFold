package hwr.oop

class Deck {
    var cards = emptyList<Card>().toMutableList()
        private set

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