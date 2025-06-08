package hwr.oop.group1.poker.handEvaluation.detectionStrategie

import hwr.oop.group1.poker.Card
import hwr.oop.group1.poker.handEvaluation.HandRank
import hwr.oop.group1.poker.handEvaluation.RankGroups

interface HandDetectionStrategy {
  fun detect(cards: List<Card>, rankGroups: RankGroups): HandRank?
}
