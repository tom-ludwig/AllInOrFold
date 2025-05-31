# AllInOrFold

[![Lint and Format](https://github.com/tom-ludwig/AllInOrFold/actions/workflows/check_lint_and_format.yaml/badge.svg)](https://github.com/tom-ludwig/AllInOrFold/actions/workflows/check_lint_and_format.yaml)
[![Unit Tests and Build](https://github.com/tom-ludwig/AllInOrFold/actions/workflows/test-runner.yaml/badge.svg)](https://github.com/tom-ludwig/AllInOrFold/actions/workflows/test-runner.yaml)

A poker game made in the OOP class at HWR in 2025

## Abstract

Stand: 2025-05-26

~~~mermaid
---
title: Texas Hold'em
---
classDiagram
    Game *-- Round
    Game "1" *-- "2..10" Player
    Round o-- Deck
    Deck "1" o-- "52" Card
    class Game{
        +/- round
        +/- players
		+/- smallBlindAmount
		+/- bigBlindAmount
        +/- dealerPosition
		+ setSmallBlind()
		+ setBigBlind()
		+ addPlayer(player)
		+ newRound()

    }
	class Exceptions{
		CanNotCheckException
		RoundIsCompleteException
		NotEnoughMoneyException
		NotEnoughToRaiseException
	}
    class Round{
		+ players
		+ smallBlindAmount
		+ bigBlindAmount
		+ dealerPosition
		- communityCards
		+ stage
		- smallBlindPositon
		- bigBlindPosition
		+- pot
		+- currentBet
		- currentPlayerPosition
		- lastRaisePosition
		- isBettingRoundComplete
		+- is RoundComplete
		+- lastWinnerAnnouncement
		+ doAction(action, amount)
		- nextPlayer()
		- check()
		- call()
		- raise(amount)
		- fold()
		+ getRevealedCommunityCards()
		- nextStage()
        - addToPot(money)
		- placeBet(player, amount)
		- payBlinds()
		- checkBettingStageComplete()
		- handleBettingRoundComplete()
		- checkIfRoundIsComplete()
		- determineWinner()
    }
    class Player {
        + name
        + money //stack?
        +- hole
		+- hasFolded
		+- currentBet
        + addCard(card)
        + addMoney(money)
		+ betMoney(money)
		+ resetCurrentBet()
		+ fold()
		+ resetFold()
		+ evaluatePlayerHand(communityCards)
    }
    class Deck{
    - cards
	+ draw()
    }
    class Card {
    +rank
    +suit
    }
	class Action{
	<<enumeration>>
	FOLD
	CHECK
	CALL
	RAISE
	}
	class CardRank{
	<<enumeration>>
	TWO
	THREE
	...
	KING
	ACE
	}
	class CardSuit{
	<<enumeration>>
	CLUBS
	DIAMONDS
	HEARTS
	SPADES
	}
~~~


## Feature List
### Game Logic

| Number | implemented? | Feature     | Tests |
|--------|--------------|-------------|-------|
| 1      | &check;      | create game | /     |
| 2      | &check;      | add players | /     |
| 3      | &check;      | start game  | /     |
|        |              |             | /     |
|        |              |             | /     |
|        |              |             | /     |


### Persistence

| Number | implemented? | Feature             | Tests |
|--------|--------------|---------------------|-------|
| 1      | &check;      | serialize game      | /     |
| 2      | &check;      | save game to file   | /     |
| 3      | &check;      | load game from file | /     |

### User Interface
- `new`
- `start`
- `addPlayer <name> <money>`
- `check`
- `call`
- `raise <amount>`
- `show round <cards | pot | bet>`
- `show player <name | cards | money | bet>`

## Additional Dependencies

| Number | Dependency Name | Dependency Description | Why is it necessary? |
|--------|-----------------|------------------------|----------------------|
| 1      | /               | /                      | /                    |