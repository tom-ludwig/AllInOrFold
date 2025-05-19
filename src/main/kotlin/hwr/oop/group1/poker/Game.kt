package hwr.oop.group1.poker
import hwr.oop.group1.poker.cli.StateSerializable

class Game : StateSerializable {
    var round = Round()
        private set
    var players = emptyList<Player>().toMutableList()
        private set
    private var dealerPosition = -1
    private var currentPlayerPosition = 0

    companion object {
        const val SMALL_BLIND = 5
        const val BIG_BLIND = 10
    }

    private fun payBlinds() {
        val smallBlindIndex = if (players.size == 2) { //smbIndex = SmallBlindIndex
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

    private fun bet(player: Player, amount: Int) {
        round.addToPot(player.betMoney(amount))
        round.setCurrentBet(Math.max(player.currentBet, round.currentBet))
    }

    fun addPlayer(player: Player) {
        players += player
    }

    fun newRound() {
        val deck = Deck()
        nextDealer()
        players.forEach {it.resetFold()}
        round = Round()
        payBlinds()
        for (player in players) {
            player.resetFold()
            repeat(2){
                player.addCard(deck.draw())
            }
        }
    }

    private fun nextDealer() {
        dealerPosition = (dealerPosition + 1) % players.size
    }

    fun dealer(): Player {
        return players[dealerPosition]
    }

    private fun nextPlayer() {
        currentPlayerPosition = (currentPlayerPosition + 1) % players.size
    }

    fun currentPlayer(): Player {
        return players[currentPlayerPosition]
    }

    fun doAction(player: Player, action: Action, amount: Int = 0) {
        if (player != currentPlayer() || player.hasFolded) throw PlayerOutOfTurnException(player)
        when (action) {
            Action.CHECK -> check(player)
            Action.CALL -> call(player)
            Action.RAISE -> raise(player, amount)
            Action.FOLD -> fold(player)
        }
        nextPlayer()
    }

    private fun check(player: Player) {
        if (round.currentBet != player.currentBet) throw CanNotCheckException(player)
    }

    private fun call(player: Player) {
        bet(player, round.currentBet - player.currentBet)
    }

    private fun raise(player: Player, amount: Int) {
        if (player.money < amount) throw NotEnoughMoneyException(player, amount)
        if(player.currentBet + amount <= round.currentBet) throw NotEnoughToRaiseException(player, amount)
        bet(player, amount)
    }

    private fun fold(player: Player) {
        player.fold()
    }

    override fun toState(): Map<String, Any> {
        return mapOf(
            "round" to round.toState(),
            "players" to players.map { it.toState() },
            "dealerPosition" to dealerPosition,
            "currentPlayerPosition" to currentPlayerPosition
        )
    }

    override fun fromState(state: Map<String, Any>) {
        dealerPosition = state["dealerPosition"] as Int
        round = Round().apply { fromState(state["round"] as Map<String, Any>) }
        players = (state["players"] as List<Map<String, Any>>).map {
            Player().apply { fromState(it) }
        }.toMutableList()
        currentPlayerPosition = state["currentPlayerPosition"] as Int
    }
}

class PlayerOutOfTurnException (
    player: Player
): RuntimeException(
    "player $player can not play if not on turn"
)

class NotEnoughMoneyException (
    player: Player,
    amount: Int
): RuntimeException(
    "player $player wants to raise $amount but only has ${player.money}"
)

class CanNotCheckException (
    player: Player
): RuntimeException(
    "player $player can not check"
)

class NotEnoughToRaiseException (
    player: Player,
    amount: Int
): RuntimeException(
    "player $player can not raise, because $amount is not enough"
)