package hwr.oop.group1.poker.cli

import hwr.oop.group1.poker.Game
import hwr.oop.group1.poker.persistence.GameDoesNotExistException
import hwr.oop.group1.poker.persistence.GameLoader
import hwr.oop.group1.poker.persistence.GameSaver
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.extensions.system.captureStandardOut
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy

class CliTest : AnnotationSpec() {
    private var persistence = TestPersistence()
    private var cli = Cli(gameLoader = persistence, gameSaver = persistence)

    @BeforeEach
    fun setUp() {
        persistence = TestPersistence()
        cli = Cli(gameLoader = persistence, gameSaver = persistence)
    }

    @Test
    fun `new game can be created`() {
        val args = listOf("poker", "new")

        val output = captureStandardOut {
            cli.handle(args)
        }

        assertThat(persistence.loadGame()).isNotNull
        assertThat(output).contains("Game was created with ID: 0")
    }

    @Test
    fun `players can be added to game`() {
        val args = listOf(
            listOf("poker", "new"),
            listOf("poker", "addPlayer", "Alice"),
        )

        val output = captureStandardOut {
            args.forEach {
                cli.handle(it)
            }
        }

        assertThat(output).contains("Alice", "was added")
        assertThat(
            persistence.loadGame().getPlayers().first().name
        ).isEqualTo("Alice")
    }

    @Test
    fun `players with duplicate names can not be added to game`() {
        val args = listOf(
            listOf("poker", "new"),
            listOf("poker", "addPlayer", "Alice"),
            listOf("poker", "addPlayer", "Alice"),
        )

        val output = captureStandardOut {
            args.forEach {
                cli.handle(it)
            }
        }

        assertThat(output).contains("A player with the name 'Alice' already exists in the game")
        assertThat(
            persistence.loadGame().getPlayers().first().name
        ).isEqualTo("Alice")
    }

    @Test
    fun `players can be added to game with custom money`() {
        val args = listOf(
            listOf("poker", "new"),
            listOf("poker", "addPlayer", "Alice", "100"),
        )

        val output = captureStandardOut {
            args.forEach {
                cli.handle(it)
            }
        }

        assertThat(output).contains("Alice", "was added")
        assertThat(
            persistence.loadGame().getPlayers().first().name
        ).isEqualTo("Alice")
        assertThat(
            persistence.loadGame().getPlayers().first().money()
        ).isEqualTo(
            100
        )
    }

    @Test
    fun `players can be removed from game`() {
        val args = listOf(
            listOf("poker", "new"),
            listOf("poker", "addPlayer", "Alice"),
            listOf("poker", "addPlayer", "Bob"),
            listOf("poker", "removePlayer", "Alice"),
        )

        val output = captureStandardOut {
            args.forEach {
                cli.handle(it)
            }
        }

        assertThat(output).contains("Alice", "was removed successfully")
        val game = persistence.loadGame()
        assertThat(game.getPlayers()).hasSize(1)
        assertThat(game.getPlayers().first().name).isEqualTo("Bob")
    }

    @Test
    fun `removing non-existent player shows error message`() {
        val args = listOf(
            listOf("poker", "new"),
            listOf("poker", "addPlayer", "Alice"),
            listOf("poker", "removePlayer", "Bob"),
        )

        val output = captureStandardOut {
            args.forEach {
                cli.handle(it)
            }
        }

        assertThat(output).contains("A player with the name 'Bob' was not found in the game")
        val game = persistence.loadGame()
        assertThat(game.getPlayers()).hasSize(1)
        assertThat(game.getPlayers().first().name).isEqualTo("Alice")
    }

    @Test
    fun `removing player from empty game shows error message`() {
        val args = listOf(
            listOf("poker", "new"),
            listOf("poker", "removePlayer", "Alice"),
        )

        val output = captureStandardOut {
            args.forEach {
                cli.handle(it)
            }
        }

        assertThat(output).contains("There are already no players")
    }

