package hwr.oop.group1.poker
import hwr.oop.group1.poker.cli.StateSerializable
import hwr.oop.group1.poker.toState
import hwr.oop.group1.poker.fromState

class Game : StateSerializable {
        var round = Round()
            private set
        var players = emptyList<Player>().toMutableList()
            private set
        private var dealerPosition = -1

        companion object {
            const val SMALL_BLIND = 5
            const val BIG_BLIND = 10
        }

        private fun payBlinds() {
            val smbIndex = if (players.size == 2) { //smbIndex = SmallBlindIndex
                dealerPosition
            } else {
                (dealerPosition + 1) % players.size
            }
            val bbIndex = if (players.size == 2) {
                (dealerPosition + 1) % players.size
            } else {
                (dealerPosition + 2) % players.size
            }
            bet(smbIndex, SMALL_BLIND)
            bet(bbIndex, BIG_BLIND)
        }

        private fun bet(playerIndex: Int, amount: Int) {
            players[playerIndex].addMoney(-amount)
            round.addToPot(amount)
        }

        fun addPlayer(player: Player) {
            players += player
        }

        fun newRound() {
            nextDealer()
            players.forEach { it.resetFold() }
            round = Round()
            payBlinds()
        }

        private fun nextDealer() {
            dealerPosition = (dealerPosition + 1) % players.size
        }

        fun dealer(): Player {
            return players[dealerPosition]
        }

        override fun toState(): Map<String, Any> {
            return mapOf(
                "round" to round.toState(),
                "players" to players.map { it.toState() },
                "dealerPosition" to dealerPosition
            )
        }

        override fun fromState(state: Map<String, Any>) {
            dealerPosition = state["dealerPosition"] as Int
            round = Round().apply { fromState(state["round"] as Map<String, Any>) }
            players = (state["players"] as List<Map<String, Any>>).map {
                Player().apply { fromState(it) }
            }.toMutableList()
        }
    }