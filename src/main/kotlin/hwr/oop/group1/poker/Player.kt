package hwr.oop.group1.poker

import hwr.oop.group1.poker.handEvaluation.HandEvaluator
import hwr.oop.group1.poker.handEvaluation.HandRank
import kotlinx.serialization.Serializable
import kotlin.math.min

@Serializable
class Player(
    val name: String,
    private var money: Int,
) {
    private var hole = mutableListOf<Card>()

    var hasFolded = false
        private set
    var currentBet = 0
        private set
    var hasChecked = false
        private set
    var isAllIn = false
        private set

    fun getMoney(): Int = money

    fun getHole(): List<Card> = hole.toList()

    fun clearHole() {
        hole.clear()
    }

    fun addCard(card: Card) {
        hole.add(card)
    }

    fun addMoney(money: Int) {
        this.money += money
    }

    fun betMoney(money: Int): Int {
        val amount = min(money, this.money)
        this.money -= amount
        currentBet += amount
        if(this.money == 0) setAllIn()
        return amount
    }

    fun resetCurrentBet() {
        currentBet = 0
        hasChecked = false
    }

    fun resetChecked() {
        hasChecked = false
    }

    fun fold() {
        hasFolded = true
        hole.clear()
    }

    fun resetFold() {
        hasFolded = false
    }

    fun isActive(): Boolean = !(hasFolded || isAllIn)

    fun setChecked() {
        hasChecked = true
    }

    private fun setAllIn() {
        isAllIn = true
    }

    fun evaluatePlayerHand(communityCards: List<Card>): HandRank {
        val allCards = hole + communityCards
        return HandEvaluator.evaluateBestHandFrom(allCards)
    }
}