# AllInOrFold

[![Lint and Format](https://github.com/tom-ludwig/AllInOrFold/actions/workflows/check_lint_and_format.yaml/badge.svg)](https://github.com/tom-ludwig/AllInOrFold/actions/workflows/check_lint_and_format.yaml)
[![Unit Tests and Build](https://github.com/tom-ludwig/AllInOrFold/actions/workflows/test-runner.yaml/badge.svg)](https://github.com/tom-ludwig/AllInOrFold/actions/workflows/test-runner.yaml)

A poker game made in the OOP class at HWR in 2025

## Abstract

## Conceptional UMLs
### Sequence diagram
~~~mermaid
sequenceDiagram
participant game
participant round
actor players
game ->> round: startRound()
loop for 4 phases
round ->> round: doPhaseSpecificActions()
loop until 3 Checks
round ->> players: askAction()
players ->> round: reportAction()
end
round ->> round: rankHands()
end
round ->> game: reportBackWinningsAndLoses()
~~~
### Class diagram
~~~mermaid
sequenceDiagram
participant game
participant round
actor players
game ->> round: startRound()
loop for 4 phases
round ->> round: doPhaseSpecificActions()
loop until 3 Checks
round ->> players: askAction()
players ->> round: reportAction()
end
round ->> round: rankHands()
end
round ->> game: reportBackWinningsAndLoses()
~~~


## Feature List

| Number | Feature | Tests |
|--------|---------|-------|
| 1      | /       | /     |

## Additional Dependencies

| Number | Dependency Name | Dependency Description | Why is it necessary? |
|--------|-----------------|------------------------|----------------------|
| 1      | /               | /                      | /                    |