package hwr.oop.group1.poker.cli

import hwr.oop.group1.poker.persistence.GamePersistence


interface CliCommand {
    fun matches(args: List<String>): Boolean
    fun handle(persistence: GamePersistence, args: List<String>)
}