package hwr.oop.group1.poker.cli

import hwr.oop.group1.poker.persistence.GamePersistence

class Cli (private val gamePersistence: GamePersistence) {
    private val commands = listOf(
        NewGameCommand(),
        AddPlayerCommand(),
        GameStartCommand(),
        PlayerActionCommand(),
        ShowRoundCommand(),
        ShowPlayerCommand(),
    )

    fun handle(args: List<String>) {
        if(args.size < 2 || args.first() != "poker") throw InvalidCommandException(args.first())

        val commandArgs = args.takeLast(args.size - 1)
        val command = commands.find { it.matches(commandArgs) } ?: throw InvalidCommandException(commandArgs.first())
        command.handle(gamePersistence, commandArgs)
    }
}