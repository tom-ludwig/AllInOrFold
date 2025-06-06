package hwr.oop.group1.poker

// A Ranking of a hand (5-card combination)
data class HandRank(
  val type: HandType,
  val cardRank: List<CardRank>, // ordered for tie-breaker
) : Comparable<HandRank> {
  override fun compareTo(other: HandRank): Int {
    val cmp = type.strength.compareTo(other.type.strength)
    return if (cmp != 0) cmp
    else
      cardRank.zip(other.cardRank).map { it.first.value - it.second.value }
        .firstOrNull {
          it != 0
        }
        ?: 0
  }
}