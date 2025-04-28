package hwr.oop.group1.poker

class Round {
    var deck = Deck()
        private set
    var communityCards = emptyList<Card>().toMutableList()
        private set
    private var revealedCommunityCardCount = 0
    var stage = 0
        private set
    var pot = 0
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
}