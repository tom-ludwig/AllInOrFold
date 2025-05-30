package hwr.oop.group1.poker.cli

import hwr.oop.group1.poker.HandIsCompleteException
import hwr.oop.group1.poker.persistence.GamePersistence

class ShowPlayerCommand : CliCommand {
    override fun matches(args: List<String>): Boolean {
        return args.size > 2 && args.first() == "show" && args[1] == "player"
    }

    override fun handle(persistence: GamePersistence, args: List<String>) {
        val commands = listOf("cards", "money", "name", "bet")
        val action = args[2]
        if(!commands.contains(action)) throw InvalidCommandUsageException("show player")
        val game = persistence.loadGame() ?: throw NoGameException()
        val round = game.round ?: throw HandIsCompleteException()
        val currentPlayer = round.currentPlayer

        when (action) {
            "cards" -> {
                println("The hole cards of ${currentPlayer.name} are:")
                currentPlayer.hand.forEach {
                    println("   $it")
                }
            }
            "money" -> {
                println("The current money of ${currentPlayer.name} is ${currentPlayer.money}")
            }
            "name" -> {
                println("The current player is ${currentPlayer.name}")
            }
            "bet" -> {
                println("The current bet of ${currentPlayer.name} is ${currentPlayer.currentBet}")
            }
        }

    }
}