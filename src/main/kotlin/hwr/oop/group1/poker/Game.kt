package hwr.oop.group1.poker

import hwr.oop.group1.poker.cli.StateSerializable

class Game : StateSerializable {
    private val _players = mutableListOf<Player>()
    val players: List<Player> get() = _players
    var round = Round(deck = Deck())
        private set
    var dealer = 0
        private set
    
    // Game setup properties
    private var _smallBlind = 0
    val smallBlind: Int get() = _smallBlind
    
    private var _bigBlind = 0
    val bigBlind: Int get() = _bigBlind
    
    private var _isGameStarted = false
    val isGameStarted: Boolean get() = _isGameStarted

    // Betting round state
    private var _currentBet = 0
    val currentBet: Int get() = _currentBet
    
    private var _lastRaisePosition = -1
    val lastRaisePosition: Int get() = _lastRaisePosition
    
    private var _currentPosition = 0
    val currentPosition: Int get() = _currentPosition

    private var _isBettingRoundComplete = false
    val isBettingRoundComplete: Boolean get() = _isBettingRoundComplete

    private var _isHandComplete = false
    val isHandComplete: Boolean get() = _isHandComplete

    private var _lastWinnerAnnouncement = ""
    val lastWinnerAnnouncement: String get() = _lastWinnerAnnouncement

    // Blind positions relative to dealer
    val smallBlindPosition: Int get() = (dealer + 1) % players.size
    val bigBlindPosition: Int get() = (dealer + 2) % players.size

    fun setSmallBlind(amount: Int) {
        require(amount > 0) { "Small blind must be greater than 0" }
        if (_bigBlind > 0) {
            require(amount < _bigBlind) { "Small blind must be less than big blind" }
        }
        _smallBlind = amount
    }

    fun setBigBlind(amount: Int) {
        require(amount > 0) { "Big blind must be greater than 0" }
        if (_smallBlind > 0) {
            require(amount > _smallBlind) { "Big blind must be greater than small blind" }
        }
        _bigBlind = amount
        println("Big blind set to $_bigBlind")
    }

    /**
     * Initializes a new poker game session.
     * This is called once at the start of a poker session to:
     * - Validate minimum player count
     * - Mark the game as started
     * - Enable hand dealing and betting
     */
    fun startNewGame() {
        require(!_isGameStarted) { "Game is already started" }
        require(players.size >= 2) { "Need at least 2 players to start a game" }
        _isGameStarted = true
    }

    fun addPlayer(player: Player) {
        require(!_isGameStarted) { "Cannot add players after game has started" }
        _players.add(player)
    }

    fun clearPlayers() {
        _players.clear()
        _isGameStarted = false
    }

    fun updateRound(newRound: Round) {
        round = newRound
    }

    fun updateDealer(newDealer: Int) {
        require(newDealer in players.indices) { "Invalid dealer index" }
        dealer = newDealer
    }

    /**
     * Initializes a new hand within the current game.
     * This is called at the start of each new hand to:
     * - Post the small and big blinds
     * - Set up the initial betting round
     * - Position players for the first betting action
     * 
     * The hand continues until either:
     * - All players fold except one
     * - All betting rounds are complete (pre-flop, flop, turn, river)
     */
    fun startNewHand() {
        require(_isGameStarted) { "Game must be started before starting a hand" }
        require(players.size >= 2) { "Need at least 2 players to start a hand" }
        
        // Clear previous winner announcement
        _lastWinnerAnnouncement = ""
        
        // Reset betting round state
        _currentBet = 0
        _lastRaisePosition = -1
        _currentPosition = (bigBlindPosition + 1) % players.size
        
        // Reset round state
        val deck = Deck()
        round = Round(deck = deck)
        
        // Post blinds
        val smallBlindPlayer = players[smallBlindPosition]
        val bigBlindPlayer = players[bigBlindPosition]
        
        // Handle small blind
        val smallBlindAmount = minOf(_smallBlind, smallBlindPlayer.money)
        println("Trying to reduce small blind $smallBlindAmount from ${smallBlindPlayer.name}")
        if (smallBlindAmount > 0) {
            smallBlindPlayer.addMoney(-smallBlindAmount)
            round.addToPot(smallBlindAmount)
            println("DONE")
        }
        
        // Handle big blind
        val bigBlindAmount = minOf(_bigBlind, bigBlindPlayer.money)
        if (bigBlindAmount > 0) {
            bigBlindPlayer.addMoney(-bigBlindAmount)
            round.addToPot(bigBlindAmount)
            _currentBet = bigBlindAmount
        }

        for (player in players) {
            // assign cards to players
            (0..1).forEach {
                _ -> player.addCard(deck.draw())
            }
        }
    }

