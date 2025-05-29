package hwr.oop.group1.poker.cli

import hwr.oop.group1.poker.Player
import hwr.oop.group1.poker.persistence.GamePersistence

class AddPlayerCommand : CliCommand {
    override fun matches(args: List<String>): Boolean {
        return args.first() == "addPlayer"
    }

    override fun handle(persistence: GamePersistence, args: List<String>) {
        if(args.size < 2) throw InvalidCommandUsageException("addPlayer")
        val game = persistence.loadGame() ?: throw NoGameException()
        val playername = args[1]
        val money = if(args.size > 2) args[2].toIntOrNull() ?: throw InvalidCommandUsageException("addPlayer") else null
        game.addPlayer(Player(playername, money ?: 1000))
        persistence.saveGame(game)
        println("Player $playername was added")
    }
}