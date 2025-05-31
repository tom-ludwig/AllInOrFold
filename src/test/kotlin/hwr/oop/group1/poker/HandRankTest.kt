package hwr.oop.group1.poker

import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat

class HandRankTest: AnnotationSpec() {
    @Test
    fun `detects royal flush`() {
        val cards = listOf(
            Card(CardRank.TEN, CardSuit.HEARTS),
            Card(CardRank.JACK, CardSuit.HEARTS),
            Card(CardRank.QUEEN, CardSuit.HEARTS),
            Card(CardRank.KING, CardSuit.HEARTS),
            Card(CardRank.ACE, CardSuit.HEARTS),
            Card(CardRank.TWO, CardSuit.CLUBS),
            Card(CardRank.THREE, CardSuit.DIAMONDS)
        )
        val result = HandEvaluator.evaluateBestHandFrom(cards)

        assertThat(HandType.ROYAL_FLUSH).isEqualTo(result.type)
    }

    @Test
    fun `detects straight flush`() {
        val cards = listOf(
            Card(CardRank.SIX, CardSuit.SPADES),
            Card(CardRank.SEVEN, CardSuit.SPADES),
            Card(CardRank.EIGHT, CardSuit.SPADES),
            Card(CardRank.NINE, CardSuit.SPADES),
            Card(CardRank.TEN, CardSuit.SPADES),
            Card(CardRank.TWO, CardSuit.CLUBS),
            Card(CardRank.THREE, CardSuit.HEARTS)
        )
        val result = HandEvaluator.evaluateBestHandFrom(cards)

        assertThat(HandType.STRAIGHT_FLUSH).isEqualTo(result.type)
    }

    @Test
    fun `detects four of a kind`() {
        val cards = listOf(
            Card(CardRank.FIVE, CardSuit.HEARTS),
            Card(CardRank.FIVE, CardSuit.DIAMONDS),
            Card(CardRank.FIVE, CardSuit.CLUBS),
            Card(CardRank.FIVE, CardSuit.SPADES),
            Card(CardRank.TEN, CardSuit.HEARTS),
            Card(CardRank.TWO, CardSuit.CLUBS),
            Card(CardRank.THREE, CardSuit.DIAMONDS)
        )
        val result = HandEvaluator.evaluateBestHandFrom(cards)

        assertThat(HandType.FOUR_OF_A_KIND).isEqualTo(result.type)
    }

    @Test
    fun `detects full house`() {
        val cards = listOf(
            Card(CardRank.KING, CardSuit.HEARTS),
            Card(CardRank.KING, CardSuit.CLUBS),
            Card(CardRank.KING, CardSuit.DIAMONDS),
            Card(CardRank.THREE, CardSuit.HEARTS),
            Card(CardRank.THREE, CardSuit.SPADES),
            Card(CardRank.ACE, CardSuit.HEARTS),
            Card(CardRank.TWO, CardSuit.DIAMONDS)
        )
        val result = HandEvaluator.evaluateBestHandFrom(cards)

        assertThat(HandType.FULL_HOUSE).isEqualTo(result.type)
    }

    @Test
    fun `detects flush`() {
        val cards = listOf(
            Card(CardRank.TWO, CardSuit.HEARTS),
            Card(CardRank.FIVE, CardSuit.HEARTS),
            Card(CardRank.NINE, CardSuit.HEARTS),
            Card(CardRank.JACK, CardSuit.HEARTS),
            Card(CardRank.KING, CardSuit.HEARTS),
            Card(CardRank.THREE, CardSuit.CLUBS),
            Card(CardRank.FOUR, CardSuit.SPADES)
        )
        val result = HandEvaluator.evaluateBestHandFrom(cards)

        assertThat(HandType.FLUSH).isEqualTo(result.type)
    }

    @Test
    fun `detects straight`() {
        val cards = listOf(
            Card(CardRank.THREE, CardSuit.CLUBS),
            Card(CardRank.FOUR, CardSuit.DIAMONDS),
            Card(CardRank.FIVE, CardSuit.HEARTS),
            Card(CardRank.SIX, CardSuit.SPADES),
            Card(CardRank.SEVEN, CardSuit.HEARTS),
            Card(CardRank.KING, CardSuit.CLUBS),
            Card(CardRank.ACE, CardSuit.DIAMONDS)
        )
        val result = HandEvaluator.evaluateBestHandFrom(cards)

        assertThat(HandType.STRAIGHT).isEqualTo(result.type)
    }

    @Test
    fun `detects three of a kind`() {
        val cards = listOf(
            Card(CardRank.NINE, CardSuit.CLUBS),
            Card(CardRank.NINE, CardSuit.HEARTS),
            Card(CardRank.NINE, CardSuit.DIAMONDS),
            Card(CardRank.FOUR, CardSuit.SPADES),
            Card(CardRank.SEVEN, CardSuit.CLUBS),
            Card(CardRank.JACK, CardSuit.SPADES),
            Card(CardRank.ACE, CardSuit.DIAMONDS)
        )
        val result = HandEvaluator.evaluateBestHandFrom(cards)

        assertThat(HandType.THREE_OF_A_KIND).isEqualTo(result.type)
    }