    @Test
    fun `command removePlayer has to include a name`() {
        cli.handle(listOf("poker", "new"))

        assertThatThrownBy {
            cli.handle(listOf("poker", "removePlayer"))
        }.hasMessageContaining("Command removePlayer was used Incorrectly")
            .isInstanceOf(InvalidCommandUsageException::class.java)
    }

    @Test
    fun `new Game can be started`() {
        val args = listOf(
            listOf("poker", "new"),
            listOf("poker", "addPlayer", "Alice"),
            listOf("poker", "addPlayer", "Bob"),
            listOf("poker", "start")
        )

        val output = captureStandardOut {
            args.forEach {
                cli.handle(it)
            }
        }
        val game = persistence.loadGame()

        assertThat(output).contains("Game has started")
        assertThat(game.round).isNotNull
    }

    @Test
    fun `players can call`() {
        val args = listOf(
            listOf("poker", "new"),
            listOf("poker", "addPlayer", "Alice"),
            listOf("poker", "addPlayer", "Bob"),
            listOf("poker", "addPlayer", "Caroline"),
            listOf("poker", "start"),
            listOf("poker", "call"),
        )

        val output = captureStandardOut {
            args.forEach {
                cli.handle(it)
            }
        }
        val game = persistence.loadGame()
        val round = game.round!!

        assertThat(round.getCurrentPlayer().name).isEqualTo("Bob")
        assertThat(round.potSize()).isEqualTo(50)
        assertThat(output).contains(
            "Player Alice has performed action call",
            "Next player is Bob"
        )
    }

    @Test
    fun `players can check`() {
        val args = listOf(
            listOf("poker", "new"),
            listOf("poker", "addPlayer", "Alice"),
            listOf("poker", "addPlayer", "Bob"),
            listOf("poker", "addPlayer", "Caroline"),
            listOf("poker", "start"),
            listOf("poker", "call"),
            listOf("poker", "call"),
            listOf("poker", "check")
        )

        val output = captureStandardOut {
            args.forEach {
                cli.handle(it)
            }
        }
        val game = persistence.loadGame()
        val round = game.round!!

        assertThat(round.potSize()).isEqualTo(60)
        assertThat(output).contains("Player Caroline has performed action check")
    }

    @Test
    fun `players can raise`() {
        val args = listOf(
            listOf("poker", "new"),
            listOf("poker", "addPlayer", "Alice"),
            listOf("poker", "addPlayer", "Bob"),
            listOf("poker", "addPlayer", "Caroline"),
            listOf("poker", "start"),
            listOf("poker", "raise", "30"),
        )

        val output = captureStandardOut {
            args.forEach {
                cli.handle(it)
            }
        }
        val game = persistence.loadGame()
        val round = game.round!!

        assertThat(round.potSize()).isEqualTo(60)
        assertThat(output).contains("Player Alice has performed action raise")
    }

    @Test
    fun `players can fold`() {
        val args = listOf(
            listOf("poker", "new"),
            listOf("poker", "addPlayer", "Alice"),
            listOf("poker", "addPlayer", "Bob"),
            listOf("poker", "addPlayer", "Caroline"),
            listOf("poker", "start"),
            listOf("poker", "fold"),
        )

        val output = captureStandardOut {
            args.forEach {
                cli.handle(it)
            }
        }
        val game = persistence.loadGame()
        val round = game.round!!

        assertThat(round.potSize()).isEqualTo(30)
        assertThat(output).contains("Player Alice has performed action fold")
    }

    @Test
    fun `stage ended announcement after stage ends`() {
        val args = listOf(
            listOf("poker", "new"),
            listOf("poker", "addPlayer", "Alice"),
            listOf("poker", "addPlayer", "Bob"),
            listOf("poker", "addPlayer", "Caroline"),
            listOf("poker", "start"),
            listOf("poker", "call"),
            listOf("poker", "call"),
            listOf("poker", "check")
        )

        val output = captureStandardOut {
            args.forEach {
                cli.handle(it)
            }
        }
        val game = persistence.loadGame()
        val communityCards = game.round!!.getRevealedCommunityCards()

        assertThat(output).contains("Stage is over")
            .contains(communityCards.map { it.toString() })
    }

