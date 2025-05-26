package hwr.oop.group1.poker

// A Ranking of a hand (5-card combination)
data class HandRank(
    val type: HandType,
    val ranks: List<CardRank> // ordered for tie-breaker
) : Comparable<HandRank> {
    override fun compareTo(other: HandRank): Int {
        val cmp = type.strength.compareTo(other.type.strength)
        return if (cmp != 0) cmp else ranks.zip(other.ranks)
            .map { it.first.value - it.second.value }
            .firstOrNull { it != 0 } ?: 0
    }
}

fun <T> List<T>.combinations(k: Int): List<List<T>> {
    fun combine(start: Int, curr: List<T>): List<List<T>> {
        if (curr.size == k) return listOf(curr)
        if (start >= this.size) return emptyList()
        return combine(start + 1, curr + this[start]) + combine(start + 1, curr)
    }
    return combine(0, emptyList())
}