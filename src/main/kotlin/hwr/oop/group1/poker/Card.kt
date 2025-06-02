package hwr.oop.group1.poker

import kotlinx.serialization.Serializable

@Serializable
data class Card(val rank: CardRank, val suit: CardSuit) {
  override fun toString(): String {
    return rank.name + " of " + suit.name
  }
}