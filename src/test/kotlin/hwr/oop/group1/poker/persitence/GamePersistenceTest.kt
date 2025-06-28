package hwr.oop.group1.poker.persitence

import hwr.oop.group1.poker.Action
import hwr.oop.group1.poker.Game
import hwr.oop.group1.poker.Player
import hwr.oop.group1.poker.persistence.FileSystemGamePersistence
import hwr.oop.group1.poker.persistence.GameDoesNotExistException
import hwr.oop.group1.poker.persistence.GameFileDoesNotExistException
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

        val gameId = adapter.saveGame(game)

        val content = file.readText()
        assertThat(gameId).isEqualTo(0)
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
    fun `loadGame should restore the game with correct id from file`() {
        val firstGame = Game()
        val player1 = Player("Alice", 1000)

        val secondGame = Game()
        val player2 = Player("Bob", 1000)

        firstGame.addPlayer(player1)
        secondGame.addPlayer(player2)
        val firstGameId = adapter.saveGame(firstGame)
        val secondGameId = adapter.saveGame(secondGame)

        val loadedFirstGame = adapter.loadGame(0)
        val loadedSecondGame = adapter.loadGame(1)

        assertThat(firstGameId).isEqualTo(0)
        assertThat(loadedFirstGame).isNotNull()
        assertThat(loadedFirstGame).usingRecursiveComparison().isEqualTo(firstGame)

        assertThat(secondGameId).isEqualTo(1)
        assertThat(loadedSecondGame).isNotNull()
        assertThat(loadedSecondGame).usingRecursiveComparison().isEqualTo(secondGame)
    }

    @Test
    fun `loadGame should throw exception if id does not exist`() {
        val firstGame = Game()
        val player1 = Player("Alice", 1000)

        val secondGame = Game()
        val player2 = Player("Bob", 1000)

        firstGame.addPlayer(player1)
        secondGame.addPlayer(player2)
        adapter.saveGame(firstGame)
        adapter.saveGame(secondGame)

        assertThatThrownBy {
            adapter.loadGame(3)
        }.isInstanceOf(GameDoesNotExistException::class.java)
            .hasMessageContaining("Game with id 3 does not exist")
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
    fun `loadGame should throw exception if file does not exist`() {
        if (file.exists()) {
            file.delete()
        }

        assertThat(file.exists()).isFalse()

        assertThatThrownBy {
            adapter.loadGame()
        }.isInstanceOf(GameFileDoesNotExistException::class.java)
    }

    @Test
    fun `loadGame should throw exception if JSON is invalid`() {
        file.writeText("not-a-valid-json")

        assertThatThrownBy {
            adapter.loadGame()
        }.hasMessageContaining("Error loading state")
    }
}