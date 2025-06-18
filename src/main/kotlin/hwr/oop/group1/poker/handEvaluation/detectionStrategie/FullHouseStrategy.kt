package hwr.oop.group1.poker.handEvaluation.detectionStrategie

import hwr.oop.group1.poker.Card
import hwr.oop.group1.poker.HandType
import hwr.oop.group1.poker.handEvaluation.HandRank
import hwr.oop.group1.poker.handEvaluation.RankGroups

class FullHouseStrategy : HandDetector {
  override fun detect(
    cards: List<Card>,
    rankGroups: RankGroups
  ): HandRank? {
    val threes = rankGroups.getRanksWithCount(3)
    val pairs = rankGroups.getRanksWithCount(2)

    if (threes.isNotEmpty() && (pairs.isNotEmpty() || threes.size > 1)) {
      val triple = threes.first()
      val pair = (pairs + threes.drop(1)).first()
      return HandRank(HandType.FULL_HOUSE, listOf(triple, pair))
    }
    return null
  }
}

