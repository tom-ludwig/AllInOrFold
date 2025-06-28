# AllInOrFold

[![Lint and Format](https://github.com/tom-ludwig/AllInOrFold/actions/workflows/check_lint_and_format.yaml/badge.svg)](https://github.com/tom-ludwig/AllInOrFold/actions/workflows/check_lint_and_format.yaml)
[![Unit Tests and Build](https://github.com/tom-ludwig/AllInOrFold/actions/workflows/test-runner.yaml/badge.svg)](https://github.com/tom-ludwig/AllInOrFold/actions/workflows/test-runner.yaml)



## Abstract

This repository contains the game logic for poker. It was made for educational purposes in the object oriented programming class at HWR Berlin (summer term 2025).

⚠️ This code is for educational purposes only. Do not rely on it!


## Feature List
### Game Logic

| Number | implemented? | Feature          | Tests |
|--------|--------------|------------------|-------|
| 1      | &check;      | create game      | /     |
| 2      | &check;      | add players      | /     |
| 3      | &check;      | start game       | /     |
| 4      | &check;      | start game       | /     |
| 5      | &check;      | betting logic    | /     |
| 6      | &check;      | state output     | /     |
| 7      | &check;      | hand evaluations | /     |
| 8      | &check;      | side pots        | /     |


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
I recommend not diving into details about Maven at the beginning. Instead, you can use just to build the project. It reads the repositories justfile which maps simplified commands to corresponding sensible Maven calls.

With just installed, you can simply run this command to perform a build of this project and run all of its tests:
~~~shell
just build
~~~
## Additional Dependencies

| Number | Dependency Name | Dependency Description | Why is it necessary? |
|--------|-----------------|------------------------|----------------------|
| 1      | kotlinx-serialization-json | A library for JSON serialization and deserialization in Kotlin.  | Used for handling JSON data in the project. |
