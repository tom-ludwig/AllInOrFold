package hwr.oop.group1.poker.handEvaluation.detectionStrategie

import hwr.oop.group1.poker.Card
import hwr.oop.group1.poker.HandType
import hwr.oop.group1.poker.handEvaluation.HandRank
import hwr.oop.group1.poker.handEvaluation.RankGroups

class StraightDetector : HandDetector {
    override fun detect(cards: List<Card>, rankGroups: RankGroups): HandRank? {
        val ranks = cards.map { it.rank }.toSet()
        val straight = findStraightRanks(ranks)
        return if (straight.isNotEmpty()) {
            HandRank(HandType.STRAIGHT, straight)
        } else {
            null
        }
    }
}