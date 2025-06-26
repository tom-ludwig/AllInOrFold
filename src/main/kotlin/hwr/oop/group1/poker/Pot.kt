package hwr.oop.group1.poker

import hwr.oop.group1.poker.handEvaluation.HandRank
import kotlinx.serialization.Serializable
import kotlin.compareTo
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
            for (playerIndex in players.indices) {
                val bet = playerBets[playerIndex]
                if (bet > betSize) {
                    remainingPlayers.add(players[playerIndex])
                    remainingPlayerBets.add(bet - betSize)
                }
                playerBets[playerIndex] = min(bet, betSize)
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
            rewardWinners(listOf(winner), potSize())
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
                rewardWinners(winners, potSize(), bestHand)
            }
        }
        nextPot?.determineWinner(communityCards)
        playerBets.clear()
    }

    fun rewardWinners(players: List<Player>, totalAmount: Int, bestHand: HandRank? = null){
        val splitAmount = potSize() / players.size
        val remainder = potSize() % players.size

        // Award split pot
        players.forEach { winner -> winner.addMoney(splitAmount) }

        // Award remainder to first winner
        if (remainder > 0) {
            players[0].addMoney(remainder)
        }
        lastWinnerAnnouncement =
            if (players.size == 1) {
                "${players[0].name} wins ${potSize()} chips${if (bestHand != null) " with " + bestHand.type else ""}!"
            } else {
                val winnerNames = players.joinToString(", ") { it.name }

                "$winnerNames split the pot of ${potSize()} chips (${splitAmount} each) with ${bestHand!!.type}!"
            }
    }

    fun getWinnerAnnouncements(): List<String> {
        val winnerAnnouncements = mutableListOf(lastWinnerAnnouncement)
        if (nextPot != null) winnerAnnouncements.addAll(nextPot!!.getWinnerAnnouncements())
        return winnerAnnouncements
    }
}