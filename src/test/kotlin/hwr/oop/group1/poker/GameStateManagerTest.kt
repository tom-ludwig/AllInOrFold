package hwr.oop.group1.poker

import hwr.oop.group1.poker.cli.GameStateManager
import hwr.oop.group1.poker.cli.JsonStateManager
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.File

class GameStateManagerTest {

    @Test
    fun `game state is saved and loaded correctly`() {
        val tempFile = File.createTempFile("game", ".json") // temporary file
        val stateManager = GameStateManager(JsonStateManager(tempFile)) // use own state manager from file

        val originalGame = Game()
        val player1 = Player(name = "Frodo", money = 150)
        val player2 = Player(name = "Sam", money = 200)

        originalGame.addPlayer(player1)
        originalGame.addPlayer(player2)

        originalGame.newRound() // start new round (set dealer, big blind...)

        val expectedMoney1 = player1.money // value after (-) blind
        val expectedMoney2 = player2.money

        stateManager.saveState(originalGame) // save game state

        val loadedGame = stateManager.loadState() // load game state

        requireNotNull(loadedGame) // make sure that no error has occurred

        // player count and names correct?
        assertThat(loadedGame.players.size).isEqualTo(2)
        assertThat(loadedGame.players[0].name).isEqualTo("Frodo")
        assertThat(loadedGame.players[1].name).isEqualTo("Sam")

        // player money correct?
        assertThat(loadedGame.players[0].money).isEqualTo(expectedMoney1)
        assertThat(loadedGame.players[1].money).isEqualTo(expectedMoney2)

        // pot correct?
        assertThat(loadedGame.round!!.pot).isEqualTo(originalGame.round!!.pot)

        tempFile.delete() // delete temporary file
    }
}

