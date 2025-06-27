package hwr.oop.group1.poker.handEvaluation

import hwr.oop.group1.poker.Card
import hwr.oop.group1.poker.CardRank
import hwr.oop.group1.poker.CardSuit
import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat

class RankGroupsTest : AnnotationSpec() {

    @Test
    fun `hasOfAKind returns true when group of size exists`() {
        val cards = listOf(
            Card(CardRank.TEN, CardSuit.HEARTS),
            Card(CardRank.TEN, CardSuit.DIAMONDS),
            Card(CardRank.TEN, CardSuit.SPADES),
            Card(CardRank.THREE, CardSuit.CLUBS),
            Card(CardRank.FOUR, CardSuit.HEARTS)
        )

        val rankGroups = RankGroups(cards)
        assertThat(rankGroups.hasOfAKind(3)).isTrue
        assertThat(rankGroups.hasOfAKind(2)).isFalse
    }

    @Test
    fun `getRanksWithCount returns correct ranks`() {
        val cards = listOf(
            Card(CardRank.KING, CardSuit.HEARTS),
            Card(CardRank.KING, CardSuit.DIAMONDS),
            Card(CardRank.QUEEN, CardSuit.SPADES),
            Card(CardRank.QUEEN, CardSuit.CLUBS),
            Card(CardRank.QUEEN, CardSuit.HEARTS)
        )

        val rankGroups = RankGroups(cards)
        val result2 = rankGroups.getRanksWithCount(2)
        val result3 = rankGroups.getRanksWithCount(3)

        assertThat(result2).containsExactly(CardRank.KING)
        assertThat(result3).containsExactly(CardRank.QUEEN)
    }

    @Test
    fun `topRanks returns ranks sorted by frequency then rank value`() {
        val cards = listOf(
            Card(CardRank.ACE, CardSuit.HEARTS),
            Card(CardRank.ACE, CardSuit.DIAMONDS),
            Card(CardRank.KING, CardSuit.SPADES),
            Card(CardRank.KING, CardSuit.CLUBS),
            Card(CardRank.KING, CardSuit.HEARTS),
            Card(CardRank.QUEEN, CardSuit.DIAMONDS)
        )

        val rankGroups = RankGroups(cards)
        val topRanks = rankGroups.topRanks()

        assertThat(topRanks).containsExactly(
            CardRank.KING,  // 3 times
            CardRank.ACE,   // 2 times
            CardRank.QUEEN  // 1 time
        )
    }

    @Test
    fun `hasOfAKind returns false if no group matches`() {
        val cards = listOf(
            Card(CardRank.FOUR, CardSuit.HEARTS),
            Card(CardRank.SIX, CardSuit.DIAMONDS),
            Card(CardRank.EIGHT, CardSuit.SPADES),
            Card(CardRank.TEN, CardSuit.CLUBS),
            Card(CardRank.JACK, CardSuit.HEARTS)
        )

        val rankGroups = RankGroups(cards)
        assertThat(rankGroups.hasOfAKind(2)).isFalse
        assertThat(rankGroups.hasOfAKind(1)).isTrue
    }

    @Test
    fun `getRanksWithCount returns empty list if none match`() {
        val cards = listOf(
            Card(CardRank.NINE, CardSuit.HEARTS),
            Card(CardRank.TEN, CardSuit.DIAMONDS),
            Card(CardRank.JACK, CardSuit.SPADES)
        )

        val rankGroups = RankGroups(cards)
        assertThat(rankGroups.getRanksWithCount(2)).isEmpty()
    }
}
