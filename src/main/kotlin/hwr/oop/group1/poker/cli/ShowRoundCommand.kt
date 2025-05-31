package hwr.oop.group1.poker.cli

import hwr.oop.group1.poker.HandIsCompleteException
import hwr.oop.group1.poker.persistence.GamePersistence

class ShowRoundCommand : CliCommand {
    override fun matches(args: List<String>): Boolean {
        return args.size > 2 && args.first() == "show" && args[1] == "round"
    }

    override fun handle(persistence: GamePersistence, args: List<String>) {
        val commands = listOf("cards", "pot", "bet")
        val action = args[2]
        if(!commands.contains(action)) throw InvalidCommandUsageException("show round")
        val game = persistence.loadGame() ?: throw NoGameException()
        val round = game.round ?: throw HandIsCompleteException()

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