package hwr.oop

class Player (private var name: String, private var money: Int = 0){
    private var hand = ArrayList<Card>()

    fun name(): String{
        return name
    }

    fun addCard(card: Card){
        hand += card
    }

    fun hand(): List<Card> {
        return hand
    }

    fun addMoney(money: Int) {
        this.money += money
    }

    fun money(): Int {
        return money
    }
}