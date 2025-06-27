package hwr.oop.group1.poker

import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy

class GameTest : AnnotationSpec() {

    @Test
    fun `Game has added Player`() {
        val game = Game()
        val player = Player("Max", 1000)

        game.addPlayer(player)
        val players = game.getPlayers()

        assertThat(players).contains(player)
    }

    @Test
    fun `Adding duplicate player throws DuplicatePlayerException`() {
        val game = Game()
        val player1 = Player("Max", 1000)
        val player2 = Player("Max", 1500) // Same name, different money

        game.addPlayer(player1)

        assertThatThrownBy { game.addPlayer(player2) }
            .isInstanceOf(DuplicatePlayerException::class.java)
            .hasMessage("Player with name 'Max' already exists in the game")
    }

    @Test
    fun `Remove player successfully removes existing player`() {
        val game = Game()
        val player = Player("Max", 1000)

        game.addPlayer(player)
        assertThat(game.getPlayers()).contains(player)

        game.removePlayer("Max")
        assertThat(game.getPlayers()).doesNotContain(player)
    }

    @Test
    fun `Remove player throws PlayerNotFoundException for non-existent player`() {
        val game = Game()
        game.addPlayer(Player("Alice", 1000))

        assertThatThrownBy { game.removePlayer("Bob") }
            .isInstanceOf(PlayerNotFoundException::class.java)
            .hasMessage("Player with name 'Bob' was not found in the game")
    }

    @Test
    fun `Remove player throws exception when no players exist`() {
        val game = Game()

        assertThatThrownBy { game.removePlayer("AnyPlayer") }
            .hasMessageContaining("There are already no players")
    }

    @Test
    fun `Remove player cannot be called during active round`() {
        val game = Game()
        game.addPlayer(Player("Alice", 1000))
        game.addPlayer(Player("Bob", 1000))
        game.newRound()

        assertThatThrownBy { game.removePlayer("Alice") }
            .isInstanceOf(RoundStartedException::class.java)
            .hasMessage("The Round has already started")
    }

    @Test
    fun `Remove player can be called after round ends`() {
        val game = Game()
        val player = Player("Alice", 1000)
        
        game.addPlayer(player)
        game.addPlayer(Player("Bob", 1000))
        game.newRound()
        game.round!!.doAction(Action.FOLD) // End the round

        game.removePlayer("Alice")
        assertThat(game.getPlayers()).doesNotContain(player)
    }

    @Test
    fun `Game starts Round`() {
        val game = Game()
        val player1 = Player("Max", 1000)
        val player2 = Player("Bob", 1000)

        game.addPlayer(player1)
        game.addPlayer(player2)
        game.newRound()

        assertThat(game.round?.players).contains(player1)
        assertThat(game.round?.players).contains(player2)
        assertThat(game.round?.smallBlindAmount).isEqualTo(game.smallBlindAmount)
        assertThat(game.round?.bigBlindAmount).isEqualTo(game.bigBlindAmount)
    }

    @Test
    fun `Round does not start if there is only one player`() {
        val game = Game()
        val player1 = Player("Max", 1000)

        game.addPlayer(player1)

        assertThatThrownBy { game.newRound() }.hasMessageContaining("Need at least 2 players to start a round")
    }

    @Test
    fun `Player can not be added if round started`() {
        val game = Game()
        val player = Player("Max", 1000)

        game.addPlayer(Player("Alice", 1000))
        game.addPlayer(Player("Bob", 1000))
        game.newRound()
        assertThatThrownBy {
            game.addPlayer(player)
        }.hasMessageContaining("The Round has already started")
    }

    @Test
    fun `Player can be added if round ended`() {
        val game = Game()
        val player = Player("Max", 1000)

        game.addPlayer(Player("Alice", 1000))
        game.addPlayer(Player("Bob", 1000))
        game.newRound()
        game.round!!.doAction(Action.FOLD)

        game.addPlayer(player)
        assertThat(game.getPlayers()).contains(player)
    }

    @Test
    fun `Set small and big blind sets it correctly`() {
        val game = Game()

        game.setSmallBlind(20)
        game.setBigBlind(40)

        assertThat(game.smallBlindAmount).isEqualTo(20)
        assertThat(game.bigBlindAmount).isEqualTo(40)

        assertThatThrownBy {
            game.setBigBlind(10)
        }.hasMessageContaining("Big blind must be greater than small blind")

        assertThatThrownBy {
            game.setBigBlind(20)
        }.hasMessageContaining("Big blind must be greater than small blind")

        assertThatThrownBy {
            game.setSmallBlind(50)
        }.hasMessageContaining("Small blind must be less than big blind")

        assertThatThrownBy {
            game.setSmallBlind(40)
        }.hasMessageContaining("Small blind must be less than big blind")

        assertThatThrownBy {
            game.setBigBlind(0)
        }.hasMessageContaining("Big blind must be greater than 0")

        assertThatThrownBy {
            game.setSmallBlind(0)
        }.hasMessageContaining("Small blind must be greater than 0")
    }

    @Test
    fun `small and big blind get set correctly in round`() {
        val game = Game()

        game.setSmallBlind(20)
        game.setBigBlind(40)

        game.addPlayer(Player("Alice", 1000))
        game.addPlayer(Player("Bob", 1000))
        game.newRound()

        assertThat(game.round!!.smallBlindAmount).isEqualTo(20)
        assertThat(game.round!!.bigBlindAmount).isEqualTo(40)
    }

    @Test
    fun `only 20 players can be added`() {
        val game = Game()

        for (i in 1..20) {
            game.addPlayer(Player("Player $i", 1000))
        }

        assertThat(game.getPlayers().size).isEqualTo(20)

        assertThatThrownBy {
            game.addPlayer(Player("Player 21", 1000))
        }.hasMessageContaining("There are already 20 players")
    }

    @Test
    fun `Players can't be added after the round started`() {
        val game = Game()
        game.addPlayer(Player("Player 1", 1000))
        game.addPlayer(Player("Player 2", 1000))
        game.newRound()

        assertThatThrownBy {
            game.addPlayer(Player("Player 21", 1000))
        }.hasMessageContaining("The Round has already started")
    }

    @Test
    fun `NewRound correctly setups values`() {
        val game = Game()

        game.addPlayer(Player("Player 1", 1000))
        game.addPlayer(Player("Player 2", 1000))

        assertThat(game.dealerPosition).isEqualTo(0)

        game.newRound()

        assertThat(game.dealerPosition).isEqualTo(1)
    }

    @Test
    fun `correct reset when starting second round`(){
        val game = Game()

        val players = mutableListOf(Player("Player 1", 1000), Player("Player 2", 1000))

        game.addPlayer(players[0])
        game.addPlayer(players[1])

        game.newRound()

        val round = game.round!!

        round.doAction(Action.FOLD)

        game.newRound()

        assertThat(round.getCurrentPlayer()).isEqualTo(players[1])
        assertThat(players).allMatch { it.getHole().size == 2 }
        assertThat(players[0].getMoney()).isEqualTo(1000 - 20 - 10)
        assertThat(players[1].getMoney()).isEqualTo(1000 + 20 - 20)
    }
}