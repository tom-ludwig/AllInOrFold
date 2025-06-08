package hwr.oop.group1.poker.handEvaluation

import hwr.oop.group1.poker.Card
import hwr.oop.group1.poker.CardRank

class RankGroups(cards: List<Card>) {
  private val grouped: List<Pair<CardRank, Int>> = cards
    .groupBy { it.rank }
    .mapValues { it.value.size }
    .toList()
    .sortedWith(
      compareByDescending<Pair<CardRank, Int>> { it.second }
        .thenByDescending { it.first.value }
    )

  fun hasOfAKind(n: Int): Boolean = grouped.any { it.second == n }

  fun getRanksWithCount(n: Int): List<CardRank> =
    grouped.filter { it.second == n }.map { it.first }

  fun topRanks(): List<CardRank> = grouped.map { it.first }

  fun first(): Pair<CardRank, Int> = grouped.first()

  fun second(): Pair<CardRank, Int>? = grouped.getOrNull(1)
}
