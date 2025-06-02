package hwr.oop.group1.poker.cli

import hwr.oop.group1.poker.Game
import hwr.oop.group1.poker.persistence.GameLoader
import hwr.oop.group1.poker.persistence.GameSaver

class NewGameCommand : CliCommand {
  override fun matches(args: List<String>): Boolean {
    return args.size == 1 && args.first() == "new"
  }

  override fun handle(
    gameLoader: GameLoader,
    gameSaver: GameSaver,
    commandArgs: List<String>,
  ) {
    val game = Game()
    gameSaver.saveGame(game)
    println("Game was created")
  }
}