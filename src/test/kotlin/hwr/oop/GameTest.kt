package hwr.oop

import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat

class GameTest: AnnotationSpec() {

    @Test
    fun `Game has added Player`() {
        val game = Game()
        val player = Player("Max")

        game.addPlayer(player)
        val players = game.players()

        assertThat(players).contains(player)
    }

    @Test
    fun `Deck includes all cards`() {
        val game = Game()

        game.resetDeck()
        val deck = game.deck()

        for(suit in CardSuit.entries){
            for (rank in CardRank.entries){
                val cardInDeck = deck.find { card -> card.rank() == rank && card.suit() == suit }
                assertThat(cardInDeck).isNotNull()
            }
        }
    }

    @Test
    fun `Drawing draws top card and removes it`() {
        val game = Game()

        game.resetDeck()
        val deck = game.deck()
        val cardAtTop = deck.first()
        val card = game.drawCard()

        assertThat(cardAtTop).isEqualTo(card)
        assertThat(deck).doesNotContain(card)
    }

    @Test
    fun `Shuffling randomises the deck`() {
        val game = Game()
        game.resetDeck()

        val deck = game.deck().toList()
        game.shuffleDeck()

        assertThat(deck).isNotEqualTo(game.deck())
    }

    @Test
    fun `Dealer is 1 after new Round`() {
        val game = Game()
        game.addPlayer(Player("Max"))
        game.addPlayer(Player("Ben"))

        game.newRound()
        val dealer = game.dealer()

        assertThat(dealer).isEqualTo(1)
    }
}