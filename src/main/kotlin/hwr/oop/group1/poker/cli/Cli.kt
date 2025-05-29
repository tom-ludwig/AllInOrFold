package hwr.oop.group1.poker.cli

import hwr.oop.group1.poker.*
import hwr.oop.group1.poker.persistence.GamePersistence

class Cli (private val gamePersistence: GamePersistence) {
    fun handle(args: List<String>) {
        if(args.first() != "game") throw InvalidCommandException(args.first())
        if(args.size == 1) throw InvalidCommandUsageException(args.first())
        if(args[1] == "new"){
            gamePersistence.saveGame(createGame())
        }else{
            val game = loadGame()
            when (args[1]) {
                "start" -> {
                    game.newRound()
                    println("Game has started")
                }
                "addPlayer" -> {
                    if (args.size < 3) throw InvalidCommandUsageException(args[1])
                    addPlayer(game, args[2])
                }
                "check" -> {
                    doPlayerAction(game,Action.CHECK)
                }
                "call" -> {
                    doPlayerAction(game,Action.CALL)
                }
                "raise" -> {
                    if(args.size < 3) throw InvalidCommandUsageException(args[1])
                    doPlayerAction(game,Action.RAISE, args[2].toInt())
                }
                "fold" -> {
                    doPlayerAction(game,Action.FOLD)
                }
                "show" -> {
                    if(args.size < 3) throw InvalidCommandUsageException(args[1])
                    showAction(game, args[2])
                }
                else -> {
                    throw InvalidCommandException(args[1])
                }
            }
            gamePersistence.saveGame(game)
        }
    }

    private fun loadGame(): Game {
        return gamePersistence.loadGame() ?: throw NoGameException()
    }

    private fun createGame(): Game {
        val game = Game()
        println("Game was created")
        return game
    }

    private fun addPlayer(game: Game, player: String) {
        game.addPlayer(Player(player,1000))
        println("Player $player was added")
    }

    private fun doPlayerAction(game: Game, action: Action, amount: Int = 0) {
        val round = game.round ?: throw HandIsCompleteException()
        val currentPlayer = round.currentPlayer
        val stageBefore = round.stage
        round.doAction(action, amount)
        println("Player ${currentPlayer.name} has performed action $action")
        if(round.isHandComplete){
            println(round.lastWinnerAnnouncement)
        }
        else {
            if(round.stage != stageBefore) {
                println("Stage is over")
                showAction(game, "communityCards")
            }
            println("Next player is ${round.currentPlayer.name}")
        }
    }

    private fun showAction(game: Game, arg: String) {
        val round = game.round ?: throw HandIsCompleteException()
        val currentPlayer = round.currentPlayer

        when (arg){
            "hole" -> {
                println("The hole cards of ${currentPlayer.name} are:")
                currentPlayer.hand.forEach {
                    println("   ${it.rank.name} of ${it.suit.name}")
                }
            }
            "communityCards" -> {
                println("The shown community cards are:")
                round.getRevealedCommunityCards().forEach {
                    println("   ${it.rank.name} of ${it.suit.name}")
                }
            }
            "money" -> {
                println("The current money of ${currentPlayer.name}: ${currentPlayer.money}")
            }
            "pot" -> {
                println("Current pot: ${round.pot}")
            }
            "currentPlayer" -> {
                println("The current player is ${currentPlayer.name}")
            }
            "bet" -> {
                println("The current bet is ${round.currentBet}")
                println("The current bet of ${currentPlayer.name} is ${currentPlayer.currentBet}")
            }
        }
    }
}

