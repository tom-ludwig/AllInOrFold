package hwr.oop.group1.poker.handEvaluation.HandDetectors

import hwr.oop.group1.poker.CardRank

fun findStraightRanks(ranks: Set<CardRank>): List<CardRank> {
    val uniqueValues = ranks.map { it.value }.toSet().toMutableSet()

    // Handle Ace as 1 for A-2-3-4-5
    if (CardRank.ACE in ranks) uniqueValues.add(1)

    val sortedValues = uniqueValues.sortedDescending()
    for (i in 0..sortedValues.size - 5) {
        val window = sortedValues.subList(i, i + 5)
        if (window.zipWithNext().all { it.first - it.second == 1 }) {
            return window.map { v ->
                CardRank.values()
                    .first { it.value == (if (v == 1) 14 else v) }
            }
        }
    }
    return emptyList()
}