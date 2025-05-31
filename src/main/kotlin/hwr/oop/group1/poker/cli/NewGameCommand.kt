package hwr.oop.group1.poker.cli

import hwr.oop.group1.poker.Game
import hwr.oop.group1.poker.persistence.GamePersistence

class NewGameCommand : CliCommand{
    override fun matches(args: List<String>): Boolean {
        return args.size == 1 && args.first() == "new"
    }

    override fun handle(persistence: GamePersistence, args: List<String>) {
        val game = Game()
        persistence.saveGame(game)
        println("Game was created")
    }
}