package hwr.oop.group1.poker

import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy

class GameTest : AnnotationSpec() {
    private lateinit var game: Game

    @BeforeEach
    fun setup() {
        game = Game()
    }

    @Test
    fun `default values are set correctly`() {
        assertThat(game.smallBlind).isEqualTo(0)
        assertThat(game.bigBlind).isEqualTo(0)
        assertThat(game.startingMoney).isEqualTo(100)
        assertThat(game.isGameStarted).isFalse
    }

    @Test
    fun `setSmallBlind validates amount`() {
        assertThatThrownBy { game.setSmallBlind(0) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Small blind must be greater than 0")

        game.setBigBlind(4)
        assertThatThrownBy { game.setSmallBlind(5) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Small blind must be less than big blind")

        game.setSmallBlind(2)
        assertThat(game.smallBlind).isEqualTo(2)
    }

    @Test
    fun `setBigBlind validates amount`() {
        game.setSmallBlind(10)
        assertThatThrownBy { game.setBigBlind(5) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Big blind must be greater than small blind")

        game.setBigBlind(15)
        assertThat(game.bigBlind).isEqualTo(15)
    }

    @Test
    fun `setStartingMoney validates amount`() {
        game.setBigBlind(10)
        assertThatThrownBy { game.setStartingMoney(5) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Starting money must be greater than big blind")

        game.setStartingMoney(200)
        assertThat(game.startingMoney).isEqualTo(200)
    }

    @Test
    fun `startNewGame requires at least 2 players`() {
        assertThatThrownBy { game.startNewGame() }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Need at least 2 players to start a game")

        game.addPlayer(Player("Player 1", 100))
        assertThatThrownBy { game.startNewGame() }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Need at least 2 players to start a game")

        game.addPlayer(Player("Player 2", 100))
        game.startNewGame()
        assertThat(game.isGameStarted).isTrue()
    }

    @Test
    fun `cannot start game twice`() {
        game.addPlayer(Player("Player 1", 100))
        game.addPlayer(Player("Player 2", 100))
        game.startNewGame()

        assertThatThrownBy { game.startNewGame() }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Game is already started")
    }

    @Test
    fun `cannot add players after game started`() {
        game.addPlayer(Player("Player 1", 100))
        game.addPlayer(Player("Player 2", 100))
        game.startNewGame()

        assertThatThrownBy { game.addPlayer(Player("Player 3", 100)) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Cannot add players after game has started")
    }

    @Test
    fun `clearing players resets game state`() {
        game.addPlayer(Player("Player 1", 100))
        game.addPlayer(Player("Player 2", 100))
        game.startNewGame()
        assertThat(game.isGameStarted).isTrue()

        game.clearPlayers()
        assertThat(game.players).isEmpty()
        assertThat(game.isGameStarted).isFalse()
    }

    @Test
    fun `updateDealer validates index`() {
        game.addPlayer(Player("Player 1", 100))
        game.addPlayer(Player("Player 2", 100))

        assertThatThrownBy { game.updateDealer(2) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Invalid dealer index")

        game.updateDealer(1)
        assertThat(game.dealer).isEqualTo(1)
    }

    @Test
    fun `state serialization preserves all values`() {
        // Setup game state
        game.setBigBlind(10)
        game.setSmallBlind(5)
        game.setStartingMoney(1000)
        game.addPlayer(Player("Player 1", 1000))
        game.addPlayer(Player("Player 2", 1000))
        game.updateDealer(1)
        game.startNewGame()

        // Serialize and deserialize
        val state = game.toState()
        val newGame = Game()
        newGame.fromState(state)

        // Verify all values
        assertThat(newGame.smallBlind).isEqualTo(5)
        assertThat(newGame.bigBlind).isEqualTo(10)
        assertThat(newGame.startingMoney).isEqualTo(1000)
        assertThat(newGame.isGameStarted).isTrue()
        assertThat(newGame.players).hasSize(2)
        assertThat(newGame.dealer).isEqualTo(1)
    }

    @Test
    fun `blind positions are calculated correctly`() {
        game.addPlayer(Player("Player 1", 1000))
        game.addPlayer(Player("Player 2", 1000))
        game.addPlayer(Player("Player 3", 1000))
        
        // Dealer at position 0
        assertThat(game.smallBlindPosition).isEqualTo(1)
        assertThat(game.bigBlindPosition).isEqualTo(2)
        
        // Move dealer button
        game.moveDealerButton()
        assertThat(game.smallBlindPosition).isEqualTo(2)
        assertThat(game.bigBlindPosition).isEqualTo(0)
        
        game.moveDealerButton()
        assertThat(game.smallBlindPosition).isEqualTo(0)
        assertThat(game.bigBlindPosition).isEqualTo(1)
    }

    @Test
    fun `startNewHand posts blinds correctly`() {
        game.addPlayer(Player("Player 1", 1000))
        game.addPlayer(Player("Player 2", 1000))
        game.addPlayer(Player("Player 3", 1000))
        game.startNewGame()
        
        game.setBigBlind(10)
        game.setSmallBlind(5)
        
        game.startNewHand()
        
        // Check small blind
        assertThat(game.players[game.smallBlindPosition].money).isEqualTo(995)
        // Check big blind
        assertThat(game.players[game.bigBlindPosition].money).isEqualTo(990)
        // Check pot
        assertThat(game.round.pot).isEqualTo(15)
        // Check current bet
        assertThat(game.currentBet).isEqualTo(10)
        // Check current position (should be after big blind)
        assertThat(game.currentPosition).isEqualTo(0)
    }

    @Test
    fun `startNewHand handles all-in blinds`() {
        game.addPlayer(Player("Player 1", 1000))
        game.addPlayer(Player("Player 2", 3))  // Small blind
        game.addPlayer(Player("Player 3", 5))  // Big blind
        game.startNewGame()
        
        game.setBigBlind(10)
        game.setSmallBlind(5)
        
        game.startNewHand()
        
        // Check small blind (all-in)
        assertThat(game.players[game.smallBlindPosition].money).isEqualTo(0)
        // Check big blind (all-in)
        assertThat(game.players[game.bigBlindPosition].money).isEqualTo(0)
        // Check pot
        assertThat(game.round.pot).isEqualTo(8)
        // Check current bet
        assertThat(game.currentBet).isEqualTo(5)
    }

    @Test
    fun `placeBet handles raises and calls correctly`() {
        game.addPlayer(Player("Player 1", 1000))
        game.addPlayer(Player("Player 2", 1000))
        game.addPlayer(Player("Player 3", 1000))
        game.startNewGame()
        
        game.setBigBlind(10)
        game.setSmallBlind(5)
        game.startNewHand()
        
        // Player 1 raises to 20
        game.placeBet(20)
        assertThat(game.currentBet).isEqualTo(20)
        assertThat(game.lastRaisePosition).isEqualTo(0)
        assertThat(game.currentPosition).isEqualTo(1)
        
        // Player 2 calls
        game.placeBet(20)
        assertThat(game.currentBet).isEqualTo(20)
        assertThat(game.currentPosition).isEqualTo(2)
        
        // Player 3 calls, round should end
        game.placeBet(20)
        assertThat(game.currentBet).isEqualTo(0)  // Reset for next round
        assertThat(game.lastRaisePosition).isEqualTo(-1)
        assertThat(game.currentPosition).isEqualTo(0)
    }

    @Test
    fun `cannot start new hand before game starts`() {
        game.addPlayer(Player("Player 1", 1000))
        game.addPlayer(Player("Player 2", 1000))
        
        assertThatThrownBy { game.startNewHand() }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Game must be started before starting a hand")
    }

    @Test
    fun `cannot place bet before game starts`() {
        game.addPlayer(Player("Player 1", 1000))
        game.addPlayer(Player("Player 2", 1000))
        
        assertThatThrownBy { game.placeBet(10) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Game must be started before placing bets")
    }

    @Test
    fun `cannot place bet with insufficient funds`() {
        game.addPlayer(Player("Player 1", 1000))
        game.addPlayer(Player("Player 2", 1000))
        game.startNewGame()
        
        assertThatThrownBy { game.placeBet(2000) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Player does not have enough money")
    }
}