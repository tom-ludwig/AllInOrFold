package hwr.oop.group1.poker.persistence

class GameFileDoesNotExistException : RuntimeException(
    "Game file does not exist."
)

class GameDoesNotExistException(id: Int): RuntimeException(
    "Game with id $id does not exist."
)