package hwr.oop.group1.poker.cli

import hwr.oop.group1.poker.persistence.GamePersistence

class GameStartCommand : CliCommand {
    override fun matches(args: List<String>): Boolean {
        return args.size == 1 && args.first() == "start"
    }

    override fun handle(persistence: GamePersistence, args: List<String>) {
        val game = persistence.loadGame() ?: throw NoGameException()
        game.newRound()
        persistence.saveGame(game)
        println("Game has started")
    }
}