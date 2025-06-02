package hwr.oop.group1.poker.cli

import hwr.oop.group1.poker.Game
import hwr.oop.group1.poker.persistence.GameLoader
import hwr.oop.group1.poker.persistence.GameSaver

interface CliCommand {
  fun matches(args: List<String>): Boolean
  fun handle(
    gameLoader: GameLoader,
    gameSaver: GameSaver,
    commandArgs: List<String>,
  )
}

abstract class GameDependentCommand : CliCommand {
  abstract fun handleWithGame(game: Game, args: List<String>)

  final override fun handle(
    gameLoader: GameLoader,
    gameSaver: GameSaver,
    commandArgs: List<String>,
  ) {
    val game = gameLoader.loadGame()
    handleWithGame(game, commandArgs)
    gameSaver.saveGame(game)
  }
}

