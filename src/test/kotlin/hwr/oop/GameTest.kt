package hwr.oop

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
    fun `Dealer is 1 after new Round`() {
        val game = Game()
        game.addPlayer(Player("Max"))
        game.addPlayer(Player("Ben"))

        game.newRound()
        val dealer = game.dealer

        assertThat(dealer).isEqualTo(1)
    }
}