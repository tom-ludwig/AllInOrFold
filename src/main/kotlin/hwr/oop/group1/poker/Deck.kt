package hwr.oop.group1.poker

class Deck (
    private val cards: MutableList<Card> = mutableListOf()
){


    init {
        if(cards.isEmpty()){
            for(suit in CardSuit.entries){
                for (rank in CardRank.entries){
                    cards += Card(rank, suit)
                }
            }
            cards.shuffle()
        }
    }

    fun draw(): Card? = cards.removeFirstOrNull()
}