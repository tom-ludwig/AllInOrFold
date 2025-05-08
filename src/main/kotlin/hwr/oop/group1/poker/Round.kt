package hwr.oop.group1.poker

import hwr.oop.group1.poker.cli.StateSerializable

class Round : StateSerializable {
    var deck = Deck()
        private set
    var communityCards = emptyList<Card>().toMutableList()
        private set
    
    /**
     * The current stage of the hand:
     * 0 = Pre-flop (no community cards)
     * 1 = Flop (3 community cards)
     * 2 = Turn (4 community cards)
     * 3 = River (5 community cards)
     */
    var stage = 0
        private set
    
    var pot = 0
        private set

    /**
     * Adds a card to the community cards.
     * This is used when dealing the flop, turn, and river.
     */
    fun addCommunityCard(card: Card) {
        communityCards += card
    }

    /**
     * Advances to the next stage of the hand.
     * This is called when a betting round is complete.
     * Stages progress: Pre-flop -> Flop -> Turn -> River
     */
    fun nextStage() {
        require(stage < 3) { "Cannot advance past the river" }
        stage++
    }

    /**
     * Returns the community cards that should be visible at the current stage.
     * - Pre-flop: No cards
     * - Flop: First 3 cards
     * - Turn: First 4 cards
     * - River: All 5 cards
     */
    fun getRevealedCommunityCards(): List<Card> {
        return when (stage) {
            0 -> emptyList()
            1 -> communityCards.take(3)
            2 -> communityCards.take(4)
            3 -> communityCards
            else -> emptyList()
        }
    }

    /**
     * Adds money to the pot.
     * This is used when players place bets.
     */
    fun addToPot(money: Int) {
        require(money >= 0) { "Cannot add negative amount to pot" }
        pot += money
    }

    /**
     * Sets the pot to a specific amount.
     * This is used when loading game state.
     */
    fun setPot(newPot: Int) {
        require(newPot >= 0) { "Pot cannot be negative" }
        pot = newPot
    }

    /**
     * Sets the current stage.
     * This is used when loading game state.
     */
    fun setStage(newStage: Int) {
        require(newStage in 0..3) { "Invalid stage: $newStage" }
        stage = newStage
    }

    /**
     * Clears all community cards.
     * This is used when starting a new hand.
     */
    fun clearCommunityCards() {
        communityCards.clear()
    }

    override fun toState(): Map<String, Any> {
        return mapOf(
            "communityCards" to communityCards.map { it.toState() },
            "pot" to pot,
            "stage" to stage
        )
    }

    override fun fromState(state: Map<String, Any>) {
        @Suppress("UNCHECKED_CAST")
        val cardsState = state["communityCards"] as List<Map<String, String>>
        clearCommunityCards()
        cardsState.forEach { cardState ->
            addCommunityCard(
                Card(
                    CardRank.valueOf(cardState["rank"]!!),
                    CardSuit.valueOf(cardState["suit"]!!)
                )
            )
        }
        
        pot = (state["pot"] as Number).toInt()
        stage = (state["stage"] as Number).toInt()
    }
}