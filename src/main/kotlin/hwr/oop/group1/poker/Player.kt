package hwr.oop.group1.poker

import kotlinx.serialization.Serializable
import kotlin.math.max
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

    override fun toState(): Map<String, Any> {
        return mapOf(
            "name" to name,
            "money" to money,
            "hasFolded" to hasFolded,
            "hole" to hole.map { it.toState() }
        )
    }

    override fun fromState(state: Map<String, Any>) {
        name = state["name"] as String
        money = (state["money"] as Number).toInt()
        hasFolded = state["hasFolded"] as Boolean

        @Suppress("UNCHECKED_CAST")
        val holeState = state["hole"] as? List<Map<String, String>> ?: emptyList()

        hole = holeState.map {
            val rank = CardRank.valueOf(it["rank"]!!)
            val suit = CardSuit.valueOf(it["suit"]!!)
            Card(rank, suit)
        }.toMutableList()

    }
}
