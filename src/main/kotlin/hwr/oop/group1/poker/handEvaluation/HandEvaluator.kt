package hwr.oop.group1.poker.handEvaluation

import hwr.oop.group1.poker.Card
import hwr.oop.group1.poker.CardRank
import hwr.oop.group1.poker.handEvaluation.HandRank
import hwr.oop.group1.poker.HandType
import hwr.oop.group1.poker.handEvaluation.detectionStrategie.FlushStrategy
import hwr.oop.group1.poker.handEvaluation.detectionStrategie.FourOfAKindStrategy
import hwr.oop.group1.poker.handEvaluation.detectionStrategie.FullHouseStrategy
import hwr.oop.group1.poker.handEvaluation.detectionStrategie.HighCardStrategy
import hwr.oop.group1.poker.handEvaluation.detectionStrategie.OnePairStrategy
import hwr.oop.group1.poker.handEvaluation.detectionStrategie.StraightFlushStrategy
import hwr.oop.group1.poker.handEvaluation.detectionStrategie.StraightStrategy
import hwr.oop.group1.poker.handEvaluation.detectionStrategie.ThreeOfAKindStrategy
import hwr.oop.group1.poker.handEvaluation.detectionStrategie.TwoPairStrategy

object HandEvaluator {
  private val strategies = listOf(
    StraightFlushStrategy(),
    FourOfAKindStrategy(),
    FullHouseStrategy(),
    FlushStrategy(),
    StraightStrategy(),
    ThreeOfAKindStrategy(),
    TwoPairStrategy(),
    OnePairStrategy(),
    HighCardStrategy()
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
