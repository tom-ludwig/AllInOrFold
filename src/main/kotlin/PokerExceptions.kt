package hwr.oop.group1.poker

class NotEnoughMoneyException (
    player: Player,
    amount: Int
): RuntimeException(
    "player $player wants to raise $amount but only has ${player.money}"
)

class CanNotCheckException (
    player: Player
): RuntimeException(
    "player $player can not check"
)

class NotEnoughToRaiseException (
    player: Player,
    amount: Int
): RuntimeException(
    "player $player can not raise, because $amount is not enough"
)

class HandIsCompleteException: RuntimeException(
    "The Hand is already complete, to play again start a new round."
)

class RoundStartedException : RuntimeException(
    "The Round has already started"
)

