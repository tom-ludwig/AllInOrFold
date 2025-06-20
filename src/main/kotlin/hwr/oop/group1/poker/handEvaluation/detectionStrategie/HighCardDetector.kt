package hwr.oop.group1.poker.handEvaluation.detectionStrategie

import hwr.oop.group1.poker.Card
import hwr.oop.group1.poker.HandType
import hwr.oop.group1.poker.handEvaluation.HandRank
import hwr.oop.group1.poker.handEvaluation.RankGroups

class HighCardDetector : HandDetector {
    override fun detect(cards: List<Card>, rankGroups: RankGroups): HandRank? {
        val highCards = cards.sortedByDescending { it.rank.value }.map { it.rank }.take(5)
        return HandRank(HandType.HIGH_CARD, highCards)
    }
}
