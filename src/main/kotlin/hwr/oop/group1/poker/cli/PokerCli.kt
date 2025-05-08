package hwr.oop.group1.poker.cli

import hwr.oop.group1.poker.Game
import hwr.oop.group1.poker.Player

class PokerCliException(message: String) : Exception(message)

class PokerCli {
    private val game = Game()
    private val stateManager = GameStateManager()
    private var currentPlayerIndex = 0

    // TODO: Add player action commands
    // - --fold: Fold the current hand
    // - --check: Check (if no bet to call)
    // - --all-in: Go all-in with remaining chips

    // TODO: Add game state commands
    // - --show-pot: Show current pot size
    // - --show-players: Show all players and their chip counts
    // - --show-current-bet: Show the current bet amount
    // - --show-active-players: Show players still in the current hand

    fun start(args: Array<String>) {
        if (args.isEmpty()) {
            printUsage()
            throw PokerCliException("No command provided")
        }

        val command = args[0]
        if (command !in listOf(
            "--new-game",
            "--add-player",
            "--set-small-blind",
            "--set-big-blind",
            "--set-starting-money",
            "--start-game",
            "--deal-new-hand",
            "--call",
            "--raise",
            "--show-community-cards",
            "--show-hand",
            "--show-pot",
            "--show-current-bet",
            "--show-dealer"
        )) {
            printUsage()
            throw PokerCliException("Invalid command: $command")
        }

        // Load game state for all commands except new-game
        if (command != "--new-game") {
            loadGameState()
        }

        // Handle commands
        try {
            when (command) {
                "--new-game" -> handleNewGame()
                "--add-player" -> handleAddPlayer(args)
                "--set-small-blind" -> handleSetSmallBlind(args)
                "--set-big-blind" -> handleSetBigBlind(args)
                "--set-starting-money" -> handleSetStartingMoney(args)
                "--start-game" -> handleStartGame()
                "--deal-new-hand" -> handleDealNewHand()
                "--call" -> handleCall()
                "--raise" -> handleRaise(args)
                "--show-community-cards" -> showCommunityCards()
                "--show-hand" -> showHand()
                "--show-pot" -> showPot()
                "--show-current-bet" -> showCurrentBet()
                "--show-dealer" -> showDealer()
            }
        } catch (e: IllegalArgumentException) {
            throw PokerCliException(e.message ?: "Command failed")
        }

        // Save game state after each command
        stateManager.saveState(game)
    }

    private fun handleNewGame() {
        // Always clear players and state when starting a new game
        game.clearPlayers()
        println("New game created. Add players with --add-player <name> <chips>")
    }

    private fun handleAddPlayer(args: Array<String>) {
        if (args.size < 3) {
            throw PokerCliException("Player name and chips required")
        }

        val name = args[1]
        val chips = args[2].toIntOrNull()
        if (chips == null || chips <= 0) {
            throw PokerCliException("Invalid chips amount")
        }

        game.addPlayer(Player(name, chips))
        println("Added player $name with $chips chips")
    }

    private fun handleSetSmallBlind(args: Array<String>) {
        if (args.size < 2) {
            throw PokerCliException("Amount required for small blind")
        }
        val amount = args[1].toIntOrNull()
        if (amount == null || amount <= 0) {
            throw PokerCliException("Invalid small blind amount")
        }
        game.setSmallBlind(amount)
        println("Small blind set to $amount")
    }

    private fun handleSetBigBlind(args: Array<String>) {
        if (args.size < 2) {
            throw PokerCliException("Amount required for big blind")
        }
        val amount = args[1].toIntOrNull()
        if (amount == null || amount <= 0) {
            throw PokerCliException("Invalid big blind amount")
        }
        game.setBigBlind(amount)
        println("Big blind set to $amount")
    }

    private fun handleSetStartingMoney(args: Array<String>) {
        if (args.size < 2) {
            throw PokerCliException("Amount required for starting money")
        }
        val amount = args[1].toIntOrNull()
        if (amount == null || amount <= 0) {
            throw PokerCliException("Invalid starting money amount")
        }
        game.setStartingMoney(amount)
        println("Starting money set to $amount")
    }

    private fun handleStartGame() {
        game.startNewGame()
        println("Game started!")
    }

    private fun handleDealNewHand() {
        game.startNewHand()
        println("New hand dealt!")
        showDealer()
        println("Current bet: ${game.currentBet}")
        println("Pot: ${game.round.pot}")
    }

    private fun handleCall() {
        val currentPlayer = game.players[game.currentPosition]
        if (game.currentBet == 0) {
            println("${currentPlayer.name} checks")
        } else {
            game.placeBet(game.currentBet)
            println("${currentPlayer.name} calls")
        }
        game.moveToNextPlayer()
    }

    private fun handleRaise(args: Array<String>) {
        if (args.size < 2) {
            throw PokerCliException("Amount required for raise")
        }
        val amount = args[1].toIntOrNull()
        if (amount == null || amount <= 0) {
            throw PokerCliException("Invalid raise amount")
        }
        
        val currentPlayer = game.players[game.currentPosition]
        if (amount <= game.currentBet) {
            throw PokerCliException("Raise amount must be greater than current bet")
        }
        
        game.placeBet(amount)
        println("${currentPlayer.name} raises to $amount")
        game.moveToNextPlayer()
    }

    private fun showCommunityCards() {
        val cards = game.round.getRevealedCommunityCards()
        println("Community Cards: ${if (cards.isEmpty()) "" else cards.joinToString(" ")}")
    }

    private fun showHand() {
        val currentPlayer = game.players[currentPlayerIndex]
        println("${currentPlayer.name}'s Hand: ${currentPlayer.hand.joinToString(" ")}")
    }

    private fun showPot() {
        println("Current pot: ${game.round.pot}")
    }

    private fun showCurrentBet() {
        println("Current bet: ${game.currentBet}")
    }

    private fun showDealer() {
        println("Dealer: ${game.players[game.dealer].name}")
        println("Small Blind: ${game.players[game.smallBlindPosition].name}")
        println("Big Blind: ${game.players[game.bigBlindPosition].name}")
    }

    private fun nextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % game.players.size
    }

    private fun printUsage() {
        println("""
            Usage: poker [command]
            Game Setup Commands:
              --new-game                    Create a new poker game session
              --add-player <name> <chips>   Add a player with initial chips
              --set-small-blind <amount>    Set the small blind amount
              --set-big-blind <amount>      Set the big blind amount
              --set-starting-money <amount> Set the starting money amount
              --start-game                  Start the game (requires at least 2 players)
            
            Hand Play Commands:
              --deal-new-hand              Deal a new hand (posts blinds)
              --call                       Call the current bet
              --raise <amount>             Raise the current bet by amount
              --show-community-cards       Show the community cards
              --show-hand                  Show your current hand
              --show-pot                   Show the current pot size
              --show-current-bet          Show the current bet amount
              --show-dealer               Show dealer and blind positions
        """.trimIndent())
    }

    private fun loadGameState() {
        stateManager.loadState()?.let { loadedGame ->
            try {
                // Only update players if we don't have any
                if (game.players.isEmpty()) {
                    loadedGame.players.forEach { game.addPlayer(it) }
                }
                
                // Update game state
                game.updateRound(loadedGame.round)
                game.updateDealer(loadedGame.dealer)
                
                // If the loaded game was started, start this game too
                if (loadedGame.isGameStarted) {
                    game.startNewGame()
                }
            } catch (e: IllegalArgumentException) {
                // Ignore state loading errors
            }
        }
    }
} 