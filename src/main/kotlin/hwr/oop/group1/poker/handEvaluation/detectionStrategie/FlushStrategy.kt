package hwr.oop.group1.poker.handEvaluation.detectionStrategie

import hwr.oop.group1.poker.Card
import hwr.oop.group1.poker.HandType
import hwr.oop.group1.poker.handEvaluation.HandRank
import hwr.oop.group1.poker.handEvaluation.RankGroups

class FlushStrategy : HandDetector {
    override fun detect(cards: List<Card>, rankGroups: RankGroups): HandRank? {
        val flush = cards.groupBy { it.suit }
            .values.firstOrNull { it.size >= 5 }
            ?: return null

        val sortedRanks = flush.sortedByDescending { it.rank.value }.take(5).map { it.rank }
        return HandRank(HandType.FLUSH, sortedRanks)
    }
}
