package hwr.oop.group1.poker

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
class Round
private constructor(
  val players: List<Player>,
  val smallBlindAmount: Int = 5,
  val bigBlindAmount: Int = 10,
  private val dealerPosition: Int = 0,
  @Transient private val setupDeck: Deck? = null,
) {
  private var communityCards = mutableListOf<Card>()

  companion object {
    fun create(
      players: List<Player>,
      smallBlindAmount: Int = 5,
      bigBlindAmount: Int = 10,
      dealerPosition: Int = 0,
      setupDeck: Deck? = null,
    ): Round {
      val round = Round(
        players,
        smallBlindAmount,
        bigBlindAmount,
        dealerPosition,
        setupDeck
      )
      round.setup()
      return round
    }

//    fun createFromPersistence(
//      players: List<Player>,
//      smallBlindAmount: Int = 5,
//      bigBlindAmount: Int = 10,
//      dealerPosition: Int = 0,
//      communityCards: List<Card>,
//      pot: Int = 0,
//      currentBet: Int = 0,
//      currentPlayerPosition: Int = 0,
//      isRoundComplete: Boolean = false,
//      lastWinnerAnnouncement: String = ""
//    ): Round {
//      val round = Round(players, smallBlindAmount, bigBlindAmount, dealerPosition)
//      round.communityCards = communityCards.toMutableList()
//      round.pot = pot
//      round.currentBet = currentBet
//      round.currentPlayerPosition = currentPlayerPosition
//      round.isRoundComplete = isRoundComplete
//      round.lastWinnerAnnouncement = lastWinnerAnnouncement
//      // Skip setup since we're loading existing state
//      return round
//    }
  }

  /**
   * The current stage of the round: 0 = Pre-flop (no community cards) 1 = Flop (3 community
   * cards) 2 = Turn (4 community cards) 3 = River (5 community cards)
   */
  var stage = 0
    private set

  // Blind positions relative to dealer
  private var smallBlindPosition: Int =
    getNextActivePlayerPosition(dealerPosition)
  private var bigBlindPosition: Int =
    getNextActivePlayerPosition(smallBlindPosition)

  var pot = 0
    private set
  var currentBet = 0
    private set

  private var currentPlayerPosition = 0

  // Convert to Method;
  val currentPlayer
    get() = players[currentPlayerPosition]

  var isRoundComplete = false
    private set

  var lastWinnerAnnouncement = ""
    private set

  private fun setup() {
    require(players.size >= 2) { "Need at least 2 players to start a round" }
    require(dealerPosition <= players.size - 1) {
      "The dealer position must be a valid player position"
    }
    require(bigBlindAmount > smallBlindAmount) {
      "The small blind has to be smaller than the big blind"
    }
    // Reset round state
    val deck = setupDeck ?: Deck()

    payBlinds()

    // Set the currentPlayer to the player after the big blind
    currentPlayerPosition =
      if (players.size > 2) getNextActivePlayerPosition(bigBlindPosition)
      else dealerPosition

    // assign cards to players
    for (player in players) {
      player.clearHole()
      repeat(2) {
        val card = deck.draw()!!
        player.addCard(card)
      }
    }

    repeat(5) {
      val card = deck.draw()!!
      communityCards += card
    }
  }

  fun doAction(action: Action, amount: Int = 0) {
    if (isRoundComplete) throw RoundIsCompleteException()

    when (action) {
      Action.CHECK -> check()
      Action.CALL -> call()
      Action.RAISE -> raise(amount)
      Action.FOLD -> fold()
    }

    // Move to next active player
    nextPlayer()

    if (checkRoundComplete()) {
      isRoundComplete = true
      determineWinner()
    } else if (bettingRoundComplete()) {
      handleBettingRoundComplete()
    }
  }

  private fun nextPlayer() {
    currentPlayerPosition = getNextActivePlayerPosition(currentPlayerPosition)
  }

  private fun check() {
    if (currentBet != currentPlayer.currentBet) throw CanNotCheckException(
      currentPlayer
    )
    currentPlayer.setChecked()
  }

  private fun call() {
    placeBet(currentPlayer, currentBet - currentPlayer.currentBet)
  }

  private fun raise(amount: Int) {
    if (currentPlayer.getMoney() < amount) throw NotEnoughMoneyException(
      currentPlayer,
      amount
    )
    if (currentPlayer.currentBet + amount <= currentBet)
      throw NotEnoughToRaiseException(currentPlayer, amount)
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
      1 -> communityCards.take(3)
      2 -> communityCards.take(4)
      3 -> communityCards
      else -> emptyList()
    }
  }

  /** Advances to the next stage of the round. This is called when a betting round is complete. */
  private fun nextStage() {
    require(stage < 3) { "Cannot advance past the river" }

    currentBet = 0 // Reset current bet for next round
    players.map { it.resetCurrentBet() }
    stage++
  }

  private fun addToPot(money: Int) {
    require(money >= 0) { "Cannot add negative amount to pot" }
    pot += money
  }

  /**
   * Places a bet in the current betting round. Handles calls (matching current bet) and raises
   * (increasing current bet).
   */
  private fun placeBet(player: Player, amount: Int) {
    require(!isRoundComplete) { "Round is already complete" }
    require(amount >= 0) { "Bet amount must be non-negative" }

    require(player.getMoney() >= amount) { "Player does not have enough money" }

    // Check if the player raised
    if (amount + player.currentBet > currentBet) {
      currentBet = amount + player.currentBet
    }

    player.betMoney(amount)
    addToPot(amount)
  }

  private fun payBlinds() {
    placeBet(players[smallBlindPosition], smallBlindAmount)
    placeBet(players[bigBlindPosition], bigBlindAmount)
  }

  /**
   * Checks if the current betting round is complete. A betting round is complete when:
   * 1. All players have acted after the last raise
   * 2. All active players have matched the current bet
   */
  private fun bettingRoundComplete(): Boolean {
    val activePlayers = players.filter { it.isActive() }
    if (currentBet == 0) {
      // No raises in this round, check if everyone has checked
      return activePlayers.all { it.hasChecked }
    }
    return activePlayers.all { it.currentBet == currentBet }
        // In the first round the big blind can play again
        &&
        (stage != 0 ||
            !players[bigBlindPosition].isActive() ||
            players[bigBlindPosition].hasChecked ||
            currentBet > bigBlindAmount)
  }

  private fun getNextActivePlayerPosition(index: Int): Int {
    var currentIndex = index
    do {
      currentIndex = (currentIndex + 1) % players.size
    } while (!players[currentIndex].isActive())
    return currentIndex
  }

  /**
   * Handles the completion of a betting round and progression to the next stage. If all stages
   * are complete, triggers the showdown.
   */
  private fun handleBettingRoundComplete() {
    currentBet = 0

    currentPlayerPosition = getNextActivePlayerPosition(dealerPosition)

    if (stage < 3) {
      // Move to next stage
      nextStage()
    } else {
      // River is complete, trigger showdown
      isRoundComplete = true
      determineWinner()
    }
  }

  private fun checkRoundComplete(): Boolean {
    // Check if only one player is active
    if (players.filter { !it.hasFolded }.size == 1) return true

    // Check if all players are all in
    if (players.filter { !it.hasFolded }.all { it.getMoney() == 0 }) return true

    return false
  }

  /**
   * Determines the winner(s) of the round and awards the pot. This is called after the river
   * betting round is complete. Handles split pots when multiple players have equal hand strength.
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
      val playerHands =
        activePlayers.map { player ->
          player to player.evaluatePlayerHand(
            getRevealedCommunityCards()
          )
        }

      // Group players by hand strength
      val groupedByHand = playerHands.groupBy { it.second }
      val bestHand = groupedByHand.keys.maxOrNull()

      if (bestHand != null) {
        val winners = groupedByHand[bestHand]!!.map { it.first }
        val splitAmount = pot / winners.size
        val remainder = pot % winners.size

        // Award split pot
        winners.forEach { winner -> winner.addMoney(splitAmount) }

        // Award remainder to first winner (or could be distributed randomly)
        if (remainder > 0) {
          winners[0].addMoney(remainder)
        }

        // Create winner announcement
        lastWinnerAnnouncement =
          if (winners.size == 1) {
            "${winners[0].name} wins $pot chips with ${bestHand.type}!"
          } else {
            val winnerNames = winners.joinToString(", ") { it.name }
            "$winnerNames split the pot of $pot chips (${splitAmount} each) with ${bestHand.type}!"
          }
        pot = 0
      }
    }
  }
}

