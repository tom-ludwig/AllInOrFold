package hwr.oop.group1.poker

import kotlinx.serialization.Serializable

@Serializable
class Game {
  var round: Round? = null
    private set

  // Make Private; use separate get method
  private val players = mutableListOf<Player>()

  fun getPlayers(): List<Player> {
    return players.toList()
  }

  var smallBlindAmount = 10
    private set

  var bigBlindAmount = 20
    private set

  private var dealerPosition: Int = 0

  fun setSmallBlind(amount: Int) {
    require(amount > 0) { "Small blind must be greater than 0" }
    if (bigBlindAmount > 0) {
      require(amount < bigBlindAmount) { "Small blind must be less than big blind" }
    }
    smallBlindAmount = amount
  }

  fun setBigBlind(amount: Int) {
    require(amount > 0) { "Big blind must be greater than 0" }
    if (smallBlindAmount > 0) {
      require(amount > smallBlindAmount) { "Big blind must be greater than small blind" }
    }
    bigBlindAmount = amount
    println("Big blind set to $bigBlindAmount")
  }

  fun addPlayer(player: Player) {
    if (round != null && !round!!.isRoundComplete) throw RoundStartedException()
    require(players.size < 20) { "There are already 20 players" }
    players += player
  }

  fun newRound() {
    players.map {
      it.resetFold()
      it.resetCurrentBet()
    }
    round =
      Round.create(
        players,
        smallBlindAmount,
        bigBlindAmount,
        dealerPosition,
      )
    dealerPosition = (dealerPosition + 1) % players.size
  }
}