    /**
     * Advances to the next player in the betting round.
     * If all players have acted after the last raise, moves to the next betting round.
     */
    fun moveToNextPlayer() {
        _currentPosition = (_currentPosition + 1) % players.size
        
        // Check if betting round is complete
        if (_currentPosition == _lastRaisePosition) {
            // All players have acted after the last raise
            _lastRaisePosition = -1
            _currentPosition = 0
            _currentBet = 0  // Reset current bet for next round
            round.nextStage()
        }
    }

    /**
     * Checks if the current betting round is complete.
     * A betting round is complete when:
     * 1. All players have acted after the last raise
     * 2. All active players have matched the current bet
     */
    private fun checkBettingRoundComplete() {
        if (_lastRaisePosition == -1) {
            // No raises in this round, check if everyone has acted
            if (_currentPosition == bigBlindPosition) {
                _isBettingRoundComplete = true
            }
        } else if (_currentPosition == _lastRaisePosition) {
            // All players have acted after the last raise
            _isBettingRoundComplete = true
        }
    }

    /**
     * Handles the completion of a betting round and progression to the next stage.
     * If all stages are complete, triggers the showdown.
     */
    private fun handleBettingRoundComplete() {
        if (!_isBettingRoundComplete) return

        _isBettingRoundComplete = false
        _currentBet = 0
        _lastRaisePosition = -1
        _currentPosition = (dealer + 1) % players.size

        if (round.stage < 3) {
            // Move to next stage
            round.nextStage()
        } else {
            // River is complete, trigger showdown
            _isHandComplete = true
            determineWinner()
        }
    }

    /**
     * Determines the winner(s) of the hand and awards the pot.
     * This is called after the river betting round is complete.
     * Handles split pots when multiple players have equal hand strength.
     */
    private fun determineWinner() {
        val activePlayers = players.filter { it.isActive }
        if (activePlayers.size == 1) {
            // Only one player left, they win
            val winner = activePlayers[0]
            winner.addMoney(round.pot)
            _lastWinnerAnnouncement = "${winner.name} wins ${round.pot} chips!"
        } else {
            // Evaluate hands and find winners
            val playerHands = activePlayers.map { player ->
                player to player.evaluatePlayerHand(round.getRevealedCommunityCards())
            }
            
            // Group players by hand strength
            val groupedByHand = playerHands.groupBy { it.second }
            val bestHand = groupedByHand.keys.maxOrNull()
            
            if (bestHand != null) {
                val winners = groupedByHand[bestHand]!!.map { it.first }
                val splitAmount = round.pot / winners.size
                val remainder = round.pot % winners.size
                
                // Award split pot
                winners.forEach { winner ->
                    winner.addMoney(splitAmount)
                }
                
                // Award remainder to first winner (or could be distributed randomly)
                if (remainder > 0) {
                    winners[0].addMoney(remainder)
                }
                
                // Create winner announcement
                _lastWinnerAnnouncement = if (winners.size == 1) {
                    "${winners[0].name} wins ${round.pot} chips with ${bestHand.type}!"
                } else {
                    val winnerNames = winners.joinToString(", ") { it.name }
                    "$winnerNames split the pot of ${round.pot} chips (${splitAmount} each) with ${bestHand.type}!"
                }
            }
        }
    }

