package hwr.oop.group1.poker

import hwr.oop.group1.poker.cli.StateSerializable

class Game : StateSerializable {
    private val _players = mutableListOf<Player>()
    val players: List<Player> get() = _players
    var round = Round()
        private set
    var dealer = 0
        private set
    
    // Game setup properties
    private var _smallBlind = 1
    val smallBlind: Int get() = _smallBlind
    
    private var _bigBlind = 2
    val bigBlind: Int get() = _bigBlind
    
    private var _startingMoney = 100
    val startingMoney: Int get() = _startingMoney
    
    private var _isGameStarted = false
    val isGameStarted: Boolean get() = _isGameStarted

    // Betting round state
    private var _currentBet = 0
    val currentBet: Int get() = _currentBet
    
    private var _lastRaisePosition = -1
    val lastRaisePosition: Int get() = _lastRaisePosition
    
    private var _currentPosition = 0
    val currentPosition: Int get() = _currentPosition

    // Blind positions relative to dealer
    val smallBlindPosition: Int get() = (dealer + 1) % players.size
    val bigBlindPosition: Int get() = (dealer + 2) % players.size

    // TODO: Add game state tracking
    // - current betting round (pre-flop, flop, turn, river)
    // - current bet amount
    // - active players in current hand
    // - side pots for all-in situations

    // TODO: Add automatic game flow methods
    // - startNewHand(): Deal cards, post blinds, start first betting round
    // - nextBettingRound(): Move to next round when betting is complete
    // - dealCommunityCards(): Deal flop/turn/river when round is complete
    // - determineWinner(): Compare hands and award pot
    // - moveDealerButton(): Move dealer position after hand
    // - handlePlayerElimination(): Remove players with no chips

    // TODO: Add betting validation
    // - validateBet(): Ensure bets follow rules
    // - validateRaise(): Ensure raises are valid
    // - validateAllIn(): Handle all-in situations

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
    }

    fun setStartingMoney(amount: Int) {
        require(amount > _bigBlind) { "Starting money must be greater than big blind" }
        _startingMoney = amount
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
        
        // Reset betting round state
        _currentBet = 0
        _lastRaisePosition = -1
        _currentPosition = (bigBlindPosition + 1) % players.size
        
        // Reset round state
        round = Round()
        
        // Post blinds
        val smallBlindPlayer = players[smallBlindPosition]
        val bigBlindPlayer = players[bigBlindPosition]
        
        // Handle small blind
        val smallBlindAmount = minOf(_smallBlind, smallBlindPlayer.money)
        if (smallBlindAmount > 0) {
            smallBlindPlayer.addMoney(-smallBlindAmount)
            round.addToPot(smallBlindAmount)
        }
        
        // Handle big blind
        val bigBlindAmount = minOf(_bigBlind, bigBlindPlayer.money)
        if (bigBlindAmount > 0) {
            bigBlindPlayer.addMoney(-bigBlindAmount)
            round.addToPot(bigBlindAmount)
            _currentBet = bigBlindAmount
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
     * Places a bet in the current betting round.
     * Handles calls (matching current bet) and raises (increasing current bet).
     * Automatically advances to the next player after the bet is placed.
     */
    fun placeBet(amount: Int) {
        require(_isGameStarted) { "Game must be started before placing bets" }
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
        moveToNextPlayer()
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
            "startingMoney" to _startingMoney,
            "isGameStarted" to _isGameStarted,
            "currentBet" to _currentBet,
            "lastRaisePosition" to _lastRaisePosition,
            "currentPosition" to _currentPosition
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
        val newRound = Round()
        newRound.fromState(roundState)
        round = newRound

        dealer = (state["dealer"] as Number).toInt()
        _smallBlind = (state["smallBlind"] as Number).toInt()
        _bigBlind = (state["bigBlind"] as Number).toInt()
        _startingMoney = (state["startingMoney"] as Number).toInt()
        _isGameStarted = state["isGameStarted"] as Boolean
        _currentBet = (state["currentBet"] as Number).toInt()
        _lastRaisePosition = (state["lastRaisePosition"] as Number).toInt()
        _currentPosition = (state["currentPosition"] as Number).toInt()
    }
}