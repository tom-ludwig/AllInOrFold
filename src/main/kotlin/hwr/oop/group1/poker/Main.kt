package hwr.oop.group1.poker

import hwr.oop.group1.poker.cli.Cli
import hwr.oop.group1.poker.persistence.JSONGamePersistence
import java.io.File

fun main(args: Array<String>) {
    val jsonGamePersistence = JSONGamePersistence(File("game.json"))
    val cli = Cli(jsonGamePersistence)
    try {
        cli.handle(args.toList())
    }
    catch(e: Exception) {
        println(e.message)
    }
}