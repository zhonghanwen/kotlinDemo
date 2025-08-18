

class BigBen {

    companion object Bonger {
        fun getBongs(nTimes: Int){
            repeat(nTimes){ println("BONG!") }
        }
    }
}

fun main() {
    BigBen.getBongs(5)
}