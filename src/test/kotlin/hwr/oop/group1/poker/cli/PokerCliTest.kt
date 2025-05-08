package hwr.oop.group1.poker.cli

import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream

class PokerCliTest : AnnotationSpec() {
    private val outputStream = ByteArrayOutputStream()
    private val cli = PokerCli()

    init {
        System.setOut(PrintStream(outputStream))
        File("poker_state.json").delete()
    }

    private fun getOutput(): String {
        return outputStream.toString().trim()
    }

    private fun clearOutput() {
        outputStream.reset()
    }

    @Test
    fun `new game command clears players`() {
        clearOutput()
        cli.start(arrayOf("--new-game"))
        assertThat(getOutput()).isEqualTo("New game created. Add players with --add-player <name> <chips>")
    }

    @Test
    fun `add player command adds player with chips`() {
        clearOutput()
        cli.start(arrayOf("--add-player", "Test Player", "1000"))
        assertThat(getOutput()).isEqualTo("Added player Test Player with 1000 chips")
    }

    @Test
    fun `add player requires name and chips`() {
        clearOutput()
        assertThatThrownBy { cli.start(arrayOf("--add-player")) }
            .isInstanceOf(PokerCliException::class.java)
            .hasMessage("Player name and chips required")

        assertThatThrownBy { cli.start(arrayOf("--add-player", "Test Player")) }
            .isInstanceOf(PokerCliException::class.java)
            .hasMessage("Player name and chips required")
    }

    @Test
    fun `add player validates chips amount`() {
        clearOutput()
        assertThatThrownBy { cli.start(arrayOf("--add-player", "Test Player", "invalid")) }
            .isInstanceOf(PokerCliException::class.java)
            .hasMessage("Invalid chips amount")

        assertThatThrownBy { cli.start(arrayOf("--add-player", "Test Player", "0")) }
            .isInstanceOf(PokerCliException::class.java)
            .hasMessage("Invalid chips amount")

        assertThatThrownBy { cli.start(arrayOf("--add-player", "Test Player", "-100")) }
            .isInstanceOf(PokerCliException::class.java)
            .hasMessage("Invalid chips amount")
    }

    @Test
    fun `set small blind command sets amount`() {
        clearOutput()
        cli.start(arrayOf("--set-big-blind", "10"))
        clearOutput()
        cli.start(arrayOf("--set-small-blind", "5"))
        assertThat(getOutput()).isEqualTo("Small blind set to 5")
    }

    @Test
    fun `set small blind requires amount`() {
        clearOutput()
        assertThatThrownBy { cli.start(arrayOf("--set-small-blind")) }
            .isInstanceOf(PokerCliException::class.java)
            .hasMessage("Amount required for small blind")
    }

    @Test
    fun `set small blind validates amount`() {
        clearOutput()
        assertThatThrownBy { cli.start(arrayOf("--set-small-blind", "invalid")) }
            .isInstanceOf(PokerCliException::class.java)
            .hasMessage("Invalid small blind amount")

        assertThatThrownBy { cli.start(arrayOf("--set-small-blind", "0")) }
            .isInstanceOf(PokerCliException::class.java)
            .hasMessage("Invalid small blind amount")

        assertThatThrownBy { cli.start(arrayOf("--set-small-blind", "-5")) }
            .isInstanceOf(PokerCliException::class.java)
            .hasMessage("Invalid small blind amount")
    }

    @Test
    fun `set big blind command sets amount`() {
        clearOutput()
        cli.start(arrayOf("--set-big-blind", "10"))
        assertThat(getOutput()).isEqualTo("Big blind set to 10")
    }

    @Test
    fun `set big blind requires amount`() {
        clearOutput()
        assertThatThrownBy { cli.start(arrayOf("--set-big-blind")) }
            .isInstanceOf(PokerCliException::class.java)
            .hasMessage("Amount required for big blind")
    }

    @Test
    fun `set big blind validates amount`() {
        clearOutput()
        assertThatThrownBy { cli.start(arrayOf("--set-big-blind", "invalid")) }
            .isInstanceOf(PokerCliException::class.java)
            .hasMessage("Invalid big blind amount")

        assertThatThrownBy { cli.start(arrayOf("--set-big-blind", "0")) }
            .isInstanceOf(PokerCliException::class.java)
            .hasMessage("Invalid big blind amount")

        assertThatThrownBy { cli.start(arrayOf("--set-big-blind", "-10")) }
            .isInstanceOf(PokerCliException::class.java)
            .hasMessage("Invalid big blind amount")
    }

