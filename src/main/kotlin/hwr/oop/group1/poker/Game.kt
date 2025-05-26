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

    private fun payBlinds() {
        val smallBlindIndex = if (players.size == 2) {
            dealerPosition
        } else {
            (dealerPosition + 1) % players.size
        }
        val bigBlindIndex = if (players.size == 2) {
            (dealerPosition + 1) % players.size
        } else {
            (dealerPosition + 2) % players.size
        }
        bet(players[smallBlindIndex], SMALL_BLIND)
        bet(players[bigBlindIndex], BIG_BLIND)
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