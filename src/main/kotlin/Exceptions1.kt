import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield

fun main(): Unit = runBlocking {
    launch {
        launch {
            delay(1000)
            throw Error("Some error")
        }
        launch {
            delay(2000)
            println("Will not be printed")
        }
        launch {
            delay(500) // faster than the exception
            println("Will be printed")
        }
    }
    launch {
        delay(2000)
        println("Will not be printed")
    }

    launch {
        delay(500)
        println("Will be printed")
    }
}