package hwr.oop.group1.poker

import hwr.oop.group1.poker.cli.StateSerializable

class Card(
    val rank: CardRank,
    val suit: CardSuit
) : StateSerializable {
    override fun toState(): Map<String, Any> {
        return mapOf(
            "rank" to rank.name,
            "suit" to suit.name
        )
    }

    override fun fromState(state: Map<String, Any>) {
        // Cards are immutable, so we don't need to implement this
    }

    override fun toString(): String {
        return "$rank of $suit"
    }
}