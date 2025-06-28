package hwr.oop.group1.poker.persistence

import hwr.oop.group1.poker.Game
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import java.io.File

class FileSystemGamePersistence(private val file: File) : GameLoader,
    GameSaver {

    override fun saveGame(game: Game, id: Int): Int {
        val gamesMap: MutableMap<Int, Game> =
            if (file.exists()) {
                Json.decodeFromString<MutableMap<Int, Game>>(file.readText())
            } else mutableMapOf()
        gamesMap[id] = game
        file.writeText(Json.encodeToString(gamesMap))
        return id
    }

    override fun getNextGameId(): Int {
        if (!file.exists()) return 0
        val json = Json.parseToJsonElement(file.readText())
        val keys = json.jsonObject.keys.map { it.toInt() }
        return keys.maxOrNull()?.plus(1) ?: 0
    }

    override fun loadGame(id: Int): Game {
        if (!file.exists()) throw GameFileDoesNotExistException()
        val gamesMap = try {
            Json.decodeFromString<MutableMap<Int, Game>>(file.readText())

        } catch (e: Exception) {
            throw IllegalStateException("Error loading state: ${e.message}", e)
        }
        return gamesMap[id] ?: throw GameDoesNotExistException(id)
    }
}