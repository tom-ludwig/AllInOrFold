package hwr.oop.group1.poker.persitence

import hwr.oop.group1.poker.Action
import hwr.oop.group1.poker.Game
import hwr.oop.group1.poker.Player
import hwr.oop.group1.poker.persistence.FileSystemGamePersistence
import hwr.oop.group1.poker.persistence.GameFileDoesNotExist
import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import java.io.File

class GamePersistenceTest : AnnotationSpec() {
  private lateinit var file: File
  private lateinit var adapter: FileSystemGamePersistence

  @BeforeEach
  fun setup() {
    file = File("test_game.json")
    adapter = FileSystemGamePersistence(file)
  }

  @AfterEach
  fun cleanup() {
    if (file.exists()) {
      file.delete()
    }
  }

  @Test
  fun `saveGame should write game JSON to file`() {
    val game = Game()
    val player1 = Player("Max", 1000)
    val player2 = Player("Bob", 1000)

    game.addPlayer(player1)
    game.addPlayer(player2)

    adapter.saveGame(game)

    val content = file.readText()
    assertThat(content).contains("Max")
    assertThat(content).contains("Bob")
    assertThat(content).contains("1000")
  }

  @Test
  fun `loadGame should restore the game from file`() {
    val expectedGame = Game()
    val player1 = Player("Max", 1000)

    expectedGame.addPlayer(player1)
    adapter.saveGame(expectedGame)

    val loadedGame = adapter.loadGame()

    assertThat(loadedGame).isNotNull()
    assertThat(loadedGame).usingRecursiveComparison().isEqualTo(expectedGame)
  }

  @Test
  fun `loaded Game should not perform init`() {
    val expectedGame = Game()
    val player1 = Player("Max", 1000)
    val player2 = Player("Ben", 1000)
    val player3 = Player("Alice", 1000)

    expectedGame.addPlayer(player1)
    expectedGame.addPlayer(player2)
    expectedGame.addPlayer(player3)
    expectedGame.newRound()
    expectedGame.round!!.doAction(Action.CALL)

    adapter.saveGame(expectedGame)

    val loadedGame = adapter.loadGame()

    assertThat(loadedGame).isNotNull()
    assertThat(loadedGame).usingRecursiveComparison().isEqualTo(expectedGame)
  }

  @Test
  fun `loadGame should return null if file does not exist`() {
    if (file.exists()) {
      file.delete()
    }

    assertThat(file.exists()).isFalse()

    assertThatThrownBy {
      adapter.loadGame()
    }.isInstanceOf(GameFileDoesNotExist::class.java)
  }

  @Test
  fun `loadGame should return null if JSON is invalid`() {
    file.writeText("not-a-valid-json")

    assertThatThrownBy {
      adapter.loadGame()
    }.hasMessageContaining("Error loading state")
  }
}