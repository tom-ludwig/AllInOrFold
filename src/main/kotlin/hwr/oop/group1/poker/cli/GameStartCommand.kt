package hwr.oop.group1.poker.cli

import hwr.oop.group1.poker.Game

class GameStartCommand : GameDependentCommand() {
    override fun matches(args: List<String>): Boolean {
        return args.size == 1 && args.first() == "start"
    }

    override fun handleWithGame(game: Game, args: List<String>) {
        game.newRound()
        println("Game has started")
    }
}