    @Test
    fun `detects two pair`() {
        val cards = listOf(
            Card(CardRank.TEN, CardSuit.HEARTS),
            Card(CardRank.TEN, CardSuit.CLUBS),
            Card(CardRank.FOUR, CardSuit.DIAMONDS),
            Card(CardRank.FOUR, CardSuit.SPADES),
            Card(CardRank.SEVEN, CardSuit.HEARTS),
            Card(CardRank.KING, CardSuit.CLUBS),
            Card(CardRank.ACE, CardSuit.HEARTS)
        )
        val result = HandEvaluator.evaluateBestHandFrom(cards)

        assertThat(HandType.TWO_PAIR).isEqualTo(result.type)
    }

    @Test
    fun `detects one pair`() {
        val cards = listOf(
            Card(CardRank.THREE, CardSuit.HEARTS),
            Card(CardRank.THREE, CardSuit.CLUBS),
            Card(CardRank.SIX, CardSuit.DIAMONDS),
            Card(CardRank.NINE, CardSuit.SPADES),
            Card(CardRank.JACK, CardSuit.HEARTS),
            Card(CardRank.KING, CardSuit.CLUBS),
            Card(CardRank.ACE, CardSuit.SPADES)
        )
        val result = HandEvaluator.evaluateBestHandFrom(cards)

        assertThat(HandType.ONE_PAIR).isEqualTo(result.type)
    }

    @Test
    fun `detects high Card`() {
        val cards = listOf(
            Card(CardRank.TWO, CardSuit.HEARTS),
            Card(CardRank.FIVE, CardSuit.CLUBS),
            Card(CardRank.SEVEN, CardSuit.DIAMONDS),
            Card(CardRank.NINE, CardSuit.SPADES),
            Card(CardRank.JACK, CardSuit.HEARTS),
            Card(CardRank.KING, CardSuit.CLUBS),
            Card(CardRank.ACE, CardSuit.SPADES)
        )
        val result = HandEvaluator.evaluateBestHandFrom(cards)

        assertThat(HandType.HIGH_CARD).isEqualTo(result.type)
    }

    @Test
    fun `hand comparison respects hand strength`() {
        val flush = HandRank(HandType.FLUSH, listOf(CardRank.KING, CardRank.QUEEN, CardRank.TEN))
        val straight = HandRank(HandType.STRAIGHT, listOf(CardRank.JACK, CardRank.TEN, CardRank.NINE))

        assertThat(flush).isGreaterThan(straight)
    }

    @Test
    fun `hand comparison resolves with tiebreaker ranks`() {
        val pairA = HandRank(HandType.ONE_PAIR, listOf(CardRank.TEN, CardRank.NINE, CardRank.EIGHT, CardRank.FOUR))
        val pairB = HandRank(HandType.ONE_PAIR, listOf(CardRank.TEN, CardRank.NINE, CardRank.SEVEN, CardRank.FOUR))

        assertThat(pairA).isGreaterThan(pairB)
    }

    @Test
    fun `equal hands compare equal`() {
        val hand1 = HandRank(HandType.FULL_HOUSE, listOf(CardRank.QUEEN, CardRank.TEN))
        val hand2 = HandRank(HandType.FULL_HOUSE, listOf(CardRank.QUEEN, CardRank.TEN))

        assertThat(hand1.compareTo(hand2)).isEqualTo(0)
    }

    @Test
    fun `get all combinations`() {
        val card1 = Card(CardRank.ACE, CardSuit.HEARTS)
        val card2 = Card(CardRank.TWO, CardSuit.HEARTS)
        val card3 = Card(CardRank.THREE, CardSuit.HEARTS)
        val cards = listOf(
            card1,
            card2,
            card3
        )
        val combinations = cards.combinations(2)

        assertThat(combinations).isEqualTo(listOf(
            listOf(card1, card2),
            listOf(card1, card3),
            listOf(card2, card3))
        )
    }

    @Test
    fun `combinations returns correct number of combos`() {
        val input = listOf(
            Card(CardRank.ACE, CardSuit.HEARTS),
            Card(CardRank.TWO, CardSuit.HEARTS),
            Card(CardRank.THREE, CardSuit.HEARTS),
            Card(CardRank.FOUR, CardSuit.HEARTS),
            Card(CardRank.FIVE, CardSuit.HEARTS),
            Card(CardRank.SIX, CardSuit.HEARTS),
            Card(CardRank.SEVEN, CardSuit.HEARTS)
        )
        val result = input.combinations(5)

        assertThat(21).isEqualTo(result.size) // C(7,5) = 21
        assertThat(result.all { it.size == 5 }).isTrue()
    }

    @Test
    fun `combinations edge case empty list`() {
        val input = emptyList<Card>()
        val result = input.combinations(3)

        assertThat(result.isEmpty()).isTrue()
    }
}