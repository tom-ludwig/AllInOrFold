package hwr.oop.group1.poker
import hwr.oop.group1.poker.cli.StateSerializable

class Round(
    val players: List<Player>,
    val smallBlindAmount: Int = 5,
    val bigBlindAmount: Int = 10,
    val dealerPosition: Int = 0,
) : StateSerializable {
    var communityCards = mutableListOf<Card>()
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

    // Blind positions relative to dealer
    val smallBlindPosition: Int get() = (dealerPosition + 1) % players.size
    val bigBlindPosition: Int get() = (dealerPosition + 2) % players.size

    var pot = 0
        private set
    var currentBet = 0
        private set

    var currentPlayerPosition = 0
        private set
    val currentPlayer get() = players[currentPlayerPosition]

    var lastRaisePosition = -1
        private set

    private var isBettingRoundComplete = false

    var isHandComplete = false
        private set

    var lastWinnerAnnouncement = ""
       private set

    init {
        require(players.size >= 2) { "Need at least 2 players to start a hand" }
        require(dealerPosition <= players.size - 1) { "The dealer position must be a valid player position" }
        require(bigBlindAmount > smallBlindAmount) { "The small blind has to be smaller than the big blind" }
        // Reset round state
        val deck = Deck()

        payBlinds()

        for (player in players) {
            // assign cards to players
            (0..<2).forEach {
                _ -> player.addCard(deck.draw())
            }
        }

        (0..<5).forEach {
            _ -> communityCards += deck.draw()
        }
    }

    fun doAction(action: Action, amount: Int = 0) {
        if (isHandComplete) throw HandIsCompleteException()

        when (action) {
            Action.CHECK -> check()
            Action.CALL -> call()
            Action.RAISE -> raise(amount)
            Action.FOLD -> fold()
        }

        // Move to next active player
        nextPlayer()

        // Set isHandComplete to make sure no other actions can be triggered
        isHandComplete = checkIfHandIsComplete()

        if (isBettingRoundComplete) {
            handleBettingRoundComplete()
        } else if (isHandComplete) {
            determineWinner()
        } else {
            checkBettingStageComplete()
        }
    }

    private fun nextPlayer() {
        do {
            currentPlayerPosition = (currentPlayerPosition + 1) % players.size
        } while (players[currentPlayerPosition].hasFolded)
    }

    private fun check() {
        if (currentBet != currentPlayer.currentBet) throw CanNotCheckException(currentPlayer)
    }

    private fun call() {
        placeBet(currentPlayer, currentBet - currentPlayer.currentBet)
    }

    private fun raise(amount: Int) {
        if (currentPlayer.money < amount) throw NotEnoughMoneyException(currentPlayer, amount)
        if(currentPlayer.currentBet + amount <= currentBet) throw NotEnoughToRaiseException(currentPlayer, amount)
        placeBet(currentPlayer, amount)
    }

    private fun fold() {
        currentPlayer.fold()
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
     * Advances to the next stage of the hand.
     * This is called when a betting round is complete.
     * Stages progress: Pre-flop -> Flop -> Turn -> River
     */
    private fun nextStage() {
        require(stage < 3) { "Cannot advance past the river" }

        lastRaisePosition = -1
        currentBet = 0  // Reset current bet for next round
        players.map { it.resetCurrentBet() }
        stage++
    }

    private fun addToPot(money: Int) {
        require(money >= 0) { "Cannot add negative amount to pot" }
        pot += money
    }

    /**
     * Places a bet in the current betting round.
     * Handles calls (matching current bet) and raises (increasing current bet).
     * Automatically advances to the next player after the bet is placed.
     */
    private fun placeBet(player: Player, amount: Int) {
//        require(isGameStarted) { "Game must be started before placing bets" }
        require(!isHandComplete) { "Hand is already complete" }
        require(amount >= 0) { "Bet amount must be non-negative" }

        require(player.money >= amount) { "Player does not have enough money" }

        // Check if the player raised
        if (amount > currentBet) {
            lastRaisePosition = currentPlayerPosition
            currentBet = amount
        }

        player.betMoney(amount)
        addToPot(amount)
    }

    private fun payBlinds() {
        placeBet(players[smallBlindPosition], smallBlindAmount)
        placeBet(players[bigBlindPosition], bigBlindAmount)
        isBettingRoundComplete = false
        lastRaisePosition = bigBlindPosition
    }

    /**
     * Advances to the next player in the betting round.
     * If all players have acted after the last raise, moves to the next betting round.
     */
//    private fun moveToNextPlayer() {
//        currentPlayerPosition = (currentPlayerPosition + 1) % players.size
//
//        // Check if betting round is complete
//        if (currentPlayerPosition == lastRaisePosition) {
//            // All players have acted after the last raise

//        }
//    }

    /**
     * Checks if the current betting round is complete.
     * A betting round is complete when:
     * 1. All players have acted after the last raise
     * 2. All active players have matched the current bet
     */
    private fun checkBettingStageComplete() {
        if (lastRaisePosition == -1) {
            // No raises in this round, check if everyone has acted
            if (currentPlayerPosition == bigBlindPosition) {
                isBettingRoundComplete = true
            }
        } else if (currentPlayerPosition == lastRaisePosition) {
            // All players have acted after the last raise
            isBettingRoundComplete = true
        }
    }

    /**
     * Handles the completion of a betting round and progression to the next stage.
     * If all stages are complete, triggers the showdown.
     */
    private fun handleBettingRoundComplete() {
        if (!isBettingRoundComplete) return

        isBettingRoundComplete = false
        currentBet = 0
        lastRaisePosition = -1
        currentPlayerPosition = (dealerPosition + 1) % players.size

        if (stage < 3) {
            // Move to next stage
            nextStage()
        } else {
            // River is complete, trigger showdown
            isHandComplete = true
            determineWinner()
        }
    }

    private fun checkIfHandIsComplete(): Boolean {
        // Check if only one player is active
        if (players.filter { !it.hasFolded }.size == 1) return true

        // Check if all players are all in
        if (players.filter { !it.hasFolded }.all { it.money == 0 }) return true

        return false
    }

    /**
     * Determines the winner(s) of the hand and awards the pot.
     * This is called after the river betting round is complete.
     * Handles split pots when multiple players have equal hand strength.
     */
    private fun determineWinner() {
        val activePlayers = players.filter { !it.hasFolded }
        if (activePlayers.size == 1) {
            // Only one player left, they win
            val winner = activePlayers[0]
            winner.addMoney(pot)
            lastWinnerAnnouncement = "${winner.name} wins $pot chips!"
            pot = 0
        } else {
            // Evaluate hands and find winners
            val playerHands = activePlayers.map { player ->
                player to player.evaluatePlayerHand(getRevealedCommunityCards())
            }

            // Group players by hand strength
            val groupedByHand = playerHands.groupBy { it.second }
            val bestHand = groupedByHand.keys.maxOrNull()

            if (bestHand != null) {
                val winners = groupedByHand[bestHand]!!.map { it.first }
                val splitAmount = pot / winners.size
                val remainder = pot % winners.size

                // Award split pot
                winners.forEach { winner ->
                    winner.addMoney(splitAmount)
                }

                // Award remainder to first winner (or could be distributed randomly)
                if (remainder > 0) {
                    winners[0].addMoney(remainder)
                }

                // Create winner announcement
                lastWinnerAnnouncement = if (winners.size == 1) {
                    "${winners[0].name} wins $pot chips with ${bestHand.type}!"
                } else {
                    val winnerNames = winners.joinToString(", ") { it.name }
                    "$winnerNames split the pot of $pot chips (${splitAmount} each) with ${bestHand.type}!"
                }
                pot = 0
            }
        }
    }

    override fun toState(): Map<String, Any> {
        return mapOf(
            "communityCards" to communityCards.map { it.toState() },
            "stage" to stage,
            "pot" to pot
        )
    }
    override fun fromState(state: Map<String, Any>) {
        communityCards = (state["communityCards"] as List<Map<String, Any>>).map {
            Card(CardRank.valueOf(it["rank"] as String), CardSuit.valueOf(it["suit"] as String))
        }.toMutableList()
        stage = (state["stage"] as Number).toInt()
        pot = (state["pot"] as Number).toInt()
    }
}

class NotEnoughMoneyException (
    player: Player,
    amount: Int
): RuntimeException(
    "player $player wants to raise $amount but only has ${player.money}"
)

class CanNotCheckException (
    player: Player
): RuntimeException(
    "player $player can not check"
)

class NotEnoughToRaiseException (
    player: Player,
    amount: Int
): RuntimeException(
    "player $player can not raise, because $amount is not enough"
)

class HandIsCompleteException: RuntimeException(
    "The Hand is already complete, to play again start a new round."
)