    @Test
    fun `winner announcement is shown after round ends`() {
        val args = listOf(
            listOf("poker", "new"),
            listOf("poker", "addPlayer", "Alice"),
            listOf("poker", "addPlayer", "Bob"),
            listOf("poker", "addPlayer", "Caroline"),
            listOf("poker", "start"),
            listOf("poker", "fold"),
            listOf("poker", "fold"),
        )

        val output = captureStandardOut {
            args.forEach {
                cli.handle(it)
            }
        }

        assertThat(output).contains("Caroline wins 30 chips")
    }

    @Test
    fun `show player cards shows hole of current player`() {
        val args = listOf(
            listOf("poker", "new"),
            listOf("poker", "addPlayer", "Alice"),
            listOf("poker", "addPlayer", "Bob"),
            listOf("poker", "addPlayer", "Caroline"),
            listOf("poker", "start"),
            listOf("poker", "show", "player", "cards"),
        )

        val output = captureStandardOut {
            args.forEach {
                cli.handle(it)
            }
        }
        val game = persistence.loadGame()
        val round = game.round!!
        val currentPlayer = round.getCurrentPlayer()
        val holeCards = currentPlayer.hole()

        assertThat(output)
            .contains("The hole cards of Alice are")
            .contains(holeCards.map { it.toString() })
    }

    @Test
    fun `show round cards shows communityCards`() {
        val args = listOf(
            listOf("poker", "new"),
            listOf("poker", "addPlayer", "Alice"),
            listOf("poker", "addPlayer", "Bob"),
            listOf("poker", "addPlayer", "Caroline"),
            listOf("poker", "start"),
            listOf("poker", "call"),
            listOf("poker", "call"),
            listOf("poker", "check"),
            listOf("poker", "show", "round", "cards"),
        )

        val output = captureStandardOut {
            args.forEach {
                cli.handle(it)
            }
        }
        val game = persistence.loadGame()
        val round = game.round!!
        val communityCards = round.getRevealedCommunityCards()

        assertThat(output)
            .contains("The shown community cards are")
            .contains(communityCards.map { it.toString() })
    }

    @Test
    fun `show player money shows money of current Player`() {
        val args = listOf(
            listOf("poker", "new"),
            listOf("poker", "addPlayer", "Alice"),
            listOf("poker", "addPlayer", "Bob"),
            listOf("poker", "addPlayer", "Caroline"),
            listOf("poker", "start"),
            listOf("poker", "show", "player", "money"),
        )

        val output = captureStandardOut {
            args.forEach {
                cli.handle(it)
            }
        }

        val game = persistence.loadGame()
        val round = game.round!!
        val currentPlayer = round.getCurrentPlayer()
        val money = currentPlayer.money()
        assertThat(output).contains(
            "The current money of ${currentPlayer.name}",
            money.toString()
        )
    }

    @Test
    fun `show round pot shows current pot`() {
        val args = listOf(
            listOf("poker", "new"),
            listOf("poker", "addPlayer", "Alice"),
            listOf("poker", "addPlayer", "Bob"),
            listOf("poker", "addPlayer", "Caroline"),
            listOf("poker", "start"),
            listOf("poker", "show", "round", "pot"),
        )

        val output = captureStandardOut {
            args.forEach {
                cli.handle(it)
            }
        }

        val game = persistence.loadGame()
        val round = game.round!!
        val pot = round.potSize()
        assertThat(output).contains("The current pot contains", pot.toString())
    }

    @Test
    fun `show round currentPlayer shows current player`() {
        val args = listOf(
            listOf("poker", "new"),
            listOf("poker", "addPlayer", "Alice"),
            listOf("poker", "addPlayer", "Bob"),
            listOf("poker", "addPlayer", "Caroline"),
            listOf("poker", "start"),
            listOf("poker", "show", "player", "name"),
        )

        val output = captureStandardOut {
            args.forEach {
                cli.handle(it)
            }
        }

        val game = persistence.loadGame()
        val round = game.round!!
        val currentPlayer = round.getCurrentPlayer()
        assertThat(output).contains("The current player is", currentPlayer.name)
    }

