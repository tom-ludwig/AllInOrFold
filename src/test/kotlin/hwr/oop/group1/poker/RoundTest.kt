package hwr.oop.group1.poker

import io.kotest.core.spec.style.AnnotationSpec
import kotlinx.serialization.json.Json
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import java.io.File

class RoundTest : AnnotationSpec() {
  private lateinit var round: Round
  private lateinit var players: List<Player>

  @BeforeEach
  fun setUp() {
    players = listOf(
      Player("Alice", 1000),
      Player("Bob", 1000),
      Player("Caroline", 1000)
    )
    round = Round.create(players, 5, 10)
  }

  @Test
  fun `stage is 0 at start`() {
    assertThat(round.stage).isEqualTo(0)
  }

  @Test
  fun `blinds are paid on round init`() {
    assertThat(round.potSize()).isEqualTo(15)
    assertThat(players[1].getMoney()).isEqualTo(995) // small blind
    assertThat(players[2].getMoney()).isEqualTo(990) // big blind
  }

  @Test
  fun `currentPlayer is player after big blind`() {
    assertThat(round.getCurrentPlayer().name).isEqualTo("Alice")
  }

  @Test
  fun `Round can't be created with invalid dealer index`() {
    assertThatThrownBy { Round.create(players, 5, 10, dealerPosition = 5) }.hasMessageContaining("The dealer position must be a valid player position")
  }

  @Test
  fun `currentPlayer is dealer when only 2 players`() {
    players = listOf(
      Player("Alice", 1000),
      Player("Bob", 1000),
    )
    round = Round.create(players, 5, 10)

    assertThat(round.getCurrentPlayer().name).isEqualTo("Alice")
  }

  @Test
  fun `community cards should be hidden at stage 0`() {
    assertThat(round.getRevealedCommunityCards()).isEmpty()
  }

  @Test
  fun `community cards revealed correctly after each stage`() {
    assertThat(round.getRevealedCommunityCards()).hasSize(0)
    // Preflop to flop
    round.doAction(Action.CALL) // Alice
    round.doAction(Action.CALL) // Bob
    round.doAction(Action.CHECK) // Caroline
    assertThat(round.getRevealedCommunityCards()).hasSize(3)

    // Flop to turn
    round.doAction(Action.CHECK)
    round.doAction(Action.CHECK)
    round.doAction(Action.CHECK)
    assertThat(round.getRevealedCommunityCards()).hasSize(4)

    // Turn to river
    round.doAction(Action.CHECK)
    round.doAction(Action.CHECK)
    round.doAction(Action.CHECK)
    assertThat(round.getRevealedCommunityCards()).hasSize(5)
  }

  @Test
  fun `player cannot raise with insufficient funds`() {
    val player1 = Player("Alice", 15)
    val player2 = Player("Bob", 15)
    val testRound = Round.create(listOf(player1, player2), 5, 10)

    assertThatThrownBy {
      testRound.doAction(Action.RAISE, 100)
    }.isInstanceOf(NotEnoughMoneyException::class.java)
      .hasMessageContaining("wants to raise")
  }

  @Test
  fun `player cannot raise with amount not greater than current bet`() {
    round.doAction(Action.RAISE, 20) // Alice raises
    assertThatThrownBy {
      round.doAction(Action.RAISE, 5) // Bob tries invalid raise
    }.isInstanceOf(NotEnoughToRaiseException::class.java)
      .hasMessageContaining("not enough")
  }

  @Test
  fun `player cannot check if current bet is not matched`() {
    assertThatThrownBy {
      round.doAction(Action.CHECK)
    }.isInstanceOf(CanNotCheckException::class.java)
      .hasMessageContaining("can not check")
  }

  @Test
  fun `folding skips player and marks them folded`() {
    val initial = round.getCurrentPlayer().name
    round.doAction(Action.FOLD)
    assertThat(round.players[0].hasFolded).isTrue()
    assertThat(round.getCurrentPlayer().name).isEqualTo("Bob")
  }

