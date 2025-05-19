package hwr.oop.group1.poker

class Player(
    var name: String,
    var money: Int,
) {
    var hand = mutableListOf<Card>()
        private set
    var hasFolded = false
        private set
    var currentBet = 0
        private set

    fun addCard(card: Card) {
        hand.add(card)
    }

    fun addMoney(money: Int) {
        this.money += money
    }

    fun betMoney(money: Int): Int {
        val amount = Math.min(money, this.money)
        this.money -= amount
        currentBet = Math.max(currentBet, amount)
        return amount
    }

    fun resetCurrentBet() {
        currentBet = 0
    }

    fun fold() {
        hasFolded = true
        hand.clear()
    }

    fun resetFold() {
        hasFolded = false
    }

    /**
     * Evaluates the player's hand strength using the community cards.
     * Returns a HandRank object that can be used to compare hands.
     */
    fun evaluatePlayerHand(communityCards: List<Card>): HandRank {
        val allCards = hand + communityCards
        return evaluateHand(allCards)
    }
}
