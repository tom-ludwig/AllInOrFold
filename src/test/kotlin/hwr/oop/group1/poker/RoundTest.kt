package hwr.oop.group1.poker

import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy

class RoundTest : AnnotationSpec() {
    lateinit var round: Round
    lateinit var players: List<Player>

    @BeforeEach
    fun setUp() {
        players = listOf(
            Player("Alice", 1000),
            Player("Bob", 1000),
            Player("Caroline", 1000)
        )
        round = Round(players, 5, 10)
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
    fun `community cards should be hidden at stage 0`() {
        assertThat(round.getRevealedCommunityCards()).isEmpty()
    }

    @Test
    fun `community cards revealed correctly after each stage`() {
        assertThat(round.getRevealedCommunityCards()).hasSize(0)
        // Preflop to flop
        round.doAction(Action.CALL) // Alice
        round.doAction(Action.CALL) // Bob
        round.doAction(Action.CALL) // Caroline
        assertThat(round.getRevealedCommunityCards()).hasSize(3)

        // Flop to turn
        round.doAction(Action.CHECK)
        round.doAction(Action.CHECK)
//        round.doAction(Action.CHECK)
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
        round.doAction(Action.CALL)

        repeat(2) {
            round.doAction(Action.CHECK)
            round.doAction(Action.CHECK)
            round.doAction(Action.CHECK)
        }

        assertThat(round.isHandComplete).isTrue()
        assertThat(round.pot).isEqualTo(0)
        assertThat(players.sumOf { it.money }).isEqualTo(3000)
    }

    @Test
    fun `player cannot tigger an action, after the hand end`() {
        round.doAction(Action.CALL)
        round.doAction(Action.CALL)
        round.doAction(Action.CALL)

        repeat(2) {
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
        round.doAction(Action.CALL)
        round.doAction(Action.CALL)
        round.doAction(Action.CALL)

        repeat(2) {
            round.doAction(Action.CHECK)
            round.doAction(Action.CHECK)
            round.doAction(Action.CHECK)
        }

        val totalMoney = players.sumOf { it.money }
        assertThat(totalMoney).isEqualTo(3000)
    }

    @Test
    fun `toState and fromState preserve state`() {
        val state = round.toState()

        val restoredRound = Round(players)
        restoredRound.fromState(state)

        assertThat(restoredRound.stage).isEqualTo(round.stage)
        assertThat(restoredRound.pot).isEqualTo(round.pot)
        assertThat(restoredRound.getRevealedCommunityCards())
            .containsExactlyElementsOf(round.getRevealedCommunityCards())
    }
}