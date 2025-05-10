package hwr.oop.group1.poker.cli

interface StateSerializable {
    fun toState(): Map<String, Any>
    fun fromState(state: Map<String, Any>)
}