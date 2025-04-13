package hwr.oop

class Game {
    private var round: Round = Round()
    private var players = ArrayList<Player>()
    private var dealer: Int = 0

    fun addPlayer(player: Player) {
        players += player
    }

    fun players(): List<Player> {
        return players
    }

    fun nextDealer() {
        dealer = (dealer + 1) % players.size
    }

    fun dealer(): Int {
        return dealer
    }

    fun newRound() {
        nextDealer()
        round = Round()
    }
}