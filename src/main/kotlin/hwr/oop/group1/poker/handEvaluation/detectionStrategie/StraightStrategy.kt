package hwr.oop.group1.poker.handEvaluation.detectionStrategie

import hwr.oop.group1.poker.Card
import hwr.oop.group1.poker.HandType
import hwr.oop.group1.poker.handEvaluation.HandRank
import hwr.oop.group1.poker.handEvaluation.RankGroups

class StraightStrategy : HandDetectionStrategy {
  override fun detect(cards: List<Card>, rankGroups: RankGroups): HandRank? {
    val ranks = cards.map { it.rank }.toSet()
    val straight = findStraightRanks(ranks)
    return HandRank(HandType.STRAIGHT, straight)
  }
}

