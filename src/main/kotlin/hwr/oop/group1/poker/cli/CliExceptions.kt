package hwr.oop.group1.poker.cli

class NoGameException : RuntimeException(
    "No game was found"
)

class InvalidCommandException (command: String): RuntimeException(
    "Command $command does not exist"
)

class InvalidCommandUsageException (command: String): RuntimeException(
    "Command $command was used Incorrectly"
)