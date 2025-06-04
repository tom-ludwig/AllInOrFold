package hwr.oop.group1.poker.cli

import hwr.oop.group1.poker.persistence.GameLoader
import hwr.oop.group1.poker.persistence.GameSaver

class Cli(
  private val gameLoader: GameLoader,
  private val gameSaver: GameSaver,
) {

  private val commands = listOf(
    NewGameCommand(),
    AddPlayerCommand(),
    GameStartCommand(),
    PlayerActionCommand(),
    ShowRoundCommand(),
    ShowPlayerCommand(),
  )

  fun handle(args: List<String>) {
    if (args.size < 2 || args.first() != "poker") throw InvalidCommandException(
      args.first()
    )

    val commandArgs = args.takeLast(args.size - 1)
    val command = commands.find { it.matches(commandArgs) }
      ?: throw InvalidCommandException(commandArgs.first())
    command.handle(
      gameLoader = gameLoader,
      gameSaver = gameSaver,
      commandArgs = commandArgs
    )
  }
}