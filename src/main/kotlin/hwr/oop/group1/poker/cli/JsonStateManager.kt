package hwr.oop.group1.poker.cli

import kotlinx.serialization.json.Json
import java.io.File

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
            json.decodeFromString(StateMapSerializer, file.readText())
        } catch (e: Exception) {
            println("Error loading state: ${e.message}")
            null
        }
    }
}