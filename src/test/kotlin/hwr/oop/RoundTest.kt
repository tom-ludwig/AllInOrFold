package hwr.oop

import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat

class RoundTest: AnnotationSpec() {

    @Test
    fun `Round number is 0 at start`() {
        val round = Round()

        val roundNum = round.round

        assertThat(roundNum).isEqualTo(0)
    }

    @Test
    fun `Round number is 1 after next Round`() {
        val round = Round()

        round.nextRound()
        val roundNum = round.round

        assertThat(roundNum).isEqualTo(1)
    }

    @Test
    fun `added Card is added`() {
        val round = Round()
        val card = Card(CardRank.FIVE, CardSuit.SPADES)

        round.addCommunityCard(card)
        val roundHasCard = round.communityCards.contains(card)

        assertThat(roundHasCard).isTrue()
    }

    @Test
    fun `Pot is 25 after adding 25`() {
        val round = Round()

        round.addToPot(25)
        val pot = round.pot

        assertThat(pot).isEqualTo(25)
    }
}