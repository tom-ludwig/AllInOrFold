package hwr.oop

class Card (private val cardRank: CardRank, private val cardSuit: CardSuit){
    fun rank(): CardRank {
        return cardRank
    }

    fun suit(): CardSuit {
        return cardSuit
    }
}