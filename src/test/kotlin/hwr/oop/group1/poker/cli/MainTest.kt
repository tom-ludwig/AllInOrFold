package hwr.oop.group1.poker.cli

import hwr.oop.group1.poker.main
import hwr.oop.group1.poker.persistence.FileSystemGamePersistence
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.extensions.system.captureStandardOut
import org.assertj.core.api.Assertions.assertThat
import java.io.File

class MainTest : AnnotationSpec() {
    @Before
    @AfterEach
    fun cleanup() {
        val file = File("game.json")
        if (file.exists()) {
            file.delete()
        }
    }

    @Test
    fun `game file gets created`() {
        val args = listOf(
            arrayOf("poker", "new"),
            arrayOf("poker", "addPlayer", "Alice"),
            arrayOf("poker", "addPlayer", "Bob"),
            arrayOf("poker", "start"),
        )

        args.forEach {
            main(it)
        }

        val persistence = FileSystemGamePersistence(File("game.json"))
        val game = persistence.loadGame()

        assertThat(game).isNotNull
        assertThat(game.getPlayers().map { it.name }).containsExactly(
            "Alice",
            "Bob"
        )
        assertThat(game.round).isNotNull
    }

    @Test
    fun `main outputs errors`() {
        val args = listOf(
            arrayOf("poker", "start"),
        )

        val output = captureStandardOut {
            args.forEach {
                main(it)
            }
        }

        assertThat(output).contains("Game file does not exist.")
    }
}