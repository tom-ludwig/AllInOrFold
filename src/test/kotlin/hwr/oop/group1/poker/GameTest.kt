package hwr.oop.group1.poker

import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy

class GameTest: AnnotationSpec() {

    @Test
    fun `Game has added Player`() {
        val game = Game()
        val player = Player("Max")

        game.addPlayer(player)
        val players = game.players

        assertThat(players).contains(player)
    }

    @Test
    fun `Dealer is first player after new Round`() {
        val game = Game()
        val player1 = Player("Max")
        val player2 = Player("Ben")
        game.addPlayer(player1)
        game.addPlayer(player2)

        game.newRound()
        val dealer = game.dealer()

        assertThat(dealer).isEqualTo(player1)
    }

    @Test
    fun `small and big blinds are paid (for more than 2 players)`() {
        val game = Game()
        val a = Player("Harry", money = 100)
        val b = Player("Ron", money = 100)
        val c = Player("Hermione", money = 100)
        game.addPlayer(a)
        game.addPlayer(b)
        game.addPlayer(c)


        game.newRound()

        assertThat(b.money).isEqualTo(100 - Game.SMALL_BLIND)
        assertThat(c.money).isEqualTo(100 - Game.BIG_BLIND)
        assertThat(game.round.pot).isEqualTo(Game.SMALL_BLIND + Game.BIG_BLIND)

        assertThat(a.money).isEqualTo(100)
    }

    @Test
    fun `dealer pays small blind and other pays big blind in heads up (2 players)`() {

        val game = Game()
        val first  = Player("Harry",  money = 200)
        val second = Player("Ron", money = 200)
        game.addPlayer(first)
        game.addPlayer(second)

        game.newRound()

        assertThat(first.money).isEqualTo(200 - Game.SMALL_BLIND)
        assertThat(second.money).isEqualTo(200 - Game.BIG_BLIND)
        assertThat(game.round.pot).isEqualTo(Game.SMALL_BLIND + Game.BIG_BLIND)
    }

    @Test
    fun `players are dealt two cards at the beginning of a new round`() {
        val game = Game()
        val alice  = Player("Alice",  money = 100)
        val bob = Player("Bob", money = 100)
        game.addPlayer(alice)
        game.addPlayer(bob)

        game.newRound()

        assertThat(alice.hand.size).isEqualTo(2)
    }

    @Test
    fun `players can not do an action if it is not their turn`(){
        val game = Game()
        val alice  = Player("Alice",  money = 100)
        val bob = Player("Bob", money = 100)
        val max = Player("Max", money = 100)
        game.addPlayer(alice)
        game.addPlayer(bob)
        game.addPlayer(max)

        game.newRound()
        assertThatThrownBy {
            game.doAction(bob, Action.CHECK)
        }.hasMessageContainingAll(
            bob.toString(),
            "can not play if not on turn"
        )
        assertThatThrownBy {
            game.doAction(bob, Action.CALL)
        }.hasMessageContainingAll(
            bob.toString(),
            "can not play if not on turn"
        )
        assertThatThrownBy {
            game.doAction(bob, Action.RAISE)
        }.hasMessageContainingAll(
            bob.toString(),
            "can not play if not on turn"
        )
        assertThatThrownBy {
            game.doAction(bob, Action.FOLD)
        }.hasMessageContainingAll(
            bob.toString(),
            "can not play if not on turn"
        )
    }

    @Test
    fun `players can call`(){
        val game = Game()
        val alice  = Player("Alice",  money = 100)
        val bob = Player("Bob", money = 100)
        val max = Player("Max", money = 100)
        game.addPlayer(alice)
        game.addPlayer(bob)
        game.addPlayer(max)

        game.newRound()
        val potBefore = game.round.pot
        game.doAction(alice, Action.CALL)

        assertThat(alice.money).isEqualTo(100 - Game.BIG_BLIND)
        assertThat(game.round.pot).isEqualTo(potBefore + Game.BIG_BLIND)
        assertThat(game.currentPlayer()).isEqualTo(bob)
    }

