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

    private var _isActive = true
    val isActive: Boolean get() = _isActive

    fun addCard(card: Card) {
        _hand = _hand + card
    }

    fun addMoney(amount: Int) {
        money += amount
    }

    fun updateHand(newHand: List<Card>) {
        _hand = newHand
    }

    /**
     * Folds the player's hand.
     * This marks the player as inactive for the current hand.
     */
    fun fold() {
        _isActive = false
    }

    /**
     * Resets the player's state for a new hand.
     * This clears the hand and marks the player as active.
     */
    fun resetForNewHand() {
        _hand = emptyList()
        _isActive = true
    }

    /**
     * Evaluates the player's hand strength using the community cards.
     * Returns a HandRank object that can be used to compare hands.
     */
    fun evaluatePlayerHand(communityCards: List<Card>): HandRank {
        val allCards = _hand + communityCards
        return evaluateHand(allCards)
    }

    override fun toState(): Map<String, Any> {
        return mapOf(
            "name" to name,
            "money" to money,
            "hand" to hand.map { it.toState() },
            "isActive" to _isActive
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
        _isActive = state["isActive"] as Boolean
    }
}