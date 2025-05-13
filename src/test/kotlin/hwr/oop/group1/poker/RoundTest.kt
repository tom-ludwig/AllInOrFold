package hwr.oop.group1.poker

import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy

class RoundTest : AnnotationSpec() {
    private lateinit var round: Round

    @BeforeEach
    fun setup() {
        round = Round(deck = Deck())
    }

    @Test
    fun `stage starts at pre-flop`() {
        assertThat(round.stage).isEqualTo(0)
        assertThat(round.getRevealedCommunityCards()).isEmpty()
    }

    @Test
    fun `nextStage advances through stages correctly`() {
        // Pre-flop to Flop
        round.nextStage()
        assertThat(round.stage).isEqualTo(1)
        
        // Flop to Turn
        round.nextStage()
        assertThat(round.stage).isEqualTo(2)
        
        // Turn to River
        round.nextStage()
        assertThat(round.stage).isEqualTo(3)
        
        // Cannot advance past River
        assertThatThrownBy { round.nextStage() }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Cannot advance past the river")
    }

    @Test
    fun `getRevealedCommunityCards shows correct cards for each stage`() {
        // Pre-flop: No cards
        assertThat(round.getRevealedCommunityCards()).isEmpty()

        // Flop: First 3 cards
        round.nextStage()
        assertThat(round.getRevealedCommunityCards()).hasSize(3)

        // Turn: First 4 cards
        round.nextStage()
        assertThat(round.getRevealedCommunityCards()).hasSize(4)

        // River: All 5 cards
        round.nextStage()
        assertThat(round.getRevealedCommunityCards()).hasSize(5)
    }

    @Test
    fun `addToPot validates amount`() {
        assertThatThrownBy { round.addToPot(-10) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Cannot add negative amount to pot")

        round.addToPot(10)
        assertThat(round.pot).isEqualTo(10)
    }

    @Test
    fun `setPot validates amount`() {
        assertThatThrownBy { round.setPot(-10) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Pot cannot be negative")

        round.setPot(10)
        assertThat(round.pot).isEqualTo(10)
    }

    @Test
    fun `setStage validates stage`() {
        assertThatThrownBy { round.setStage(-1) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Invalid stage: -1")

        assertThatThrownBy { round.setStage(4) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Invalid stage: 4")

        round.setStage(2)
        assertThat(round.stage).isEqualTo(2)
    }

    @Test
    fun `clearCommunityCards removes all cards`() {
        round.clearCommunityCards()
        assertThat(round.communityCards).isEmpty()
    }

    @Test
    fun `state serialization preserves all values`() {
        // Setup round state
        round.setPot(100)
        round.setStage(2)

        // Serialize and deserialize
        val state = round.toState()
        val newRound = Round(deck = Deck())
        newRound.fromState(state)

        // Verify all values
        assertThat(newRound.communityCards).hasSize(5)
        assertThat(newRound.communityCards[0].rank).isEqualTo(round.communityCards[0].rank)
        assertThat(newRound.communityCards[0].suit).isEqualTo(round.communityCards[0].suit)
        assertThat(newRound.communityCards[1].rank).isEqualTo(round.communityCards[1].rank)
        assertThat(newRound.communityCards[1].suit).isEqualTo(round.communityCards[1].suit)
        assertThat(newRound.pot).isEqualTo(100)
        assertThat(newRound.stage).isEqualTo(2)
    }
}