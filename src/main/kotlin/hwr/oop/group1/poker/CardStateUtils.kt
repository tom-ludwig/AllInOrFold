package hwr.oop.group1.poker

fun Card.Companion.fromState(state: Map<String, Any>): Card {
    val rank = CardRank.valueOf(state["rank"] as String)
    val suit = CardSuit.valueOf(state["suit"] as String)
    return Card(rank, suit)
}

fun Card.toState(): Map<String, Any> {
    return mapOf(
        "rank" to rank.name,
        "suit" to suit.name
    )
}