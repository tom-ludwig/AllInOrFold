package hwr.oop

import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat

class RoundTest: AnnotationSpec() {

    @Test
    fun `stage is 0 at start`() {
        val round = Round()

        val stage = round.stage

        assertThat(stage).isEqualTo(0)
    }

    @Test
    fun `stage is 1 after next Stage`() {
        val round = Round()

        round.nextStage()
        val stage = round.stage

        assertThat(stage).isEqualTo(1)
    }

    @Test
    fun `There are 5 community cards at the start`(){
        val round = Round()
        assertThat(round.communityCards.size).isEqualTo(5)
    }

    @Test
    fun `correct amount of revealed community cards in stages`() {
        val round = Round()

        assertThat(round.getRevealedCommunityCards().size).isEqualTo(0)

        round.nextStage()
        assertThat(round.getRevealedCommunityCards().size).isEqualTo(3)

        round.nextStage()
        assertThat(round.getRevealedCommunityCards().size).isEqualTo(4)

        round.nextStage()
        assertThat(round.getRevealedCommunityCards().size).isEqualTo(5)
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