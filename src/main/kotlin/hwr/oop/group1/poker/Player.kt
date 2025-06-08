package hwr.oop.group1.poker

import hwr.oop.group1.poker.handEvaluation.HandEvaluator
import hwr.oop.group1.poker.handEvaluation.HandRank
import kotlinx.serialization.Serializable
import kotlin.math.min

interface PlayerQuery {
  val name: String
  fun getMoney(): Int
  fun getHole(): List<Card>
  fun isActive(): Boolean
  fun evaluatePlayerHand(communityCards: List<Card>): HandRank
  val hasFolded: Boolean
  val currentBet: Int
  val hasChecked: Boolean
}

interface PlayerCommand {
  fun clearHole()
  fun addCard(card: Card)
  fun addMoney(money: Int)
  fun betMoney(money: Int): Int
  fun resetCurrentBet()
  fun fold()
  fun resetFold()
  fun setChecked()
}

@Serializable
class Player(
  override val name: String,
  private var money: Int,
) : PlayerQuery, PlayerCommand {

  private var hole = mutableListOf<Card>()

  override var hasFolded = false
    private set
  override var currentBet = 0
    private set
  override var hasChecked = false
    private set

  override fun getMoney(): Int = money

  override fun getHole(): List<Card> = hole.toList()

  override fun clearHole() {
    hole.clear()
  }

  override fun addCard(card: Card) {
    hole.add(card)
  }

  override fun addMoney(money: Int) {
    this.money += money
  }

  override fun betMoney(money: Int): Int {
    val amount = min(money, this.money)
    this.money -= amount
    currentBet += amount
    return amount
  }

  override fun resetCurrentBet() {
    currentBet = 0
    hasChecked = false
  }

  override fun fold() {
    hasFolded = true
    hole.clear()
  }

  override fun resetFold() {
    hasFolded = false
  }

  override fun isActive(): Boolean = !hasFolded && money != 0

  override fun setChecked() {
    hasChecked = true
  }

  override fun evaluatePlayerHand(communityCards: List<Card>): HandRank {
    val allCards = hole + communityCards
    return HandEvaluator.evaluateBestHandFrom(allCards)
  }
}



//@Serializable
//class Player(
//  val name: String,
//  private var money: Int,
//) {
//  private var hole = mutableListOf<Card>()
//
//  var hasFolded = false
//    private set
//  var currentBet = 0
//    private set
//  var hasChecked = false
//    private set
//
//  fun getMoney(): Int {
//    return money
//  }
//
//  fun getHole(): List<Card> {
//    return hole.toList()
//  }
//
//  fun clearHole() {
//    hole.clear()
//  }
//
//  fun addCard(card: Card) {
//    hole.add(card)
//  }
//
//  fun addMoney(money: Int) {
//    this.money += money
//  }
//
//  fun betMoney(money: Int): Int {
//    val amount = min(money, this.money)
//    this.money -= amount
//    currentBet += amount
//    return amount
//  }
//
//  fun resetCurrentBet() {
//    currentBet = 0
//    hasChecked = false
//  }
//
//  fun fold() {
//    hasFolded = true
//    hole.clear()
//  }
//
//  fun resetFold() {
//    hasFolded = false
//  }
//
//  fun isActive(): Boolean {
//    return !hasFolded && money != 0
//  }
//
//  fun setChecked() {
//    hasChecked = true
//  }
//
//  /**
//   * Evaluates the player's hand strength using the community cards. Returns a HandRank object
//   * that can be used to compare hands.
//   */
//  fun evaluatePlayerHand(communityCards: List<Card>): HandRank {
//    val allCards = hole + communityCards
//    return HandEvaluator.evaluateBestHandFrom(allCards)
//  }
//}
