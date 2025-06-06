package hwr.oop.group1.poker

import kotlinx.serialization.Serializable
import kotlin.math.min

// TODO: Use 2 seperate Interfaces for Command/Querys
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

  fun getMoney(): Int {
    return money
  }

  fun getHole(): List<Card> {
    return hole.toList()
  }

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
    return amount
  }

  fun resetCurrentBet() {
    currentBet = 0
    hasChecked = false
  }

  fun fold() {
    hasFolded = true
    hole.clear()
  }

  fun resetFold() {
    hasFolded = false
  }

  fun isActive(): Boolean {
    return !hasFolded && money != 0
  }

  fun setChecked() {
    hasChecked = true
  }

  /**
   * Evaluates the player's hand strength using the community cards. Returns a HandRank object
   * that can be used to compare hands.
   */
  fun evaluatePlayerHand(communityCards: List<Card>): HandRank {
    val allCards = hole + communityCards
    return HandEvaluator.evaluateBestHandFrom(allCards)
  }
}
