package hwr.oop.group1.poker

import kotlinx.serialization.Serializable
import kotlin.math.min

@Serializable
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
    var hasChecked = false
        private set

    fun addCard(card: Card) {
        hand.add(card)
    }

    fun addMoney(money: Int) {
        this.money += money
    }

    fun betMoney(money: Int): Int {
        val amount = min(money, this.money)
        this.money -= amount
        currentBet += amount
        return amount
    }

    fun resetCurrentBet() {
        currentBet = 0
        hasChecked = false
    }

    fun fold() {
        hasFolded = true
        hand.clear()
    }

    fun resetFold() {
        hasFolded = false
    }

    fun isActive(): Boolean {
        return !hasFolded || money == 0
    }

    fun setChecked() {
        hasChecked = true
    }

    /**
     * Evaluates the player's hand strength using the community cards.
     * Returns a HandRank object that can be used to compare hands.
     */
    fun evaluatePlayerHand(communityCards: List<Card>): HandRank {
        val allCards = hand + communityCards
        return HandEvaluator.evaluateBestHandFrom(allCards)
    }
}
