package hwr.oop

import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat

class DeckTest: AnnotationSpec() {
    @Test
    fun `Deck includes all cards`() {
        val deck = Deck()

        deck.resetDeck()

        assertThat(deck.cards.toSet()).hasSize(deck.cards.size).hasSize(52)
    }

    @Test
    fun `Drawing draws top card and removes it`() {
        val deck = Deck()

        deck.resetDeck()
        val cardAtTop = deck.cards.first()
        val card = deck.draw()

        assertThat(cardAtTop).isEqualTo(card)
        assertThat(deck.cards).doesNotContain(card)
    }

    @Test
    fun `Shuffling randomises the deck`() {
        val deck = Deck()
        deck.resetDeck()

        val deckOld = deck.cards.toList()
        deck.shuffleDeck()

        assertThat(deckOld).isNotEqualTo(deck.cards)
    }
}