package hwr.oop.group1.poker
import hwr.oop.group1.poker.cli.StateSerializable



class Player(
    var name: String = "",
    var money: Int = 0
) : StateSerializable {
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
        var amount = Math.min(money, this.money)
        this.money -= amount
        currentBet += amount
        return amount
    }

    fun fold() {
        hasFolded = true
        hand.clear()
    }

    fun resetFold() {
        hasFolded = false
    }

    override fun toState(): Map<String, Any> {
        return mapOf(
            "name" to name,
            "money" to money,
            "hasFolded" to hasFolded,
            "hand" to hand.map { it.toState() }
        )
    }

    override fun fromState(state: Map<String, Any>) {
        name = state["name"] as String
        money = (state["money"] as Number).toInt()
        hasFolded = state["hasFolded"] as Boolean

        @Suppress("UNCHECKED_CAST")
        val handState = state["hand"] as? List<Map<String, String>> ?: emptyList()

        hand = handState.map {
            val rank = CardRank.valueOf(it["rank"]!!)
            val suit = CardSuit.valueOf(it["suit"]!!)
            Card(rank, suit)
        }.toMutableList()
    }
}
