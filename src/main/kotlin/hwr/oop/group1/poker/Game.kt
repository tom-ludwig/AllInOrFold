package hwr.oop.group1.poker

class Game {
    private var round = Round()
    var players = emptyList<Player>().toMutableList()
        private set
    private var dealerPosition = 0

    fun addPlayer(player: Player) {
        players += player
    }

    private fun nextDealer() {
        dealerPosition = (dealerPosition + 1) % players.size
    }

    fun newRound() {
        nextDealer()
        round = Round()
    }

    fun dealer(): Player {
        return players[dealerPosition]
    }
}