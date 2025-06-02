package hwr.oop.group1.poker

// A Ranking of a hand (5-card combination)
data class HandRank(
  val type: HandType,
  val ranks: List<CardRank>, // ordered for tie-breaker
) : Comparable<HandRank> {
  override fun compareTo(other: HandRank): Int {
    val cmp = type.strength.compareTo(other.type.strength)
    return if (cmp != 0) cmp
    else
      ranks.zip(other.ranks).map { it.first.value - it.second.value }
        .firstOrNull {
          it != 0
        }
        ?: 0
  }
}

fun List<Card>.combinations(combinationSize: Int): List<List<Card>> {
  fun combine(
    startIndex: Int,
    currentCombination: List<Card>,
  ): List<List<Card>> {
    if (currentCombination.size == combinationSize) return listOf(
      currentCombination
    )
    if (startIndex >= this.size) return emptyList()
    return combine(
      startIndex + 1,
      currentCombination + this[startIndex]
    ) + combine(
      startIndex + 1,
      currentCombination
    )
  }
  return combine(0, emptyList())
}