    @Test
    fun `set starting money command sets amount`() {
        clearOutput()
        cli.start(arrayOf("--set-big-blind", "10"))
        clearOutput()
        cli.start(arrayOf("--set-starting-money", "1000"))
        assertThat(getOutput()).isEqualTo("Starting money set to 1000")
    }

    @Test
    fun `set starting money requires amount`() {
        clearOutput()
        assertThatThrownBy { cli.start(arrayOf("--set-starting-money")) }
            .isInstanceOf(PokerCliException::class.java)
            .hasMessage("Amount required for starting money")
    }

    @Test
    fun `set starting money validates amount`() {
        clearOutput()
        assertThatThrownBy { cli.start(arrayOf("--set-starting-money", "invalid")) }
            .isInstanceOf(PokerCliException::class.java)
            .hasMessage("Invalid starting money amount")

        assertThatThrownBy { cli.start(arrayOf("--set-starting-money", "0")) }
            .isInstanceOf(PokerCliException::class.java)
            .hasMessage("Invalid starting money amount")

        assertThatThrownBy { cli.start(arrayOf("--set-starting-money", "-1000")) }
            .isInstanceOf(PokerCliException::class.java)
            .hasMessage("Invalid starting money amount")
    }

    @Test
    fun `start game command starts game`() {
        clearOutput()
        cli.start(arrayOf("--new-game"))
        clearOutput()
        cli.start(arrayOf("--add-player", "Player 1", "1000"))
        clearOutput()
        cli.start(arrayOf("--add-player", "Player 2", "1000"))
        clearOutput()
        cli.start(arrayOf("--start-game"))
        assertThat(getOutput()).isEqualTo("Game started!")
    }

    @Test
    fun `start game requires at least 2 players`() {
        clearOutput()
        cli.start(arrayOf("--new-game"))
        clearOutput()
        assertThatThrownBy { cli.start(arrayOf("--start-game")) }
            .isInstanceOf(PokerCliException::class.java)
            .hasMessage("Need at least 2 players to start a game")

        cli.start(arrayOf("--add-player", "Player 1", "1000"))
        clearOutput()
        assertThatThrownBy { cli.start(arrayOf("--start-game")) }
            .isInstanceOf(PokerCliException::class.java)
            .hasMessage("Need at least 2 players to start a game")
    }

    @Test
    fun `show community cards should display current community cards`() {
        clearOutput()
        cli.start(arrayOf("--new-game"))
        clearOutput()
        cli.start(arrayOf("--add-player", "Player 1", "1000"))
        clearOutput()
        cli.start(arrayOf("--add-player", "Player 2", "1000"))
        clearOutput()
        cli.start(arrayOf("--start-game"))
        clearOutput()
        cli.start(arrayOf("--show-community-cards"))
        assertThat(getOutput()).contains("Community Cards:")
    }

    @Test
    fun `show hand should display current player's hand`() {
        clearOutput()
        cli.start(arrayOf("--new-game"))
        clearOutput()
        cli.start(arrayOf("--add-player", "Test Player", "1000"))
        clearOutput()
        cli.start(arrayOf("--add-player", "Player 2", "1000"))
        clearOutput()
        cli.start(arrayOf("--start-game"))
        clearOutput()
        cli.start(arrayOf("--show-hand"))
        assertThat(getOutput()).contains("Test Player's Hand:")
    }

    @Test
    fun `start hand command starts new hand and posts blinds`() {
        clearOutput()
        cli.start(arrayOf("--new-game"))
        clearOutput()
        cli.start(arrayOf("--add-player", "Player 1", "1000"))
        clearOutput()
        cli.start(arrayOf("--add-player", "Player 2", "1000"))
        clearOutput()
        cli.start(arrayOf("--set-small-blind", "5"))
        clearOutput()
        cli.start(arrayOf("--set-big-blind", "10"))
        clearOutput()
        cli.start(arrayOf("--start-game"))
        clearOutput()
        cli.start(arrayOf("--deal-new-hand"))
        
        val output = getOutput()
        assertThat(output).contains("New hand dealt!")
        assertThat(output).contains("Dealer: Player 1")
        assertThat(output).contains("Small Blind: Player 2")
        assertThat(output).contains("Big Blind: Player 1")
        assertThat(output).contains("Current bet: 10")
        assertThat(output).contains("Pot: 15")
    }

