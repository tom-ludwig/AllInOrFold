package hwr.oop.group1.poker.cli

import hwr.oop.group1.poker.*
import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat
import java.io.File

class GameStateManagerTest : AnnotationSpec() {
    private fun createTestGame(): Game {
        val game = Game()
        val player = Player("Test Player", 1000)
        player.updateHand(listOf(Card(CardRank.ACE, CardSuit.HEARTS)))
        game.addPlayer(player)
        return game
    }

    private fun createTestGameWithMultiplePlayers(): Game {
        val game = Game()
        val player1 = Player("Player 1", 1000)
        val player2 = Player("Player 2", 2000)
        player1.updateHand(listOf(Card(CardRank.ACE, CardSuit.HEARTS)))
        player2.updateHand(listOf(Card(CardRank.KING, CardSuit.SPADES)))
        game.addPlayer(player1)
        game.addPlayer(player2)
        return game
    }

    @Test
    fun `saveState should create a state file`() {
        val game = createTestGame()
        val stateManager = GameStateManager()
        stateManager.saveState(game)
        assertThat(File("poker_state.json")).exists()
    }
    
    @Test
    fun `loadState should return null for non-existent state file`() {
        File("poker_state.json").delete()
        val stateManager = GameStateManager()
        assertThat(stateManager.loadState()).isNull()
    }
    
    @Test
    fun `saveState and loadState should preserve game state`() {
        val game = createTestGame()
        val stateManager = GameStateManager()
        stateManager.saveState(game)
        
        val loadedGame = stateManager.loadState()
        assertThat(loadedGame).isNotNull
        assertThat(loadedGame?.players).hasSize(1)
        assertThat(loadedGame?.players?.first()?.name).isEqualTo("Test Player")
    }

    @Test
    fun `saveState and loadState should preserve multiple players`() {
        val game = createTestGameWithMultiplePlayers()
        val stateManager = GameStateManager()
        stateManager.saveState(game)
        
        val loadedGame = stateManager.loadState()
        assertThat(loadedGame?.players).hasSize(2)
        assertThat(loadedGame?.players?.map { it.name }).containsExactly("Player 1", "Player 2")
        assertThat(loadedGame?.players?.map { it.money }).containsExactly(1000, 2000)
    }

    @Test
    fun `saveState and loadState should preserve round state`() {
        val game = createTestGame()
        
        game.round.setPot(500)
        game.round.nextStage() // Stage 1 - should reveal 3 cards
        
        val stateManager = GameStateManager()
        stateManager.saveState(game)
        
        val loadedGame = stateManager.loadState()
        assertThat(loadedGame?.round?.getRevealedCommunityCards()).hasSize(3)
        assertThat(loadedGame?.round?.pot).isEqualTo(500)
        assertThat(loadedGame?.round?.stage).isEqualTo(1)
    }

    @Test
    fun `saveState and loadState should preserve dealer position`() {
        val game = createTestGameWithMultiplePlayers()
        game.updateDealer(1)
        
        val stateManager = GameStateManager()
        stateManager.saveState(game)
        
        val loadedGame = stateManager.loadState()
        assertThat(loadedGame?.dealer).isEqualTo(1)
    }

    @Test
    fun `saveState and loadState should preserve player hands`() {
        val game = createTestGame()
        val hand = listOf(
            Card(CardRank.ACE, CardSuit.HEARTS),
            Card(CardRank.KING, CardSuit.SPADES)
        )
        game.players.first().updateHand(hand)
        
        val stateManager = GameStateManager()
        stateManager.saveState(game)
        
        val loadedGame = stateManager.loadState()
        val loadedHand = loadedGame?.players?.first()?.hand
        assertThat(loadedHand).hasSize(2)
        assertThat(loadedHand?.map { it.rank }).containsExactly(CardRank.ACE, CardRank.KING)
        assertThat(loadedHand?.map { it.suit }).containsExactly(CardSuit.HEARTS, CardSuit.SPADES)
    }

    @Test
    fun `saveState should overwrite existing state file`() {
        val game1 = createTestGame()
        val game2 = createTestGameWithMultiplePlayers()
        
        val stateManager = GameStateManager()
        stateManager.saveState(game1)
        stateManager.saveState(game2)
        
        val loadedGame = stateManager.loadState()
        assertThat(loadedGame?.players).hasSize(2)
        assertThat(loadedGame?.players?.first()?.name).isEqualTo("Player 1")
    }
} 