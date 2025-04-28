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
}