    @Test
    fun `players can check`(){
        val game = Game()
        val alice  = Player("Alice",  money = 100)
        val bob = Player("Bob", money = 100)
        val max = Player("Max", money = 100)
        game.addPlayer(alice)
        game.addPlayer(bob)
        game.addPlayer(max)

        game.newRound()
        game.doAction(alice, Action.CALL)
        game.doAction(bob, Action.CALL)

        val potBefore = game.round.pot
        val moneyBefore = max.money
        game.doAction(max, Action.CHECK)

        assertThat(max.money).isEqualTo(moneyBefore)
        assertThat(game.round.pot).isEqualTo(potBefore)
        assertThat(game.currentPlayer()).isEqualTo(alice)
    }

    @Test
    fun `players can not check in first round`(){
        val game = Game()
        val alice  = Player("Alice",  money = 100)
        val bob = Player("Bob", money = 100)
        val max = Player("Max", money = 100)
        game.addPlayer(alice)
        game.addPlayer(bob)
        game.addPlayer(max)

        game.newRound()
        assertThatThrownBy {
            game.doAction(alice, Action.CHECK)
        }.hasMessageContainingAll(
            alice.toString(),
            "can not check"
        )
    }

    @Test
    fun `players can raise`(){
        val game = Game()
        val alice  = Player("Alice",  money = 100)
        val bob = Player("Bob", money = 100)
        val max = Player("Max", money = 100)
        game.addPlayer(alice)
        game.addPlayer(bob)
        game.addPlayer(max)

        game.newRound()
        game.doAction(alice, Action.CALL)
        game.doAction(bob, Action.CALL)

        val currentBetBefore = game.round.currentBet
        val potBefore = game.round.pot
        val moneyBefore = max.money
        game.doAction(max, Action.RAISE, 30)

        assertThat(max.money).isEqualTo(moneyBefore - 30)
        assertThat(game.round.pot).isEqualTo(potBefore + 30)
        assertThat(game.round.currentBet).isEqualTo(currentBetBefore + 30)
        assertThat(game.currentPlayer()).isEqualTo(alice)
    }

    @Test
    fun `players can not raise more than they have`(){
        val game = Game()
        val alice  = Player("Alice",  money = 100)
        val bob = Player("Bob", money = 100)
        val max = Player("Max", money = 100)
        game.addPlayer(alice)
        game.addPlayer(bob)
        game.addPlayer(max)

        game.newRound()
        game.doAction(alice, Action.CALL)
        game.doAction(bob, Action.CALL)

        assertThatThrownBy {
            game.doAction(max, Action.RAISE, 100)
        }.hasMessageContainingAll(
            max.toString(),
            "wants to raise 100",
            "only has " + max.money
        )
    }

    @Test
    fun `players can not raise to less than the current bet`(){
        val game = Game()
        val alice  = Player("Alice",  money = 100)
        val bob = Player("Bob", money = 100)
        val max = Player("Max", money = 100)
        game.addPlayer(alice)
        game.addPlayer(bob)
        game.addPlayer(max)

        game.newRound()

        assertThatThrownBy {
            game.doAction(alice, Action.RAISE, 5)
        }.hasMessageContainingAll(
            alice.toString(),
            "can not raise",
            "5 is not enough"
        )
    }

    @Test
    fun `players can fold`(){
        val game = Game()
        val alice  = Player("Alice",  money = 100)
        val bob = Player("Bob", money = 100)
        val max = Player("Max", money = 100)
        game.addPlayer(alice)
        game.addPlayer(bob)
        game.addPlayer(max)

        game.newRound()
        game.doAction(alice, Action.FOLD)

        assertThat(alice.hasFolded).isTrue()
        assertThat(game.currentPlayer()).isEqualTo(bob)
    }
}
