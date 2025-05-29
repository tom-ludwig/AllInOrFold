package hwr.oop.group1.poker.cli

import hwr.oop.group1.poker.Game
import hwr.oop.group1.poker.persistence.GamePersistence
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.extensions.system.captureStandardOut
import org.assertj.core.api.Assertions.assertThat

class CliTest : AnnotationSpec() {
    private var persistence = TestPersistence()
    private var cli = Cli(persistence)

    @BeforeEach
    fun setUp() {
        persistence = TestPersistence()
        cli = Cli(persistence)
    }

    @Test
    fun `new game can be created`() {
        val args = listOf("game", "new")

        val output = captureStandardOut {
            cli.handle(args)
        }

        assertThat(persistence.loadGame()).isNotNull
        assertThat(output).contains("Game was created")
    }

    @Test
    fun `players can be added to game`() {
        val args = listOf(
            listOf("game", "new"),
            listOf("game", "addPlayer", "Alice"),
        )

        val output = captureStandardOut {
            args.forEach {
                cli.handle(it)
            }
        }

        assertThat(output).contains("Alice", "was added")
        assertThat(persistence.loadGame()!!.players.first().name).isEqualTo("Alice")
    }

    @Test
    fun `new Game can be started`() {
        val args = listOf(
            listOf("game", "new"),
            listOf("game", "addPlayer", "Alice"),
            listOf("game", "addPlayer", "Bob"),
            listOf("game", "start")
        )

        val output = captureStandardOut {
            args.forEach {
                cli.handle(it)
            }
        }
        val game = persistence.loadGame()!!

        assertThat(output).contains("Game has started")
        assertThat(game.round).isNotNull
    }

    @Test
    fun `players can call`() {
        val args = listOf(
            listOf("game", "new"),
            listOf("game", "addPlayer", "Alice"),
            listOf("game", "addPlayer", "Bob"),
            listOf("game", "addPlayer", "Caroline"),
            listOf("game", "start"),
            listOf("game", "call"),
        )

        val output = captureStandardOut {
            args.forEach {
                cli.handle(it)
            }
        }
        val game = persistence.loadGame()!!
        val round = game.round!!

        assertThat(round.currentPlayer.name).isEqualTo("Bob")
        assertThat(round.pot).isEqualTo(50)
        assertThat(output).contains("Player Alice has performed action CALL", "Next player is Bob")
    }

    @Test
    fun `players can check`() {
        val args = listOf(
            listOf("game", "new"),
            listOf("game", "addPlayer", "Alice"),
            listOf("game", "addPlayer", "Bob"),
            listOf("game", "addPlayer", "Caroline"),
            listOf("game", "start"),
            listOf("game", "call"),
            listOf("game", "call"),
            listOf("game", "check")
        )

        val output = captureStandardOut {
            args.forEach {
                cli.handle(it)
            }
        }
        val game = persistence.loadGame()!!
        val round = game.round!!

        assertThat(round.pot).isEqualTo(60)
        assertThat(output).contains("Player Caroline has performed action CHECK")
    }

    @Test
    fun `players can raise`() {
        val args = listOf(
            listOf("game", "new"),
            listOf("game", "addPlayer", "Alice"),
            listOf("game", "addPlayer", "Bob"),
            listOf("game", "addPlayer", "Caroline"),
            listOf("game", "start"),
            listOf("game", "raise", "30"),
        )

        val output = captureStandardOut {
            args.forEach {
                cli.handle(it)
            }
        }
        val game = persistence.loadGame()!!
        val round = game.round!!

        assertThat(round.pot).isEqualTo(60)
        assertThat(output).contains("Player Alice has performed action RAISE")
    }

    @Test
    fun `players can fold`() {
        val args = listOf(
            listOf("game", "new"),
            listOf("game", "addPlayer", "Alice"),
            listOf("game", "addPlayer", "Bob"),
            listOf("game", "addPlayer", "Caroline"),
            listOf("game", "start"),
            listOf("game", "fold"),
        )

        val output = captureStandardOut {
            args.forEach {
                cli.handle(it)
            }
        }
        val game = persistence.loadGame()!!
        val round = game.round!!

        assertThat(round.pot).isEqualTo(30)
        assertThat(output).contains("Player Alice has performed action FOLD")
    }
}

class TestPersistence : GamePersistence {
    private var game : Game? = null

    override fun saveGame(game: Game) {
        this.game = game
    }

    override fun loadGame(): Game? {
        return game
    }
}