# AllInOrFold

[![Unit Tests and Build](https://github.com/tom-ludwig/AllInOrFold/actions/workflows/test-runner.yaml/badge.svg)](https://github.com/tom-ludwig/AllInOrFold/actions/workflows/test-runner.yaml)



## Abstract

This project contains the core logic for a simple Texas Hold’em poker game that runs in the terminal.

It’s written in Kotlin and was built to showcase some key object-oriented programming principles, along with a few behavioral design patterns—most notably, the strategy pattern.

The code is also very well tested, with both code coverage and mutation coverage consistently above 95%.
## Feature List
### Game Logic

| Number | implemented? | Feature          | tested? |
|--------|--------------|------------------|---------|
| 1      | &check;      | create game      | &check; |
| 2      | &check;      | add players      | &check; |
| 3      | &check;      | remove players   | &check; |
| 4      | &check;      | start game       | &check; |
| 5      | &check;      | betting logic    | &check; |
| 6      | &check;      | state output     | &check; |
| 7      | &check;      | hand evaluations | &check; |
| 8      | &check;      | side pots        | &check; |


### Persistence

| Number | implemented? | Feature             | tested? |
|--------|--------------|---------------------|---------|
| 1      | &check;      | serialize game      | &check; |
| 2      | &check;      | save game to file   | &check; |
| 3      | &check;      | load game from file | &check; |

### User Interface
`poker <gameId>` ...
- `new`
- `start`
- `addPlayer <name> <money>`
- `removePlayer <name>`
- `check`
- `call`
- `raise <amount>`
- `show round <cards | pot | bet>`
- `show player <name | cards | money | bet>`

## Development Prerequisites
Installed:

1. IDE of your choice (e.g. IntelliJ IDEA)
2. JDK of choice (JDK 21 GraalVM recommended)
3. Maven (e.g. through IntelliJ IDEA)
4. Git

## Local Development
This project uses Apache Maven as build tool.

To build from your shell (without an additional local installation of Maven), ensure that ./mvnw is executable:
~~~shell
chmod +x ./mvnw
~~~

With just installed, you can simply run this command to perform a build of this project and run all of its tests:
~~~shell
just build
~~~
## Additional Dependencies

| Number | Dependency Name            | Dependency Description                                          | Why is it necessary?                        |
|--------|----------------------------|-----------------------------------------------------------------|---------------------------------------------|
| 1      | kotlinx-serialization-json | A library for JSON serialization and deserialization in Kotlin. | Used for handling JSON data in the project. |
