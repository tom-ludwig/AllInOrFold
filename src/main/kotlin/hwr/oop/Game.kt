package hwr.oop

class Game {
    var round = Round()
        private set
    var players = emptyList<Player>().toMutableList()
        private set
    var dealer = 0
        private set

    fun addPlayer(player: Player) {
        players += player
    }

    fun nextDealer() {
        dealer = (dealer + 1) % players.size
    }

    fun newRound() {
        nextDealer()
        round = Round()
    }
}