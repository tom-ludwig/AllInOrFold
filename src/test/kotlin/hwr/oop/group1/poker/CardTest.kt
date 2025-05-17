package hwr.oop.group1.poker

import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat

class CardTest: AnnotationSpec() {

    @Test
    fun `Card King of Diamonds has suit and rank`(){
        val card = Card(CardRank.KING, CardSuit.DIAMONDS)

        val rank = card.rank
        val suit = card.suit

        assertThat(rank).isEqualTo(CardRank.KING)
        assertThat(suit).isEqualTo(CardSuit.DIAMONDS)
    }
    @Test
    fun `card is correctly converted and restored`() {
        val original = Card(CardRank.QUEEN, CardSuit.DIAMONDS)

        val state = original.toState()
        val copy = Card.fromState(state)


        assertThat(copy.rank).isEqualTo(CardRank.QUEEN)
        assertThat(copy.suit).isEqualTo(CardSuit.DIAMONDS)
        assertThat(copy).isEqualTo(original)
    }
}