package hwr.oop

class Round {
    var deck = Deck()
        private set
    var communityCards = emptyList<Card>().toMutableList()
        private set
    var stage = 0
        private set
    var pot = 0
        private set

    init {
        deck.resetDeck()
    }

    fun addCommunityCard(card: Card) {
        this.communityCards += card
    }

    fun nextStage() {
        stage++
        if(stage == 1){
            for(i in 1..3){
                addCommunityCard(deck.draw())
            }
        }else if(stage <= 3){
            addCommunityCard(deck.draw())
        }
    }

    fun addToPot(money: Int) {
        pot += money
    }
}