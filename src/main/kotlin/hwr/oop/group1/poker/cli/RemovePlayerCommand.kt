package hwr.oop.group1.poker.cli

import hwr.oop.group1.poker.PlayerNotFoundException
import hwr.oop.group1.poker.Game
import hwr.oop.group1.poker.Player

class RemovePlayerCommand : GameDependentCommand() {
    override fun matches(args: List<String>): Boolean {
        return args.first() == "removePlayer"
    }

    override fun handleWithGame(game: Game, args: List<String>) {
        if (args.size == 1) throw InvalidCommandUsageException("removePlayer")
        val playerName = args[1]

        try {
            game.removePlayer(playerName)
            println("Player $playerName was removed successfully")
        } catch (e: PlayerNotFoundException) {
            println("A player with the name '$playerName' was not found in the game.")
        } catch (e: IllegalArgumentException) {
            if (e.message?.contains("There are already no players") == true) {
                println("There are already no players.")
            } else {
                throw e // Re-throw other IllegalArgumentException cases
            }
        }
    }
}