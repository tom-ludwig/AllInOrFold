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
  fun `Game starts Round`() {
    val game = Game()
    val player1 = Player("Max", 1000)
    val player2 = Player("Max", 1000)

    game.addPlayer(player1)
    game.addPlayer(player2)
    game.newRound()

    assertThat(game.round?.players).contains(player1)
    assertThat(game.round?.players).contains(player2)
    assertThat(game.round?.smallBlindAmount).isEqualTo(game.smallBlindAmount)
    assertThat(game.round?.bigBlindAmount).isEqualTo(game.bigBlindAmount)
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
  fun `Set small and big blind sets it correctly`() {
    val game = Game()

    game.setBigBlind(40)
    game.setSmallBlind(20)

    assertThat(game.smallBlindAmount).isEqualTo(20)
    assertThat(game.bigBlindAmount).isEqualTo(40)

    assertThatThrownBy {
      game.setBigBlind(10)
    }.hasMessageContaining("Big blind must be greater than small blind")

    assertThatThrownBy {
      game.setSmallBlind(50)
    }.hasMessageContaining("Small blind must be less than big blind")

    assertThatThrownBy {
      game.setBigBlind(0)
    }.hasMessageContaining("Big blind must be greater than 0")

    assertThatThrownBy {
      game.setSmallBlind(0)
    }.hasMessageContaining("Small blind must be greater than 0")
  }
}