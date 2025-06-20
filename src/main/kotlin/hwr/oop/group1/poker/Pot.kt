package hwr.oop.group1.poker

import kotlinx.serialization.Serializable
import kotlin.math.min

@Serializable
class Pot(
    private val players: MutableList<Player> = mutableListOf(),
    private var playerBets: MutableList<Int> = mutableListOf(),
    private var currentBet: Int = 0,
    private var nextPot: Pot? = null
) {
    companion object {
        private var totalBet: Int = 0
    }

    private var lastWinnerAnnouncement = ""

    fun call(player: Player) {
        require(player.currentBet < totalBet) { "Player can not Call" }

        val toCall = currentBet - alreadyBetAmount(player)
        if (player.getMoney() <= toCall) {
            val betSize = player.getMoney() + alreadyBetAmount(player)
            val remainingBetSize = currentBet - betSize

            val remainingPlayers = mutableListOf<Player>()
            val remainingPlayerBets = mutableListOf<Int>()
            for (i in players.indices) {
                val bet = playerBets[i]
                if (bet > betSize) {
                    remainingPlayers.add(players[i])
                    remainingPlayerBets.add(bet - betSize)
                }
                playerBets[i] = min(bet, betSize)
            }

            addPot(remainingPlayers, remainingPlayerBets, remainingBetSize)
            currentBet = betSize
        }

        setBetAmount(player, currentBet)
        player.betMoney(toCall)
        if (nextPot != null && !player.isAllIn) nextPot!!.call(player)
    }

    fun raise(player: Player, amount: Int) {
        require(amount >= 0) { "Bet amount must be non-negative" }
        if (player.getMoney() < amount) throw NotEnoughMoneyException(
            player,
            amount
        )
        if (player.currentBet + amount <= totalBet)
            throw NotEnoughToRaiseException(player, amount)

        val toRaise = (player.currentBet + amount) - totalBet
        if (player.currentBet < totalBet) call(player)
        if (nextPot != null) {
            nextPot!!.raise(player, toRaise)
        } else {
            currentBet += toRaise
            player.betMoney(toRaise)
            setBetAmount(player, currentBet)
            totalBet += toRaise
        }
    }

    private fun playerIndex(player: Player) = players.indexOf(player)

    private fun alreadyBetAmount(player: Player) = playerBets.getOrNull(playerIndex(player)) ?: 0

    private fun setBetAmount(player: Player, amount: Int) {
        if (!players.contains(player)) {
            players.add(player)
            playerBets.add(amount)
        } else {
            playerBets[playerIndex(player)] = amount
        }
    }

    private fun addPot(players: MutableList<Player>, playerBets: MutableList<Int>, currentBet: Int) {
        nextPot = Pot(players, playerBets, currentBet, nextPot)
    }

    fun potSize() = playerBets.sum()

    fun nextPot() = nextPot

    fun totalBet() = totalBet

    fun players() = players.toList()

    fun resetTotalBet() {
        totalBet = 0
    }

    fun nextStage() {
        players().forEach { it.resetChecked() }
        if (nextPot != null) nextPot!!.nextStage()
    }

    fun determineWinner(communityCards: MutableList<Card>) {
        val activePlayers = players().filter { !it.hasFolded }
        if (activePlayers.size == 1) {
            // Only one player left, they win
            val winner = activePlayers[0]
            winner.addMoney(potSize())
            lastWinnerAnnouncement = "${winner.name} wins ${potSize()} chips!"
        } else {
            // Evaluate hands and find winners
            val playerHands =
                activePlayers.map { player ->
                    player to player.evaluatePlayerHand(
                        communityCards
                    )
                }

            // Group players by hand strength
            val groupedByHand = playerHands.groupBy { it.second }
            val bestHand = groupedByHand.keys.maxOrNull()

            if (bestHand != null) {
                val winners = groupedByHand[bestHand]!!.map { it.first }
                val splitAmount = potSize() / winners.size
                val remainder = potSize() % winners.size

                // Award split pot
                winners.forEach { winner -> winner.addMoney(splitAmount) }

                // Award remainder to first winner (or could be distributed randomly)
                if (remainder > 0) {
                    winners[0].addMoney(remainder)
                }

                // Create winner announcement
                lastWinnerAnnouncement =
                    if (winners.size == 1) {
                        "${winners[0].name} wins ${potSize()} chips with ${bestHand.type}!"
                    } else {
                        val winnerNames = winners.joinToString(", ") { it.name }
                        "$winnerNames split the pot of ${potSize()} chips (${splitAmount} each) with ${bestHand.type}!"
                    }
            }
            nextPot?.determineWinner(communityCards)
        }
        playerBets.clear()
    }

    fun getWinnerAnnouncements(): List<String> {
        val winnerAnnouncements = mutableListOf(lastWinnerAnnouncement)
        if (nextPot != null) winnerAnnouncements.add(lastWinnerAnnouncement)
        return winnerAnnouncements
    }
}