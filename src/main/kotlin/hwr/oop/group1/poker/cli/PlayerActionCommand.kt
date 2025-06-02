package hwr.oop.group1.poker.cli

import hwr.oop.group1.poker.Action
import hwr.oop.group1.poker.Game
import hwr.oop.group1.poker.RoundIsCompleteException

class PlayerActionCommand : GameDependentCommand() {
  override fun matches(args: List<String>): Boolean {
    val actions = listOf("check", "call", "raise", "fold")
    return actions.contains(args.first())
  }

  override fun handleWithGame(game: Game, args: List<String>) {
    val round = game.round ?: throw RoundIsCompleteException()
    val currentPlayer = round.currentPlayer

    val stageBefore = round.stage

    val action = args.first()
    when (action) {
      "check" -> {
        round.doAction(Action.CHECK)
      }

      "call" -> {
        round.doAction(Action.CALL)
      }

      "raise" -> {
        val amount =
          args[1].toIntOrNull() ?: throw InvalidCommandUsageException("raise")
        round.doAction(Action.RAISE, amount)
      }

      "fold" -> {
        round.doAction(Action.FOLD)
      }
    }

    println("Player ${currentPlayer.name} has performed action $action")

    if (round.isRoundComplete) {
      println(round.lastWinnerAnnouncement)
    } else {
      if (round.stage != stageBefore) {
        println("Stage is over")
        ShowRoundCommand().handleWithGame(
          game,
          listOf("show", "round", "cards")
        )
      }
      println("Next player is ${round.currentPlayer.name}")
    }
  }
}