package hwr.oop.group1.poker.handEvaluation.handDetectors

import hwr.oop.group1.poker.Card
import hwr.oop.group1.poker.CardRank
import hwr.oop.group1.poker.HandType
import hwr.oop.group1.poker.handEvaluation.HandRank
import hwr.oop.group1.poker.handEvaluation.RankGroups

class StraightFlushDetector : HandDetector {
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