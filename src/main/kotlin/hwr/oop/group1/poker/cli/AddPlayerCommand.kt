package hwr.oop.group1.poker.cli

import hwr.oop.group1.poker.DuplicatePlayerException
import hwr.oop.group1.poker.Game
import hwr.oop.group1.poker.Player

class AddPlayerCommand : GameDependentCommand() {
    override fun matches(args: List<String>): Boolean {
        return args.first() == "addPlayer"
    }

    override fun handleWithGame(game: Game, args: List<String>) {
        if (args.size < 2) throw InvalidCommandUsageException("addPlayer")
        val playerName = args[1]
        val money = if (args.size > 2) args[2].toIntOrNull()
            ?: throw InvalidCommandUsageException("addPlayer") else null
        try {
            game.addPlayer(Player(playerName, money ?: 1000))
            println("Player $playerName was added")
        } catch (_: DuplicatePlayerException) {
            println("A player with the name '$playerName' already exists in the game.")
        }
    }
}