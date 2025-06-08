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

//object HandEvaluator {
//  fun evaluateBestHandFrom(cards: List<Card>): HandRank {
//    // all 5-card combos from 7 cards
//    val allCombos = cards.combinations(5)
//    return allCombos.maxOf { evaluateFiveCardHand(it) }
//  }
//
//  /** Evaluates a 5-card hand and returns its rank. */
//  private fun evaluateFiveCardHand(cards: List<Card>): HandRank {
//    val sorted = cards.sortedByDescending { it.rank.value }
//    val rankGroups =
//      cards
//        .groupBy { it.rank }
//        .mapValues { it.value.size }
//        .toList()
//        .sortedWith(
//          compareByDescending<Pair<CardRank, Int>> { it.second }
//            .thenByDescending { it.first.value }
//        )
//
//    val isFlush = cards.groupBy { it.suit }.values.any { it.size == 5 }
//    val straightRanks = findStraightRanks(cards.map { it.rank }.toSet())
//
//    return when {
//      isFlush && straightRanks.isNotEmpty() -> {
//        if (straightRanks ==
//          listOf(
//            CardRank.ACE,
//            CardRank.KING,
//            CardRank.QUEEN,
//            CardRank.JACK,
//            CardRank.TEN
//          )
//        ) {
//          HandRank(
//            type = HandType.ROYAL_FLUSH,
//            cardRank = straightRanks
//          )
//        } else {
//          HandRank(
//            type = HandType.STRAIGHT_FLUSH,
//            cardRank = straightRanks
//          )
//        }
//      }
//
//      rankGroups.first().second == 4 -> {
//        HandRank(
//          type = HandType.FOUR_OF_A_KIND,
//          cardRank = listOf(rankGroups[0].first, rankGroups[1].first)
//        )
//      }
//
//      rankGroups.first().second == 3 && rankGroups[1].second >= 2 -> {
//        HandRank(
//          type = HandType.FULL_HOUSE,
//          cardRank = listOf(rankGroups[0].first, rankGroups[1].first)
//        )
//      }
//
//      isFlush -> {
//        HandRank(
//          type = HandType.FLUSH,
//          cardRank = sorted.map { it.rank }
//        )
//      }
//
//      straightRanks.isNotEmpty() -> {
//        HandRank(
//          type = HandType.STRAIGHT,
//          cardRank = straightRanks
//        )
//      }
//
//      rankGroups.first().second == 3 -> {
//        HandRank(
//          type = HandType.THREE_OF_A_KIND,
//          cardRank = listOf(rankGroups[0].first) + rankGroups.drop(1)
//            .map { it.first }
//        )
//      }
//
//      rankGroups.first().second == 2 && rankGroups[1].second == 2 -> {
//        HandRank(
//          type = HandType.TWO_PAIR,
//          cardRank = listOf(
//            rankGroups[0].first,
//            rankGroups[1].first,
//            rankGroups[2].first
//          )
//        )
//      }
//
//      rankGroups.first().second == 2 -> {
//        HandRank(
//          type = HandType.ONE_PAIR,
//          cardRank = listOf(rankGroups[0].first) + rankGroups.drop(1)
//            .map { it.first }
//        )
//      }
//
//      else -> {
//        HandRank(
//          type = HandType.HIGH_CARD,
//          cardRank = sorted.map { it.rank }
//        )
//      }
//    }
//  }
//

//
//}

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
