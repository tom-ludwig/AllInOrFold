package hwr.oop.group1.poker

import kotlinx.serialization.Serializable
import kotlin.math.max
import kotlin.math.min

@Serializable
class Player(
    var name: String,
    var money: Int,
) {
    var hole = mutableListOf<Card>()
        private set
    var hasFolded = false
        private set
    var currentBet = 0
        private set

    fun addCard(card: Card) {
        hole.add(card)
    }

    fun addMoney(money: Int) {
        this.money += money
    }

    fun betMoney(money: Int): Int {
        val amount = min(money, this.money)
        this.money -= amount
        currentBet = max(currentBet, amount)
        return amount
    }

    fun resetCurrentBet() {
        currentBet = 0
    }

    fun fold() {
        hasFolded = true
        hole.clear()
    }

    fun resetFold() {
        hasFolded = false
    }

    /**
     * Evaluates the player's hand strength using the community cards.
     * Returns a HandRank object that can be used to compare hands.
     */
    fun evaluatePlayerHand(communityCards: List<Card>): HandRank {
        val allCards = hole + communityCards
        return HandEvaluator.evaluateBestHandFrom(allCards)
    }
}