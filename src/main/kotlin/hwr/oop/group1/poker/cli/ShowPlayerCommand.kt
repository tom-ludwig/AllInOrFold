package hwr.oop.group1.poker.cli

import hwr.oop.group1.poker.Game
import hwr.oop.group1.poker.RoundIsCompleteException

class ShowPlayerCommand : GameDependentCommand() {
  override fun matches(args: List<String>): Boolean {
    return args.size > 2 && args.first() == "show" && args[1] == "player"
  }

  override fun handleWithGame(game: Game, args: List<String>) {
    val commands = listOf("cards", "money", "name", "bet")
    val action = args[2]
    if (!commands.contains(action)) throw InvalidCommandUsageException("show player")
    val round = game.round ?: throw RoundIsCompleteException()
    val currentPlayer = round.currentPlayer

    when (action) {
      "cards" -> {
        println("The hole cards of ${currentPlayer.name} are:")
        currentPlayer.getHole().forEach {
          println("   $it")
        }
      }

      "money" -> {
        println("The current money of ${currentPlayer.name} is ${currentPlayer.getMoney()}")
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