package hwr.oop.group1.poker

import kotlinx.serialization.Serializable

@Serializable
class Game {
    var round: Round? = null
        private set

    var players = emptyList<Player>().toMutableList()
        private set

    var smallBlindAmount = 10
        private set

    var bigBlindAmount = 20
        private set

    // TODO: Increment after a round ended
    private var dealerPosition: Int = -1

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
        // TODO: Exception if the current round isn't over
        players += player
    }

    fun newRound() {
        dealerPosition = (dealerPosition + 1) % players.size
        players.map {
            it.resetFold()
            it.resetCurrentBet()
        }
        round = Round(
            players,
            smallBlindAmount,
            bigBlindAmount,
            dealerPosition,
        )
    }
}