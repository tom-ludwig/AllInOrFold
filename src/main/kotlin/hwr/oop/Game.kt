package hwr.oop

class Game {
    private var deck = ArrayList<Card>()
    private var round: Round = Round()
    private var players = ArrayList<Player>()
    private var dealer: Int = 0

    fun addPlayer(player: Player) {
        players += player
    }

    fun players(): List<Player> {
        return players
    }

    fun resetDeck() {
        for(suit in CardSuit.entries){
            for (rank in CardRank.entries){
                deck += Card(rank, suit)
            }
        }
        shuffleDeck()
    }

    fun deck(): List<Card> {
        return deck
    }

    fun drawCard(): Card {
        return deck.removeAt(0)
    }

    fun shuffleDeck() {
        deck.shuffle()
    }

    fun nextDealer() {
        dealer = (dealer + 1) % players.size
    }

    fun dealer(): Int {
        return dealer
    }

    fun newRound() {
        nextDealer()
        round = Round()
    }
}