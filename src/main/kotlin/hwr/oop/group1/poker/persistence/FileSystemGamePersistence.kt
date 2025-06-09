package hwr.oop.group1.poker.persistence

import hwr.oop.group1.poker.Game
import kotlinx.serialization.json.Json
import java.io.File

class FileSystemGamePersistence(private val file: File) : GameLoader,
  GameSaver {

  override fun saveGame(game: Game) {
    file.writeText(Json.encodeToString(game))
  }

  override fun loadGame(): Game {
    if (!file.exists()) throw GameFileDoesNotExist()
    return try {
      Json.decodeFromString<Game>(file.readText())
    } catch (e: Exception) {
      throw IllegalStateException("Error loading state: ${e.message}", e)
    }
  }
}