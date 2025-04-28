package hwr.oop.group1.poker

class Deck {
    private val cards = mutableListOf<Card>()

    init {
        cards.clear()
        for(suit in CardSuit.entries){
            for (rank in CardRank.entries){
                cards += Card(rank, suit)
            }
        }
        cards.shuffle()
    }

    fun draw(): Card = cards.removeAt(0)
}