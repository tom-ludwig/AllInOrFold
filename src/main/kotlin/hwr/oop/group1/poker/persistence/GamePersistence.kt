package hwr.oop.group1.poker.persistence

import hwr.oop.group1.poker.Game

interface GameSaver {
    fun saveGame(game: Game, id: Int = getNextGameId()): Int
    fun getNextGameId(): Int
}

interface GameLoader {
    fun loadGame(id: Int = 0): Game
}