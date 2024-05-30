import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.cancellation.CancellationException

suspend fun main(): Unit = coroutineScope {
    val job = launch {
        repeat(10) { i ->
            delay(200)
            println("Printing $i")
        }
    }
    job.invokeOnCompletion {
        if (it is CancellationException) {
            println("Cancelled with $it")
        }
        println("Finally")
    }
    delay(700)

    println("Cancelled successfully")
}