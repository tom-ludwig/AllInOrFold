package hwr.oop.group1.poker

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

        assertThat(player.hole).contains(card)
    }

    @Test
    fun `Player Money is 10 after adding 10`() {
        val player = Player("Max")

        player.addMoney(10)
        val playerMoney = player.money

        assertThat(playerMoney).isEqualTo(10)
    }
    @Test
    fun `fold sets hasFolded to true `() {

        val player = Player("Saruman", money = 50)
        assertThat(player.hasFolded).isFalse()  // vorab


        player.fold()


        assertThat(player.hasFolded).isTrue()
    }

    @Test 
    fun `fold clears hole`() {
        val player = Player("Saruman", money = 50)
        player.addCard(Card(CardRank.ACE, CardSuit.SPADES))
        player.addCard(Card(CardRank.KING, CardSuit.HEARTS))
        player.fold()
        assertThat(player.hasFolded).isTrue()
        assertThat(player.hole).isEmpty()
    }

    @Test
    fun `resetFold clears fold status`() {

        val player = Player("Saruman", money = 50)
        player.addCard(Card(CardRank.ACE, CardSuit.SPADES))
        player.addCard(Card(CardRank.KING, CardSuit.HEARTS))
        player.fold()
        assertThat(player.hasFolded).isTrue()
        player.resetFold()
        assertThat(player.hasFolded).isFalse()
    }

    @Test
    fun `player state is correctly converted and restored`() {
        val original = Player(name = "Voldemort", money = 300)
        original.addCard(Card(CardRank.ACE, CardSuit.SPADES))
        original.addCard(Card(CardRank.TEN, CardSuit.HEARTS))
        original.fold() // player fold → hasFolded = true, clear hand

        val state = original.toState() // serialize player in map

        val copy = Player()
        copy.fromState(state)


        assertThat(copy.name).isEqualTo("Voldemort")
        assertThat(copy.money).isEqualTo(300)


        assertThat(copy.hasFolded).isTrue()


        assertThat(copy.hole).isEmpty()
    }
}

