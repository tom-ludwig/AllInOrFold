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
        round = Round(players, 5, 10)
        round.setup()
    }

    @Test
    fun `stage is 0 at start`() {
        assertThat(round.stage).isEqualTo(0)
    }

    @Test
    fun `blinds are paid on round init`() {
        assertThat(round.pot).isEqualTo(15)
        assertThat(players[1].money).isEqualTo(995) // small blind
        assertThat(players[2].money).isEqualTo(990) // big blind
    }

    @Test
    fun `currentPlayer is player after big blind`() {
        assertThat(round.currentPlayer.name).isEqualTo("Alice")
    }

    @Test
    fun `currentPlayer is dealer when only 2 players`() {
        players = listOf(
            Player("Alice", 1000),
            Player("Bob", 1000),
        )
        round = Round(players, 5, 10)
        round.setup()

        assertThat(round.currentPlayer.name).isEqualTo("Alice")
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
        players[0].money = 1
        assertThatThrownBy {
            round.doAction(Action.RAISE, 100)
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
        val initial = round.currentPlayer.name
        round.doAction(Action.FOLD)
        assertThat(round.players[0].hasFolded).isTrue()
        assertThat(round.currentPlayer.name).isEqualTo("Bob")
    }

    @Test
    fun `raise updates current bet and last raise position`() {
        round.doAction(Action.RAISE, 20)
        assertThat(round.currentBet).isEqualTo(20)
        assertThat(round.players[0].money).isEqualTo(980)
    }

    @Test
    fun `round ends when only one player remains`() {
        round.doAction(Action.FOLD) // Alice
        round.doAction(Action.FOLD) // Bob

        assertThat(round.isHandComplete).isTrue()
        assertThat(round.lastWinnerAnnouncement).contains("Caroline")
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

        assertThat(round.isHandComplete).isTrue()
        assertThat(round.pot).isEqualTo(0)
        assertThat(players.sumOf { it.money }).isEqualTo(3000)
    }

    @Test
    fun `player cannot trigger an action, after the hand end`() {
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
        }.isInstanceOf(HandIsCompleteException::class.java)
            .hasMessageContaining("Hand is already complete")
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
        round = Round(players, 2, 5, setupDeck = deck)
        round.setup()

        round.doAction(Action.CALL)
        round.doAction(Action.CALL)
        round.doAction(Action.CHECK)

        repeat(3) {
            round.doAction(Action.CHECK)
            round.doAction(Action.CHECK)
            round.doAction(Action.CHECK)
        }

        assertThat(players[0].money).isEqualTo(995 + 8)
        assertThat(players[1].money).isEqualTo(995 + 7)
        assertThat(players[2].money).isEqualTo(995)
    }

    @Test
    fun `round can be saved and loaded`() {
        val json = Json
        val file = File("test_game.json")
        val expectedRound = round

        file.writeText(json.encodeToString(expectedRound))

        val loadedRound = json.decodeFromString<Round>(file.readText())

        file.delete()

        assertThat(loadedRound).isNotNull()
        assertThat(loadedRound).usingRecursiveComparison().isEqualTo(expectedRound)
    }
}