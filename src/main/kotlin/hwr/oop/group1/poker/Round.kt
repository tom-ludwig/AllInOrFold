package hwr.oop.group1.poker
import hwr.oop.group1.poker.cli.StateSerializable

class Round : StateSerializable {
    var deck = Deck()
        private set
    var communityCards = mutableListOf<Card>()
        private set
    private var revealedCommunityCardCount = 0
    var stage = 0
        private set
    var pot = 0
        private set
    var currentBet = 0
        private set

    init {
        for(i in 1..5) {
            addCommunityCard(deck.draw())
        }
    }
    fun addCommunityCard(card: Card) {
        this.communityCards += card
    }

    fun nextStage() {
        stage++
        if(stage == 1){
            revealedCommunityCardCount = 3
        }else if(stage <= 3){
            revealedCommunityCardCount++
        }
    }

    fun getRevealedCommunityCards(): List<Card> {
        return this.communityCards.take(revealedCommunityCardCount)
    }

    fun addToPot(money: Int) {
        pot += money
    }

    fun setCurrentBet(bet: Int) {
        currentBet = bet
    }

    override fun toState(): Map<String, Any> {
        return mapOf(
            "communityCards" to communityCards.map { it.toState() },
            "revealedCommunityCardCount" to revealedCommunityCardCount,
            "stage" to stage,
            "pot" to pot
        )
    }
    override fun fromState(state: Map<String, Any>) {
        communityCards = (state["communityCards"] as List<Map<String, Any>>).map {
            Card(CardRank.valueOf(it["rank"] as String), CardSuit.valueOf(it["suit"] as String))
        }.toMutableList()
        revealedCommunityCardCount = (state["revealedCommunityCardCount"] as Number).toInt()
        stage = (state["stage"] as Number).toInt()
        pot = (state["pot"] as Number).toInt()
    }
}