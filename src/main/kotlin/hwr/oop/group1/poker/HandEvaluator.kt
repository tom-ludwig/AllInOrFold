package hwr.oop.group1.poker

object HandEvaluator {
    fun evaluateBestHandFrom(cards: List<Card>): HandRank {
        // all 5-card combos from 7 cards
        val allCombos = cards.combinations(5)
        return allCombos.maxOf { evaluateFiveCardHand(it) }
    }

    /** Evaluates a 5-card hand and returns its rank. */
    private fun evaluateFiveCardHand(cards: List<Card>): HandRank {
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

    /** Checks for a straight (five consecutive card ranks) in the given set of ranks. */
    private fun getStraightRanks(ranks: Set<CardRank>): List<CardRank>? {
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
}