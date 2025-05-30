package hwr.oop.group1.poker.persistence

import hwr.oop.group1.poker.Game

interface GamePersistence {
    fun saveGame(game: Game)
    fun loadGame(): Game?
}