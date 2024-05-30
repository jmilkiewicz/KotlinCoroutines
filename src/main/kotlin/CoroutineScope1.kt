import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay

suspend fun main() {
    coroutineScope {
        delay(2000L)
        println("1!")
    }
    coroutineScope {
        delay(1000L)
        println("2!")
    }
    coroutineScope {
        delay(1000L)
        println("3!")
    }
    println("4")
}