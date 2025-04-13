package hwr.oop

class Deck {
    private var deck = ArrayList<Card>()

    fun resetDeck() {
        for(suit in CardSuit.entries){
            for (rank in CardRank.entries){
                deck += Card(rank, suit)
            }
        }
        shuffleDeck()
    }

    fun deck(): List<Card> {
        return deck
    }

    fun drawCard(): Card {
        return deck.removeAt(0)
    }

    fun shuffleDeck() {
        deck.shuffle()
    }
}