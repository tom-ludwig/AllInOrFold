package hwr.oop.group1.poker

import io.kotest.core.spec.style.AnnotationSpec
import kotlinx.serialization.json.Json
import org.assertj.core.api.Assertions.assertThat
import java.io.File

class PlayerTest : AnnotationSpec() {

    @Test
    fun `Player Max has name Max`() {
        val player = Player("Max", 1000)

        val name = player.name

        assertThat(name).isEqualTo("Max")
    }

    @Test
    fun `Player has added Card`() {
        val player = Player("Max", 1000)
        val card = Card(CardRank.FIVE, CardSuit.SPADES)

        player.addCard(card)

        assertThat(player.hole()).contains(card)
    }

    @Test
    fun `Player isn't active after spending all his money`() {
        val player = Player("Max", 50)

        assertThat(player.isActive()).isTrue()
        assertThat(player.betMoney(50)).isEqualTo(50)

        assertThat(player.isActive()).isFalse()
    }

    @Test
    fun `Player isn't active after folding`() {
        val player = Player("Max", 50)

        assertThat(player.isActive()).isTrue()
        player.fold()

        assertThat(player.isActive()).isFalse()
    }

    @Test
    fun `Players Cards are removed correctly`() {
        val player = Player("Max", 1000)
        val card = Card(CardRank.FIVE, CardSuit.SPADES)
        player.addCard(card)
        player.addCard(card)

        player.clearHole()

        assertThat(player.hole()).isEmpty()
    }

    @Test
    fun `Player Money is 10 after adding 10`() {
        val player = Player("Max", 1000)

        player.addMoney(10)
        val playerMoney = player.money()

        assertThat(playerMoney).isEqualTo(1010)
    }

    @Test
    fun `fold sets hasFolded to true `() {
        val player = Player("Saruman", 50)
        assertThat(player.hasFolded).isFalse()

        player.fold()

        assertThat(player.hasFolded).isTrue()
    }

    @Test
    fun `fold clears hole`() {
        val player = Player("Saruman", 50)
        player.addCard(Card(CardRank.ACE, CardSuit.SPADES))
        player.addCard(Card(CardRank.KING, CardSuit.HEARTS))
        player.fold()
        assertThat(player.hasFolded).isTrue()
        assertThat(player.hole()).isEmpty()
    }

    @Test
    fun `resetFold clears fold status`() {
        val player = Player("Saruman", 50)
        player.addCard(Card(CardRank.ACE, CardSuit.SPADES))
        player.addCard(Card(CardRank.KING, CardSuit.HEARTS))
        player.fold()
        assertThat(player.hasFolded).isTrue()
        player.resetFold()
        assertThat(player.hasFolded).isFalse()
    }

    @Test
    fun `player can be saved and loaded`() {
        val json = Json
        val file = File("test_game.json")
        val expectedPlayer = Player("Max", 1000)

        file.writeText(json.encodeToString(expectedPlayer))

        val loadedPlayer = json.decodeFromString<Player>(file.readText())

        file.delete()

        assertThat(loadedPlayer).isNotNull()
        assertThat(loadedPlayer).usingRecursiveComparison()
            .isEqualTo(expectedPlayer)
    }
}

