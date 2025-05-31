package hwr.oop.group1.poker.persistence

import hwr.oop.group1.poker.Game
import kotlinx.serialization.json.Json
import java.io.File

class JSONGamePersistence(private val file: File) : GamePersistence {
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    override fun saveGame(game: Game) {
        file.writeText(json.encodeToString(game))
    }

    override fun loadGame(): Game? {
        if (!file.exists()) return null
        return try {
            json.decodeFromString<Game>(file.readText())
        } catch (e: Exception) {
            println("Error loading state: ${e.message}")
            null
        }
    }
}