  @Test
  fun `raise updates current bet and last raise position`() {
    round.doAction(Action.RAISE, 20)
    assertThat(round.currentBet).isEqualTo(20)
    assertThat(round.players[0].getMoney()).isEqualTo(980)
  }

  @Test
  fun `round ends when only one player remains`() {
    round.doAction(Action.FOLD) // Alice
    round.doAction(Action.FOLD) // Bob

    assertThat(round.isRoundComplete).isTrue()
    assertThat(round.lastWinnerAnnouncements.first()).contains("Caroline")
    assertThat(players.first { player -> player.name == "Caroline" }.getMoney())
      .isEqualTo(1005) // Caroline gets the pot
  }

  @Test
  fun `players get winnings at showdown`() {
    round.doAction(Action.CALL)
    round.doAction(Action.CALL)
    round.doAction(Action.CHECK)

    repeat(3) {
      round.doAction(Action.CHECK)
      round.doAction(Action.CHECK)
      round.doAction(Action.CHECK)
    }

    assertThat(round.isRoundComplete).isTrue()
    assertThat(round.potSize()).isEqualTo(0)
    assertThat(players.sumOf { it.getMoney() }).isEqualTo(3000)
  }

  @Test
  fun `player cannot trigger an action, after the round end`() {
    round.doAction(Action.CALL)
    round.doAction(Action.CALL)
    round.doAction(Action.CHECK)

    repeat(3) {
      round.doAction(Action.CHECK)
      round.doAction(Action.CHECK)
      round.doAction(Action.CHECK)
    }
    assertThatThrownBy {
      round.doAction(Action.CHECK)
    }.isInstanceOf(RoundIsCompleteException::class.java)
      .hasMessageContaining("Round is already complete")
  }

  @Test
  fun `winner gets remainder of split pot`() {
    val deck = Deck(
      mutableListOf(
        Card(CardRank.ACE, CardSuit.HEARTS),
        Card(CardRank.ACE, CardSuit.HEARTS),

        Card(CardRank.ACE, CardSuit.HEARTS),
        Card(CardRank.ACE, CardSuit.HEARTS),

        Card(CardRank.TWO, CardSuit.HEARTS),
        Card(CardRank.TWO, CardSuit.HEARTS),

        Card(CardRank.THREE, CardSuit.HEARTS),
        Card(CardRank.FIVE, CardSuit.HEARTS),
        Card(CardRank.ACE, CardSuit.HEARTS),
        Card(CardRank.ACE, CardSuit.HEARTS),
        Card(CardRank.ACE, CardSuit.HEARTS),

        Card(CardRank.ACE, CardSuit.HEARTS),
        Card(CardRank.ACE, CardSuit.HEARTS),
      )
    )
    players = listOf(
      Player("Alice", 1000),
      Player("Bob", 1000),
      Player("Caroline", 1000)
    )
    round = Round.create(players, 2, 5, setupDeck = deck)

    round.doAction(Action.CALL)
    round.doAction(Action.CALL)
    round.doAction(Action.CHECK)

    repeat(3) {
      round.doAction(Action.CHECK)
      round.doAction(Action.CHECK)
      round.doAction(Action.CHECK)
    }

    assertThat(players[0].getMoney()).isEqualTo(995 + 7)
    assertThat(players[1].getMoney()).isEqualTo(995 + 8)
    assertThat(players[2].getMoney()).isEqualTo(995)
  }

  @Test
  fun `round can be saved and loaded`() {
    val file = File("test_game.json")
    round.doAction(Action.CALL)
    round.doAction(Action.RAISE, 50)
    round.doAction(Action.CALL)
    round.doAction(Action.CALL)
    val expectedRound = round

    file.writeText(Json.encodeToString(expectedRound))

    val loadedRound = Json.decodeFromString<Round>(file.readText())

    file.delete()

    assertThat(loadedRound).isNotNull()
    assertThat(loadedRound).usingRecursiveComparison().isEqualTo(expectedRound)
  }
}