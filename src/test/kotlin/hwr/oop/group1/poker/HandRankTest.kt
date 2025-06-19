package hwr.oop.group1.poker

import hwr.oop.group1.poker.handEvaluation.HandEvaluator
import hwr.oop.group1.poker.handEvaluation.HandRank
import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat

class HandRankTest : AnnotationSpec() {
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
        assertThat(result.cardRank).isEqualTo(
            listOf(
                CardRank.TEN,
                CardRank.NINE,
                CardRank.EIGHT,
                CardRank.SEVEN,
                CardRank.SIX
            )
        )
    }

    @Test
    fun `detects bicycle straight`() {
        val cards = listOf(
            Card(CardRank.ACE, CardSuit.SPADES),
            Card(CardRank.TWO, CardSuit.SPADES),
            Card(CardRank.THREE, CardSuit.SPADES),
            Card(CardRank.FOUR, CardSuit.SPADES),
            Card(CardRank.FIVE, CardSuit.SPADES),
            Card(CardRank.QUEEN, CardSuit.CLUBS),
            Card(CardRank.KING, CardSuit.HEARTS)
        )
        val result = HandEvaluator.evaluateBestHandFrom(cards)

        assertThat(HandType.STRAIGHT_FLUSH).isEqualTo(result.type)
        assertThat(result.cardRank).contains(CardRank.ACE) // Ace gets counted as 1 and converted back to an ace
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
        assertThat(result.cardRank.toSet()).isEqualTo(setOf(CardRank.FIVE, CardRank.TEN))
        assertThat(result.cardRank.size).isEqualTo(2)
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
        assertThat(result.cardRank).isEqualTo(
            listOf(
                CardRank.KING,
                CardRank.JACK,
                CardRank.NINE,
                CardRank.FIVE,
                CardRank.TWO
            )
        )
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
        assertThat(result.cardRank.size).isEqualTo(3)
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
        // check kicker:
        assertThat(setOf(CardRank.FOUR, CardRank.TEN, CardRank.ACE)).isEqualTo(result.cardRank.toSet())
        assertThat(result.cardRank.size).isEqualTo(3)
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
        assertThat(result.cardRank.size).isEqualTo(4)
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
        assertThat(result.cardRank).isEqualTo(
            listOf(
                CardRank.ACE,
                CardRank.KING,
                CardRank.JACK,
                CardRank.NINE,
                CardRank.SEVEN
            )
        )
    }

    @Test
    fun `hand comparison respects hand strength`() {
        val flush = HandRank(
            HandType.FLUSH,
            listOf(CardRank.KING, CardRank.QUEEN, CardRank.TEN)
        )
        val straight = HandRank(
            HandType.STRAIGHT,
            listOf(CardRank.JACK, CardRank.TEN, CardRank.NINE)
        )

        assertThat(flush).isGreaterThan(straight)
    }

    @Test
    fun `hand comparison resolves with tiebreaker ranks`() {
        val pairA = HandRank(
            HandType.ONE_PAIR,
            listOf(CardRank.TEN, CardRank.NINE, CardRank.EIGHT, CardRank.FOUR)
        )
        val pairB = HandRank(
            HandType.ONE_PAIR,
            listOf(CardRank.TEN, CardRank.NINE, CardRank.SEVEN, CardRank.FOUR)
        )

        assertThat(pairA).isGreaterThan(pairB)
    }

    @Test
    fun `equal hands compare equal`() {
        val hand1 =
            HandRank(HandType.FULL_HOUSE, listOf(CardRank.QUEEN, CardRank.TEN))
        val hand2 =
            HandRank(HandType.FULL_HOUSE, listOf(CardRank.QUEEN, CardRank.TEN))

        assertThat(hand1.compareTo(hand2)).isEqualTo(0)
    }
}