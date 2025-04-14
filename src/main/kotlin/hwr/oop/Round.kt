package hwr.oop

class Round {
    private var deck: Deck = Deck()
    private var communityCards = ArrayList<Card>()
    private var round: Int = 0
    private var pot: Int = 0

    fun addCommunityCard(card: Card) {
        this.communityCards += card
    }

    fun communityCards(): List<Card> {
        return communityCards
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