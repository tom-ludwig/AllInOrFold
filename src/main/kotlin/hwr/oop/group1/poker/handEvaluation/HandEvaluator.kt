package hwr.oop.group1.poker.handEvaluation

import hwr.oop.group1.poker.Card
import hwr.oop.group1.poker.handEvaluation.detectionStrategie.*

object HandEvaluator {
    private val strategies = listOf(
        StraightFlushDetector(),
        FourOfAKindDetector(),
        FullHouseDetector(),
        FlushDetector(),
        StraightDetector(),
        ThreeOfAKindDetector(),
        TwoPairDetector(),
        OnePairDetector(),
        HighCardDetector()
    )

    fun evaluateBestHandFrom(cards: List<Card>): HandRank {
        return cards.combinations(5)
            .map { evaluateFiveCardHand(it) }
            .maxBy { it }
    }

    private fun evaluateFiveCardHand(cards: List<Card>): HandRank {
        val rankGroups = RankGroups(cards)
        for (strategy in strategies) {
            val result = strategy.detect(cards, rankGroups)
            if (result != null) return result
        }
        throw IllegalStateException("No strategy matched for cards: $cards")
    }

    private fun List<Card>.combinations(combinationSize: Int): List<List<Card>> {
        fun combine(
            startIndex: Int,
            currentCombination: List<Card>,
        ): List<List<Card>> {
            if (currentCombination.size == combinationSize) return listOf(
                currentCombination
            )
            if (startIndex >= this.size) return emptyList()
            return combine(
                startIndex + 1,
                currentCombination + this[startIndex]
            ) + combine(
                startIndex + 1,
                currentCombination
            )
        }
        return combine(0, emptyList())
    }
}
