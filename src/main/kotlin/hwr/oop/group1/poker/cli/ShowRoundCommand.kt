package hwr.oop.group1.poker.cli

import hwr.oop.group1.poker.Game
import hwr.oop.group1.poker.RoundIsCompleteException

class ShowRoundCommand : GameDependentCommand() {
    override fun matches(args: List<String>): Boolean {
        return args.size > 2 && args.first() == "show" && args[1] == "round"
    }

    override fun handleWithGame(game: Game, args: List<String>) {
        val commands = listOf("cards", "pot", "bet")
        val action = args[2]
        if (!commands.contains(action)) throw InvalidCommandUsageException("show round")
        val round = game.round ?: throw RoundIsCompleteException()

        when (action) {
            "cards" -> {
                println("The shown community cards are:")
                round.getRevealedCommunityCards().forEach {
                    println("   $it")
                }
            }

            "pot" -> {
                println("The current pot contains ${round.pot}")
            }

            "bet" -> {
                println("The current bet is ${round.currentBet}")
            }
        }
    }
}