package hwr.oop.group1.poker

import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat

class PotTest : AnnotationSpec() {
    private lateinit var pot: Pot
    private lateinit var alice: Player
    private lateinit var bob: Player
    private lateinit var caroline: Player
    private lateinit var players: MutableList<Player>


    @BeforeEach
    fun setUp() {
        alice = Player("Alice", 300)
        bob = Player("Bob", 200)
        caroline = Player("Caroline", 100)
        players = mutableListOf(
            alice,
            bob,
            caroline
        )
        pot = Pot()
        pot.resetTotalBet()
    }

    @Test
    fun `players can bet`() {
        val betAmount = 10
        pot.raise(alice, betAmount)

        assertThat(pot.potSize()).isEqualTo(betAmount)
        assertThat(pot.players()).containsOnly(alice)
    }

    @Test
    fun `side pot gets created after all in`() {
        pot.raise(alice, 250)
        pot.call(bob)

        val nextPot = pot.nextPot()!!

        assertThat(pot.potSize()).isEqualTo(2 * 200)
        assertThat(nextPot.potSize()).isEqualTo(50)
        assertThat(nextPot.players()).containsOnly(alice)
    }

    @Test
    fun `2 side pots get created after 2 players are all in`() {
        pot.raise(alice, 250)
        pot.call(bob)
        pot.call(caroline)

        val secondPot = pot.nextPot()!!
        val thirdPot = secondPot.nextPot()!!

        assertThat(pot.potSize()).isEqualTo(3 * 100)
        assertThat(secondPot.potSize()).isEqualTo(2 * 100)
        assertThat(secondPot.players()).containsOnly(alice, bob)
        assertThat(thirdPot.potSize()).isEqualTo(50)
        assertThat(thirdPot.players()).containsOnly(alice)
    }

    @Test
    fun `correct side pots for normal call after all in`() {
        alice = Player("Alice", 300)
        bob = Player("Bob", 200)
        caroline = Player("Caroline", 300)

        pot.raise(alice, 250)
        pot.call(bob)
        pot.call(caroline)

        val nextPot = pot.nextPot()!!

        assertThat(pot.potSize()).isEqualTo(3 * 200)
        assertThat(nextPot.potSize()).isEqualTo(2 * 50)
        assertThat(nextPot.players()).containsOnly(caroline, alice)
    }

    @Test
    fun `correct side pots for higher second all in`() {
        alice = Player("Alice", 300)
        bob = Player("Bob", 100)
        caroline = Player("Caroline", 200)

        pot.raise(alice, 250)
        pot.call(bob)
        pot.call(caroline)

        val secondPot = pot.nextPot()!!
        val thirdPot = secondPot.nextPot()!!

        assertThat(pot.potSize()).isEqualTo(3 * 100)
        assertThat(secondPot.potSize()).isEqualTo(2 * 100)
        assertThat(secondPot.players()).containsOnly(alice, caroline)
        assertThat(thirdPot.potSize()).isEqualTo(50)
        assertThat(thirdPot.players()).containsOnly(alice)
    }

    @Test
    fun `correct side pots for multiple stages`() {
        alice = Player("A", 400)
        bob = Player("B", 100)
        caroline = Player("C", 200)

        val playerD = Player("D", 300)
        players += playerD

        pot.raise(alice, 250)
        pot.call(bob)
        pot.call(caroline)
        pot.call(players[3])
        pot.nextStage()
        pot.raise(alice, 100)
        pot.call(playerD)

        val secondPot = pot.nextPot()!!
        val thirdPot = secondPot.nextPot()!!
        val fourthPot = thirdPot.nextPot()!!

        assertThat(pot.potSize()).isEqualTo(4 * 100)
        assertThat(secondPot.potSize()).isEqualTo(3 * 100)
        assertThat(secondPot.players()).containsOnly(alice, caroline, playerD)
        assertThat(thirdPot.potSize()).isEqualTo(2 * 100)
        assertThat(thirdPot.players()).containsOnly(alice, playerD)
        assertThat(fourthPot.potSize()).isEqualTo(50)
        assertThat(fourthPot.players()).containsOnly(alice)
    }

    @Test
    fun `correct side pots for re-raise`() {
        alice = Player("Alice", 300)
        bob = Player("Bob", 100)
        caroline = Player("Caroline", 400)

        val playerD = Player("D", 200)
        players += playerD

        pot.raise(alice, 250)
        pot.call(bob)
        pot.raise(caroline, 350)
        pot.call(playerD)
        pot.call(alice)

        val secondPot = pot.nextPot()!!
        val thirdPot = secondPot.nextPot()!!
        val fourthPot = thirdPot.nextPot()!!

        assertThat(pot.potSize()).isEqualTo(4 * 100)
        assertThat(secondPot.potSize()).isEqualTo(3 * 100)
        assertThat(secondPot.players()).containsOnly(alice, caroline, playerD)
        assertThat(thirdPot.potSize()).isEqualTo(2 * 100)
        assertThat(thirdPot.players()).containsOnly(alice, caroline)
        assertThat(fourthPot.potSize()).isEqualTo(50)
        assertThat(fourthPot.players()).containsOnly(caroline)
    }
}