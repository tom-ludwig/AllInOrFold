package hwr.oop.group1.poker.handEvaluation.detectionStrategie

import hwr.oop.group1.poker.Card
import hwr.oop.group1.poker.HandType
import hwr.oop.group1.poker.handEvaluation.HandRank
import hwr.oop.group1.poker.handEvaluation.RankGroups

class OnePairStrategy : HandDetector {
  override fun detect(cards: List<Card>, rankGroups: RankGroups): HandRank? {
    val pairs = rankGroups.getRanksWithCount(2)
    if (pairs.isEmpty()) return null

    val pair = pairs.first()
    val kickers = rankGroups.topRanks().filter { it != pair }.take(3)
    return HandRank(HandType.ONE_PAIR, listOf(pair) + kickers)
  }
}