    /**
     * Places a bet in the current betting round.
     * Handles calls (matching current bet) and raises (increasing current bet).
     * Automatically advances to the next player after the bet is placed.
     */
    fun placeBet(amount: Int) {
        require(_isGameStarted) { "Game must be started before placing bets" }
        require(!_isHandComplete) { "Hand is already complete" }
        require(amount >= 0) { "Bet amount must be non-negative" }
        
        val currentPlayer = players[_currentPosition]
        require(currentPlayer.money >= amount) { "Player does not have enough money" }
        
        if (amount > _currentBet) {
            // This is a raise
            require(amount > _currentBet) { "Raise amount must be greater than current bet" }
            _lastRaisePosition = _currentPosition
            _currentBet = amount
        }
        
        currentPlayer.addMoney(-amount)
        round.addToPot(amount)
        
        // Move to next active player
        do {
            _currentPosition = (_currentPosition + 1) % players.size
        } while (!players[_currentPosition].isActive)
        
        checkBettingRoundComplete()
        handleBettingRoundComplete()
    }

    /**
     * Folds the current player's hand.
     * Automatically advances to the next player.
     */
    fun fold() {
        require(_isGameStarted) { "Game must be started before folding" }
        require(!_isHandComplete) { "Hand is already complete" }
        
        players[_currentPosition].fold()
        
        // Check if only one player remains
        val activePlayers = players.count { it.isActive }
        if (activePlayers == 1) {
            _isHandComplete = true
            determineWinner()
            return
        }
        
        // Move to next active player
        do {
            _currentPosition = (_currentPosition + 1) % players.size
        } while (!players[_currentPosition].isActive)
        
        checkBettingRoundComplete()
        handleBettingRoundComplete()
    }

    /**
     * Moves the dealer button to the next player.
     * This is called at the end of each hand to rotate the dealer position.
     */
    fun moveDealerButton() {
        dealer = (dealer + 1) % players.size
    }

    override fun toState(): Map<String, Any> {
        return mapOf(
            "players" to players.map { it.toState() },
            "round" to round.toState(),
            "dealer" to dealer,
            "smallBlind" to _smallBlind,
            "bigBlind" to _bigBlind,
            "isGameStarted" to _isGameStarted,
            "currentBet" to _currentBet,
            "lastRaisePosition" to _lastRaisePosition,
            "currentPosition" to _currentPosition,
            "isBettingRoundComplete" to _isBettingRoundComplete,
            "isHandComplete" to _isHandComplete,
            "lastWinnerAnnouncement" to _lastWinnerAnnouncement
        )
    }

    override fun fromState(state: Map<String, Any>) {
        @Suppress("UNCHECKED_CAST")
        val playersState = state["players"] as List<Map<String, Any>>
        clearPlayers()
        playersState.forEach { playerState ->
            val player = Player("", 0) // Temporary values
            player.fromState(playerState)
            addPlayer(player)
        }

        @Suppress("UNCHECKED_CAST")
        val roundState = state["round"] as Map<String, Any>
        val newRound = Round(deck = Deck())
        newRound.fromState(roundState)
        round = newRound

        dealer = (state["dealer"] as Number).toInt()
        _smallBlind = (state["smallBlind"] as Number).toInt()
        _bigBlind = (state["bigBlind"] as Number).toInt()
        _isGameStarted = state["isGameStarted"] as Boolean
        _currentBet = (state["currentBet"] as Number).toInt()
        _lastRaisePosition = (state["lastRaisePosition"] as Number).toInt()
        _currentPosition = (state["currentPosition"] as Number).toInt()
        _isBettingRoundComplete = state["isBettingRoundComplete"] as Boolean
        _isHandComplete = state["isHandComplete"] as Boolean
        _lastWinnerAnnouncement = state["lastWinnerAnnouncement"] as String
    }
}
