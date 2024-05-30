import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

fun main() {
    runBlocking {
        delay(2000L)
        println("World1!")
    }
    runBlocking {
        println("start for World2!")
        delay(1000L)
        println("World2!")
    }
    runBlocking {
        delay(1000L)
        println("World3!")
    }
    println("Hello,")
}