package hwr.oop

import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat

class DeckTest: AnnotationSpec() {
    @Test
    fun `Deck includes all cards`() {
        val deck = Deck()

        deck.resetDeck()

        for(suit in CardSuit.entries){
            for (rank in CardRank.entries){
                val cardInDeck = deck.deck().find { card -> card.rank() == rank && card.suit() == suit }
                assertThat(cardInDeck).isNotNull()
            }
        }
    }

    @Test
    fun `Drawing draws top card and removes it`() {
        val deck = Deck()

        deck.resetDeck()
        val cardAtTop = deck.deck().first()
        val card = deck.drawCard()

        assertThat(cardAtTop).isEqualTo(card)
        assertThat(deck.deck()).doesNotContain(card)
    }

    @Test
    fun `Shuffling randomises the deck`() {
        val deck = Deck()
        deck.resetDeck()

        val deckOld = deck.deck().toList()
        deck.shuffleDeck()

        assertThat(deckOld).isNotEqualTo(deck.deck())
    }
}