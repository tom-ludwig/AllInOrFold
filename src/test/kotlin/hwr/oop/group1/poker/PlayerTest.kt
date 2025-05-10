package hwr.oop.group1.poker

import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat

class PlayerTest : AnnotationSpec() {
    @Test
    fun `player name should match constructor parameter`() {
        val player = Player("Test Player", 1000)
        assertThat(player.name).isEqualTo("Test Player")
    }

    @Test
    fun `addCard should add a card to player's hand`() {
        val player = Player("Test Player", 1000)
        val card = Card(CardRank.ACE, CardSuit.HEARTS)
        player.addCard(card)
        assertThat(player.hand).hasSize(1)
        assertThat(player.hand.first()).isEqualTo(card)
    }

    @Test
    fun `addMoney should increase player's money`() {
        val player = Player("Test Player", 1000)
        player.addMoney(500)
        assertThat(player.money).isEqualTo(1500)
    }

    @Test
    fun `updateHand should replace player's hand`() {
        val player = Player("Test Player", 1000)
        val hand = listOf(
            Card(CardRank.ACE, CardSuit.HEARTS),
            Card(CardRank.KING, CardSuit.SPADES)
        )
        player.updateHand(hand)
        assertThat(player.hand).isEqualTo(hand)
    }
}