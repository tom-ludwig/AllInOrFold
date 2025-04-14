package hwr.oop

import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat

class PlayerTest: AnnotationSpec() {

    @Test
    fun `Player Max has name Max`() {
        val player = Player("Max")

        val name = player.name

        assertThat(name).isEqualTo("Max")
    }

    @Test
    fun `Player has added Card`() {
        val player = Player("Max")
        val card = Card(CardRank.FIVE, CardSuit.SPADES)

        player.addCard(card)

        assertThat(player.hand).contains(card)
    }

    @Test
    fun `Player Money is 10 after adding 10`() {
        val player = Player("Max")

        player.addMoney(10)
        val playerMoney = player.money

        assertThat(playerMoney).isEqualTo(10)
    }
}