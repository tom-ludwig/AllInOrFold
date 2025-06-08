package hwr.oop.group1.poker.handEvaluation.detectionStrategie

import hwr.oop.group1.poker.Card
import hwr.oop.group1.poker.CardRank
import hwr.oop.group1.poker.HandType
import hwr.oop.group1.poker.handEvaluation.HandRank
import hwr.oop.group1.poker.handEvaluation.RankGroups

class StraightFlushStrategy : HandDetectionStrategy {
  override fun detect(cards: List<Card>, rankGroups: RankGroups): HandRank? {
    val flushSuitGroup = cards.groupBy { it.suit }.values.find { it.size >= 5 } ?: return null
    val flushCards = flushSuitGroup.sortedByDescending { it.rank.value }
    val straight = findStraightRanks(flushCards.map { it.rank }.toSet())
    if (straight.isEmpty()) return null

    return if (straight == listOf(CardRank.ACE, CardRank.KING, CardRank.QUEEN, CardRank.JACK, CardRank.TEN))
      HandRank(HandType.ROYAL_FLUSH, straight)
    else
      HandRank(HandType.STRAIGHT_FLUSH, straight)
  }
}
/**
 * Checks for a straight (five consecutive card ranks) in the given set of ranks. Returns an
 * empty list if no straight was found in set of cards
 */
fun findStraightRanks(ranks: Set<CardRank>): List<CardRank> {
  val uniqueValues = ranks.map { it.value }.toSet().toMutableSet()

  // Handle Ace as 1 for A-2-3-4-5
  if (CardRank.ACE in ranks) uniqueValues.add(1)

  val sortedValues = uniqueValues.sortedDescending()
  for (i in 0..sortedValues.size - 5) {
    val window = sortedValues.subList(i, i + 5)
    if (window.zipWithNext().all { it.first - it.second == 1 }) {
      return window.map { v ->
        CardRank.values()
          .first { it.value == (if (v == 1) 14 else v) }
      }
    }
  }
  return emptyList()
}