    @Test
    fun `show bet shows current bet`() {
        val args = listOf(
            listOf("poker", "new"),
            listOf("poker", "addPlayer", "Alice"),
            listOf("poker", "addPlayer", "Bob"),
            listOf("poker", "addPlayer", "Caroline"),
            listOf("poker", "start"),
            listOf("poker", "raise", "50"),
            listOf("poker", "show", "round", "bet"),
        )

        val output = captureStandardOut {
            args.forEach {
                cli.handle(it)
            }
        }

        val game = persistence.loadGame()
        val round = game.round!!
        val currentBet = round.currentBet
        assertThat(output).contains("The current bet is $currentBet")
    }

    @Test
    fun `show player bet shows current bet of player`() {
        val args = listOf(
            listOf("poker", "new"),
            listOf("poker", "addPlayer", "Alice"),
            listOf("poker", "addPlayer", "Bob"),
            listOf("poker", "addPlayer", "Caroline"),
            listOf("poker", "start"),
            listOf("poker", "raise", "50"),
            listOf("poker", "show", "player", "bet"),
        )

        val output = captureStandardOut {
            args.forEach {
                cli.handle(it)
            }
        }

        val game = persistence.loadGame()
        val round = game.round!!
        val currentPlayer = round.getCurrentPlayer()
        val currentPlayerBet = currentPlayer.currentBet
        assertThat(output).contains("The current bet of ${currentPlayer.name} is $currentPlayerBet")
    }

    @Test
    fun `commands work with id`() {
        val args = listOf(
            listOf("poker", "new"),
            listOf("poker", "new"),
            listOf("poker", "1", "addPlayer", "Alice"),
            listOf("poker", "1", "addPlayer", "Bob"),
            listOf("poker", "1", "addPlayer", "Caroline"),
        )

        val output = captureStandardOut {
            args.forEach {
                cli.handle(it)
            }
        }

        val game = persistence.loadGame(1)
        assertThat(game.getPlayers()).hasSize(3)
        assertThat(output)
            .contains("Game was created with ID: 0")
            .contains("Game was created with ID: 1")
    }


    @Test
    fun `command has to start with poker`() {
        val args = listOf("start")

        assertThatThrownBy {
            cli.handle(args)
        }.hasMessageContaining("Command 'start' does not exist")
            .isInstanceOf(InvalidCommandException::class.java)
    }

    @Test
    fun `command has to exist`() {
        val args = listOf("poker", "something")

        assertThatThrownBy {
            cli.handle(args)
        }.hasMessageContaining("Command 'something' does not exist")
            .isInstanceOf(InvalidCommandException::class.java)
    }

    @Test
    fun `command addPlayer has to include a name`() {
        cli.handle(listOf("poker", "new"))

        assertThatThrownBy {
            cli.handle(listOf("poker", "addPlayer"))
        }.hasMessageContaining("Command addPlayer was used Incorrectly")
            .isInstanceOf(InvalidCommandUsageException::class.java)
    }

    @Test
    fun `exception when second argument of addPlayer is not a number`() {
        cli.handle(listOf("poker", "new"))

        assertThatThrownBy {
            cli.handle(listOf("poker", "addPlayer", "alice", "something"))
        }.hasMessageContaining("Command addPlayer was used Incorrectly")
            .isInstanceOf(InvalidCommandUsageException::class.java)
    }

    @Test
    fun `command show has to exist`() {
        cli.handle(listOf("poker", "new"))

        assertThatThrownBy {
            cli.handle(listOf("poker", "show", "player", "something"))
        }.hasMessageContaining("Command show player was used Incorrectly")
            .isInstanceOf(InvalidCommandUsageException::class.java)
    }
}

class TestPersistence : GameLoader, GameSaver {
    private var games: MutableList<Game> = mutableListOf()

    override fun saveGame(game: Game, id: Int): Int {
        if (games.size <= id) {
            games.add(game)
        } else {
            games[id] = game
        }
        return id
    }

    override fun loadGame(id: Int): Game {
        if (games.size <= id) throw GameDoesNotExistException(id)
        return games[id]
    }

    override fun getNextGameId(): Int {
        return games.size
    }
}