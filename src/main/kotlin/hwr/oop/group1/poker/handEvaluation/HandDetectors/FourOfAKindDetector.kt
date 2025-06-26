package hwr.oop.group1.poker.handEvaluation.HandDetectors

import hwr.oop.group1.poker.Card
import hwr.oop.group1.poker.HandType
import hwr.oop.group1.poker.handEvaluation.HandRank
import hwr.oop.group1.poker.handEvaluation.RankGroups

class FourOfAKindDetector : HandDetector {
    override fun detect(cards: List<Card>, rankGroups: RankGroups): HandRank? {
        if (!rankGroups.hasOfAKind(4)) return null
        val quads = rankGroups.getRanksWithCount(4).first()
        val kicker = rankGroups.topRanks().first { it != quads }
        return HandRank(HandType.FOUR_OF_A_KIND, listOf(quads, kicker))
    }
}
