package hwr.oop.group1.poker

class Game {
    var round = Round()
        private set
    var players = emptyList<Player>().toMutableList()
        private set
    var dealer = 0
        private set
    companion object {
        const val SMALL_BLIND = 5
        const val BIG_BLIND = 10
        }
    private fun payBlinds(){
      val smbIndex = if (players.size == 2) { //smbIndex = SmallBlindIndex
          dealer
      }else{
          (dealer+1) % players.size
      }
      val bbIndex = if (players.size == 2) {
          (dealer + 1) % players.size
      } else {
          (dealer + 2) % players.size
      }
      players[smbIndex].addMoney(-SMALL_BLIND)
      players[bbIndex].addMoney(-BIG_BLIND)
    }
    fun addPlayer(player: Player) {
        players += player
    }
    fun nextDealer() {
        dealer = (dealer + 1) % players.size
    }

    fun newRound() {
        nextDealer()
        players.forEach {it.resetFold()}
        round = Round()
        payBlinds()
    }
}