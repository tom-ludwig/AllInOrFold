package hwr.oop.group1.poker

import hwr.oop.group1.poker.cli.StateSerializable

class Player(
    name: String,
    money: Int
) : StateSerializable {
    var name = name
        private set
    var money = money
        private set
    private var _hand = emptyList<Card>()
    val hand: List<Card> get() = _hand

    fun addCard(card: Card) {
        _hand = _hand + card
    }

    fun addMoney(amount: Int) {
        money += amount
    }

    fun updateHand(newHand: List<Card>) {
        _hand = newHand
    }

    override fun toState(): Map<String, Any> {
        return mapOf(
            "name" to name,
            "money" to money,
            "hand" to hand.map { it.toState() }
        )
    }

    override fun fromState(state: Map<String, Any>) {
        name = state["name"] as String
        money = (state["money"] as Number).toInt()
        
        @Suppress("UNCHECKED_CAST")
        val handState = state["hand"] as List<Map<String, String>>
        _hand = handState.map { cardState ->
            Card(
                CardRank.valueOf(cardState["rank"]!!),
                CardSuit.valueOf(cardState["suit"]!!)
            )
        }
    }
}