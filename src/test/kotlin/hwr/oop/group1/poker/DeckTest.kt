package hwr.oop.group1.poker

import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat

class DeckTest: AnnotationSpec() {

    private fun getRemainingCards(deck: Deck): List<Card> {
        val cards = generateSequence { deck.draw() }.toList()

        return cards
    }

    @Test
    fun `Deck includes all cards`() {
        val deck = Deck()

        val cards = getRemainingCards(deck)
        assertThat(cards.toSet()).hasSize(cards.size).hasSize(52)
    }

    @Test
    fun `Drawing draws card and removes it`() {
        val deck = Deck()

        val drawnCard = deck.draw()

        val remainingCards = getRemainingCards(deck)

        assertThat(remainingCards)
            .describedAs("The deck should no longer contain the drawn card")
            .doesNotContain(drawnCard)
    }

    @Test
    fun `Constructed Decks are Shuffled`() {
        val deck = Deck()
        val newDeck = Deck()

        assertThat(getRemainingCards(deck)).isNotEqualTo(getRemainingCards(newDeck))
    }
}