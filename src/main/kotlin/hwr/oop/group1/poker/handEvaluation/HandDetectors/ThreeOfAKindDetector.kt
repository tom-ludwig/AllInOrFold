package hwr.oop.group1.poker.handEvaluation.HandDetectors

import hwr.oop.group1.poker.Card
import hwr.oop.group1.poker.HandType
import hwr.oop.group1.poker.handEvaluation.HandRank
import hwr.oop.group1.poker.handEvaluation.RankGroups

class ThreeOfAKindDetector : HandDetector {
    override fun detect(cards: List<Card>, rankGroups: RankGroups): HandRank? {
        val threes = rankGroups.getRanksWithCount(3)
        if (threes.isEmpty()) return null

        val triple = threes.first()
        val kickers = rankGroups.topRanks().filter { it != triple }.take(2)
        return HandRank(HandType.THREE_OF_A_KIND, listOf(triple) + kickers)
    }
}
