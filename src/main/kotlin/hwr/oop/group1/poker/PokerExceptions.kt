package hwr.oop.group1.poker

class NotEnoughMoneyException(
    player: Player,
    amount: Int,
) : RuntimeException(
    "player ${player.name} wants to raise $amount but only has ${player.getMoney()}"
)

class CanNotCheckException(
    player: Player,
) : RuntimeException(
    "player ${player.name} can not check"
)

class NotEnoughToRaiseException(
    player: Player,
    amount: Int,
) : RuntimeException(
    "player ${player.name} can not raise, because $amount is not enough"
)

class RoundIsCompleteException : RuntimeException(
    "The Round is already complete, to play again start a new round."
)

class RoundStartedException : RuntimeException(
    "The Round has already started"
)

