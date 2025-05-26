package hwr.oop.group1.poker

import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat

class DeckTest: AnnotationSpec() {

    @Test
    fun `Deck includes all cards`() {
        val deck = Deck()

        val cards = deck.cards
        assertThat(cards.toSet()).hasSize(cards.size).hasSize(52)
    }

    @Test
    fun `Drawing draws top card and removes it`() {
        val deck = Deck()

        val expectedTopCard = deck.cards.first()
        val drawnCard = deck.draw()

        val remainingCards = deck.cards

        assertThat(drawnCard)
            .describedAs("The drawn card should be the top card of the deck")
            .isEqualTo(expectedTopCard)

        assertThat(remainingCards)
            .describedAs("The deck should no longer contain the drawn card")
            .doesNotContain(drawnCard)
    }

    @Test
    fun `Constructed Decks are Shuffled`() {
        val deck = Deck()
        val newDeck = Deck()

        assertThat(deck.cards).isNotEqualTo(newDeck.cards)
    }
}