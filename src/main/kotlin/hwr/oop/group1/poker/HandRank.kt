package hwr.oop.group1.poker

enum class HandType(val strength: Int) {
    HIGH_CARD(1),
    ONE_PAIR(2),
    TWO_PAIR(3),
    THREE_OF_A_KIND(4),
    STRAIGHT(5),
    FLUSH(6),
    FULL_HOUSE(7),
    FOUR_OF_A_KIND(8),
    STRAIGHT_FLUSH(9),
    ROYAL_FLUSH(10)
}

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

fun evaluateHand(cards: List<Card>): HandRank {
    // all 5-card combos from 7 cards
    val allCombos = cards.combinations(5)
    return allCombos.maxOf { evaluateFiveCardHand(it) }
}

fun evaluateFiveCardHand(cards: List<Card>): HandRank {
    val sorted = cards.sortedByDescending { it.rank.value }
    val rankGroups = cards.groupBy { it.rank }
        .mapValues { it.value.size }
        .toList()
        .sortedWith(
        compareByDescending<Pair<CardRank, Int>> { it.second }
            .thenByDescending { it.first.value }
    )

    val isFlush = cards.groupBy { it.suit }.values.any { it.size == 5 }
    val straightRanks = getStraightRanks(cards.map { it.rank }.toSet())

    return when {
        isFlush && straightRanks != null -> {
            if (straightRanks == listOf(CardRank.ACE, CardRank.KING, CardRank.QUEEN, CardRank.JACK, CardRank.TEN)) {
                HandRank(HandType.ROYAL_FLUSH, straightRanks)
            } else {
                HandRank(HandType.STRAIGHT_FLUSH, straightRanks)
            }
        }
        rankGroups[0].second == 4 -> {
            HandRank(HandType.FOUR_OF_A_KIND, listOf(rankGroups[0].first, rankGroups[1].first))
        }
        rankGroups[0].second == 3 && rankGroups[1].second >= 2 -> {
            HandRank(HandType.FULL_HOUSE, listOf(rankGroups[0].first, rankGroups[1].first))
        }
        isFlush -> {
            HandRank(HandType.FLUSH, sorted.map { it.rank })
        }
        straightRanks != null -> {
            HandRank(HandType.STRAIGHT, straightRanks)
        }
        rankGroups[0].second == 3 -> {
            HandRank(HandType.THREE_OF_A_KIND, listOf(rankGroups[0].first) + rankGroups.drop(1).map { it.first })
        }
        rankGroups[0].second == 2 && rankGroups[1].second == 2 -> {
            HandRank(HandType.TWO_PAIR, listOf(rankGroups[0].first, rankGroups[1].first, rankGroups[2].first))
        }
        rankGroups[0].second == 2 -> {
            HandRank(HandType.ONE_PAIR, listOf(rankGroups[0].first) + rankGroups.drop(1).map { it.first })
        }
        else -> {
            HandRank(HandType.HIGH_CARD, sorted.map { it.rank })
        }
    }
}

fun getStraightRanks(ranks: Set<CardRank>): List<CardRank>? {
    val sorted = ranks.map { it.value }.toSet().toMutableSet()

    // Handle Ace as 1 for A-2-3-4-5
    if (CardRank.ACE in ranks) sorted.add(1)

    val values = sorted.sortedDescending()
    for (i in 0..values.size - 5) {
        val window = values.subList(i, i + 5)
        if (window.zipWithNext().all { it.first - it.second == 1 }) {
            return window.map { v -> CardRank.entries.first { it.value == if (v == 1) 14 else v } }
        }
    }
    return null
}

fun <T> List<T>.combinations(k: Int): List<List<T>> {
    fun combine(start: Int, curr: List<T>): List<List<T>> {
        if (curr.size == k) return listOf(curr)
        if (start >= this.size) return emptyList()
        return combine(start + 1, curr + this[start]) + combine(start + 1, curr)
    }
    return combine(0, emptyList())
}