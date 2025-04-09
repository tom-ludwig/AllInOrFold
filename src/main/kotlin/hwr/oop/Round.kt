package hwr.oop

class Round {
    private var cards = ArrayList<Card>()
    private var round: Int = 0
    private var pot: Int = 0

    fun addCard(card: Card) {
        this.cards += card
    }

    fun cards(): List<Card> {
        return cards
    }

    fun round(): Int {
        return round
    }

    fun nextRound() {
        round++
    }

    fun addToPot(money: Int) {
        pot += money
    }

    fun pot(): Int{
        return pot
    }
}