package hwr.oop.group1.poker.persistence

import hwr.oop.group1.poker.Game

interface GameSaver {
  fun saveGame(game: Game)
}

interface GameLoader {
  fun loadGame(): Game
}