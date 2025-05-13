package hwr.oop.group1.poker

import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat

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
}
