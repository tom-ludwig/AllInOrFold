package hwr.oop.group1.poker

import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat

class DeckTest: AnnotationSpec() {
    // Reflection to access private field
    private fun getCards(deck: Deck): List<Card> {
        // Bypass private access to check community cards
        val field = Deck::class.java.getDeclaredField("cards")
        field.isAccessible = true
        val cards =  field.get(deck) as List<*>
        return cards.filterIsInstance<Card>().takeIf { it.size == cards.size } ?: emptyList()
    }

    @Test
    fun `Deck includes all cards`() {
        val deck = Deck()

        val cards = getCards(deck)
        assertThat(cards.toSet()).hasSize(cards.size).hasSize(52)
    }

    @Test
    fun `Drawing draws top card and removes it`() {
        val deck = Deck()

        val initialCards = getCards(deck)
        val expectedTopCard = initialCards.first()
        val drawnCard = deck.draw()

        val remainingCards = getCards(deck)

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

        assertThat(getCards(deck)).isNotEqualTo(getCards(newDeck))
    }
}