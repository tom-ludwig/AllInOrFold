package hwr.oop.group1.poker.cli

import hwr.oop.group1.poker.Game
import kotlinx.serialization.json.Json
import java.io.File

interface StateSerializable {
    fun toState(): Map<String, Any>
    fun fromState(state: Map<String, Any>)
}

interface StateManager {
    fun saveState(state: Map<String, Any>)
    fun loadState(): Map<String, Any>?
}
// Concrete implementation: saves the state to a JSON file on disk
class JsonStateManager(private val file: File = File("poker_state.json")) : StateManager {
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    override fun saveState(state: Map<String, Any>) {
        file.writeText(json.encodeToString(StateMapSerializer, state))
    }

    override fun loadState(): Map<String, Any>? {
        if (!file.exists()) return null
        return try {
            json.decodeFromString(StateMapSerializer, file.readText()) //restore from file map
        } catch (e: Exception) {
            println("Error loading state: ${e.message}")
            null
        }
    }
}

class GameStateManager(private val stateManager: StateManager = JsonStateManager()) {
    // game → map → save
    fun saveState(game: Game) {
        stateManager.saveState(game.toState())
    }

    fun loadState(): Game? {
        val state = stateManager.loadState() ?: return null

        return try {
            val game = Game()
            game.fromState(state) // map → game
            game
        } catch (e: Exception) {
            println("Error loading game state: ${e.message}")
            null
        }
    }
}