    @Test
    fun `show dealer command displays dealer and blind positions`() {
        clearOutput()
        cli.start(arrayOf("--new-game"))
        clearOutput()
        cli.start(arrayOf("--add-player", "Player 1", "1000"))
        clearOutput()
        cli.start(arrayOf("--add-player", "Player 2", "1000"))
        clearOutput()
        cli.start(arrayOf("--start-game"))
        clearOutput()
        cli.start(arrayOf("--show-dealer"))
        
        val output = getOutput()
        assertThat(output).contains("Dealer: Player 1")
        assertThat(output).contains("Small Blind: Player 2")
        assertThat(output).contains("Big Blind: Player 1")
    }

    @Test
    fun `show pot command displays current pot size`() {
        clearOutput()
        cli.start(arrayOf("--new-game"))
        clearOutput()
        cli.start(arrayOf("--add-player", "Player 1", "1000"))
        clearOutput()
        cli.start(arrayOf("--add-player", "Player 2", "1000"))
        clearOutput()
        cli.start(arrayOf("--start-game"))
        clearOutput()
        cli.start(arrayOf("--deal-new-hand"))
        clearOutput()
        cli.start(arrayOf("--show-pot"))
        
        assertThat(getOutput()).isEqualTo("Current pot: 15")
    }

    @Test
    fun `show current bet command displays current bet amount`() {
        clearOutput()
        cli.start(arrayOf("--new-game"))
        clearOutput()
        cli.start(arrayOf("--add-player", "Player 1", "1000"))
        clearOutput()
        cli.start(arrayOf("--add-player", "Player 2", "1000"))
        clearOutput()
        cli.start(arrayOf("--start-game"))
        clearOutput()
        cli.start(arrayOf("--deal-new-hand"))
        clearOutput()
        cli.start(arrayOf("--show-current-bet"))
        
        assertThat(getOutput()).isEqualTo("Current bet: 10")
    }

    @Test
    fun `call command handles calls and checks correctly`() {
        clearOutput()
        cli.start(arrayOf("--new-game"))
        clearOutput()
        cli.start(arrayOf("--add-player", "Player 1", "1000"))
        clearOutput()
        cli.start(arrayOf("--add-player", "Player 2", "1000"))
        clearOutput()
        cli.start(arrayOf("--set-big-blind", "10"))
        clearOutput()
        cli.start(arrayOf("--set-small-blind", "5"))
        clearOutput()
        cli.start(arrayOf("--start-game"))
        clearOutput()
        cli.start(arrayOf("--deal-new-hand"))
        clearOutput()
        cli.start(arrayOf("--call"))
        assertThat(getOutput()).contains("calls")
    }

    @Test
    fun `raise command handles valid raises`() {
        clearOutput()
        cli.start(arrayOf("--new-game"))
        clearOutput()
        cli.start(arrayOf("--add-player", "Player 1", "1000"))
        clearOutput()
        cli.start(arrayOf("--add-player", "Player 2", "1000"))
        clearOutput()
        cli.start(arrayOf("--set-big-blind", "10"))
        clearOutput()
        cli.start(arrayOf("--set-small-blind", "5"))
        clearOutput()
        cli.start(arrayOf("--start-game"))
        clearOutput()
        cli.start(arrayOf("--deal-new-hand"))
        clearOutput()
        cli.start(arrayOf("--raise", "20"))
        assertThat(getOutput()).contains("raises to 20")
    }

    @Test
    fun `raise command validates raise amount`() {
        clearOutput()
        cli.start(arrayOf("--new-game"))
        clearOutput()
        cli.start(arrayOf("--add-player", "Player 1", "1000"))
        clearOutput()
        cli.start(arrayOf("--add-player", "Player 2", "1000"))
        clearOutput()
        cli.start(arrayOf("--set-big-blind", "10"))
        clearOutput()
        cli.start(arrayOf("--set-small-blind", "5"))
        clearOutput()
        cli.start(arrayOf("--start-game"))
        clearOutput()
        cli.start(arrayOf("--deal-new-hand"))
        clearOutput()
        assertThatThrownBy { cli.start(arrayOf("--raise", "5")) }
            .isInstanceOf(PokerCliException::class.java)
            .hasMessage("Raise amount must be greater than current bet")
    }
} 