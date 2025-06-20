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

  private var pot = Pot()
  val currentBet get() = pot.totalBet()
  private var startingBet = 0

  private var currentPlayerPosition = 0

  var isRoundComplete = false
    private set

  var lastWinnerAnnouncements = emptyList<String>()
    private set

  fun getCurrentPlayer(): Player {
    return players[currentPlayerPosition]
  }

  fun potSize() = pot.potSize()

  private fun setup() {
    require(players.size >= 2) { "Need at least 2 players to start a round" }
    require(dealerPosition <= players.size - 1) {
      "The dealer position must be a valid player position"
    }

    // Reset round state
    val deck = setupDeck ?: Deck()

    pot.resetTotalBet()
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
      Action.CALL -> pot.call(getCurrentPlayer())
      Action.RAISE -> pot.raise(getCurrentPlayer(), amount)
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
    if (currentBet != getCurrentPlayer().currentBet) throw CanNotCheckException(
      getCurrentPlayer()
    )
    getCurrentPlayer().setChecked()
  }

  private fun fold() {
    getCurrentPlayer().fold()
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

    pot.nextStage()
    startingBet = currentBet
    stage++
  }

  private fun payBlinds() {
    pot.raise(players[smallBlindPosition], smallBlindAmount)
    pot.raise(players[bigBlindPosition], bigBlindAmount)
  }

  /**
   * Checks if the current betting round is complete. A betting round is complete when:
   * 1. All players have acted after the last raise
   * 2. All active players have matched the current bet
   */
  private fun bettingRoundComplete(): Boolean {
    val activePlayers = players.filter { it.isActive() }
    if (currentBet == startingBet) {
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
    pot.determineWinner(communityCards)
    lastWinnerAnnouncements = pot.getWinnerAnnouncements()
  }
}

