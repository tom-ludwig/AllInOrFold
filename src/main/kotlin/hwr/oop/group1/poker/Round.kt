package hwr.oop.group1.poker

class Round {
    var deck = Deck()
        private set
    var communityCards = emptyList<Card>().toMutableList()
        private set
    var round = 0
        private set
    var pot = 0
        private set

    fun addCommunityCard(card: Card) {
        this.communityCards += card
    }

    fun nextRound() {
        round++
    }

    fun addToPot(money: Int) {
        pot += money
    }
}