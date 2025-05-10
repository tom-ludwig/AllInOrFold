package hwr.oop.group1.poker.cli

import hwr.oop.group1.poker.Game
import kotlinx.serialization.json.Json
import java.io.File

class GameStateManager(private val stateManager: StateManager = JsonStateManager()) {
    fun saveState(game: Game) {
        stateManager.saveState(game.toState())
    }

    fun loadState(): Game? {
        val state = stateManager.loadState() ?: return null
        
        return try {
            val game = Game()
            game.fromState(state)
            game
        } catch (e: Exception) {
            println("Error loading game state: ${e.message}")
            null
        }
    }
} 