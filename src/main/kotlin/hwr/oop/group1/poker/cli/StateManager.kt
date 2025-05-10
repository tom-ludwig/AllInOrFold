package hwr.oop.group1.poker.cli

interface StateManager {
    fun saveState(state: Map<String, Any>)
    fun loadState(): Map<String, Any>?
}