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
    fun `Dealer changes after new Round`() {
        val game = Game()
        val player1 = Player("Max")
        val player2 = Player("Ben")
        game.addPlayer(player1)
        game.addPlayer(player2)

        assertThat(game.dealer()).isEqualTo(player1)
        game.newRound()
        assertThat(game.dealer()).isEqualTo(player2)
    }
}