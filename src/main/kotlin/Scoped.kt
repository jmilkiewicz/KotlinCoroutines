import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    runBlocking { println("internal run blocking")
        launch { // same as this.launch
            delay(5000L)
            println("internal launch")
        }
    }
    launch { // same as this.launch
        delay(2000L)
        println("World!")
    }
    launch { // same as this.launch
        delay(2000L)
        println("World!")
    }
    launch { // same as this.launch
        delay(2000L)
        println("World!")
    }
    println("Hello,")
}
// Hello,
// (1 sec)
// World!
// World!
// World!