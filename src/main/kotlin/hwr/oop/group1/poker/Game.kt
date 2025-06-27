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

    var smallBlindAmount = 0
        private set

    var bigBlindAmount = 0
        private set

    var dealerPosition: Int = 0
        private set

    fun setSmallBlind(amount: Int) {
        require(amount > 0) { "Small blind must be greater than 0" }
        if (bigBlindAmount > 0) {
            require(amount < bigBlindAmount) { "Small blind must be less than big blind" }
        }
        smallBlindAmount = amount
    }

    fun setBigBlind(amount: Int) {
        require(amount > 0) { "Big blind must be greater than 0" }
        require(amount > smallBlindAmount) { "Big blind must be greater than small blind" }
        bigBlindAmount = amount
    }

    fun addPlayer(player: Player) {
        if (round != null && !round!!.isRoundComplete) throw RoundStartedException()
        require(players.size < 20) { "There are already 20 players" }
        if (players.any { it.name == player.name }) {
            throw DuplicatePlayerException(player.name)
        }
        players += player
    }

    fun removePlayer(playerName: String) {
        if (round != null && !round!!.isRoundComplete) throw RoundStartedException()
        require(players.isNotEmpty()) { "There are already no players." }
        val playerExists = players.any { it.name == playerName }
        if (!playerExists) {
            throw PlayerNotFoundException(playerName)
        }
        players.removeAll { it.name == playerName }
    }

    fun newRound() {
        players.map {
            it.resetFold()
            it.resetCurrentBet()
        }
        if (smallBlindAmount == 0) smallBlindAmount = 10
        if (bigBlindAmount == 0) bigBlindAmount = 20
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

