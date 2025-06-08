package hwr.oop.group1.poker.handEvaluation.detectionStrategie

import hwr.oop.group1.poker.Card
import hwr.oop.group1.poker.HandType
import hwr.oop.group1.poker.handEvaluation.HandRank
import hwr.oop.group1.poker.handEvaluation.RankGroups

class TwoPairStrategy : HandDetectionStrategy {
  override fun detect(cards: List<Card>, rankGroups: RankGroups): HandRank? {
    val pairs = rankGroups.getRanksWithCount(2)
    if (pairs.size < 2) return null

    val topTwo = pairs.take(2)
    val kicker = rankGroups.topRanks().first { it !in topTwo }
    return HandRank(HandType.TWO_PAIR, topTwo + kicker)
  }
}
