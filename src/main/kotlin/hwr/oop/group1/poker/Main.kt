package hwr.oop.group1.poker

import hwr.oop.group1.poker.cli.PokerCli
import hwr.oop.group1.poker.cli.PokerCliException
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    try {
        PokerCli().start(args)
    } catch (e: PokerCliException) {
        System.err.println("Error: ${e.message}")
        exitProcess(1)
    }
} 