package hwr.oop.group1.poker

import io.kotest.core.spec.style.AnnotationSpec
import kotlinx.serialization.json.Json
import org.assertj.core.api.Assertions.assertThat
import java.io.File

class CardTest : AnnotationSpec() {

  @Test
  fun `Card King of Diamonds has suit and rank`() {
    val card = Card(CardRank.KING, CardSuit.DIAMONDS)

    val rank = card.rank
    val suit = card.suit

    assertThat(rank).isEqualTo(CardRank.KING)
    assertThat(suit).isEqualTo(CardSuit.DIAMONDS)
  }

  @Test
  fun `card can be saved and loaded`() {
    val json = Json
    val file = File("test_game.json")
    val expectedCard = Card(CardRank.KING, CardSuit.DIAMONDS)

    file.writeText(json.encodeToString(expectedCard))

    val loadedCard = json.decodeFromString<Card>(file.readText())

    file.delete()

    assertThat(loadedCard).isNotNull()
    assertThat(loadedCard).usingRecursiveComparison().isEqualTo(expectedCard)
  }

  @Test
  fun `card toString handles all rank-suit combinations`() {
    // Comprehensive test to ensure all combinations work
    // This helps catch any conditional mutations that might affect specific combinations
    CardRank.values().forEach { rank ->
      CardSuit.values().forEach { suit ->
        val card = Card(rank, suit)
        val result = card.toString()

        val expectedRank = rank.name.lowercase().replaceFirstChar { it.uppercase() }
        val expectedSuit = suit.name.lowercase().replaceFirstChar { it.uppercase() }
        val expected = "$expectedRank of $expectedSuit"

        assertThat(result).isEqualTo(expected)
      }
    }
  }
}