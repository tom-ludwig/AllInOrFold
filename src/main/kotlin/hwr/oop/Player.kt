package hwr.oop

class Player (name: String, money: Int = 0){
    var name = name
        private set
    var money = money
        private set
    var hand = emptyList<Card>().toMutableList()
        private set

    fun addCard(card: Card){
        hand += card
    }

    fun addMoney(money: Int) {
        this.money += money
    }
}