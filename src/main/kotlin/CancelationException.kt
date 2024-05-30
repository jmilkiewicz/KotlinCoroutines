import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import kotlin.coroutines.cancellation.CancellationException

class MyNonPropagatingException : CancellationException()
suspend fun main(): Unit = coroutineScope {
    launch { // 1
        launch { // 2
            delay(1500)
            println("Will not be printed as this is child and CancellationException was thrown in parent ")
        }
        delay(1000)
        println("print before CancellationException")
        throw MyNonPropagatingException() // 3
    }
    launch { // 4
        delay(2000)
        println("Will be printed as it a sibbling of coroutine that threw exception")
    }
}