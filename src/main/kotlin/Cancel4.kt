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
    var childJob: Job? = null
    val job = launch {
        launch {
            println("startuje A")
            try {
                repeat(5){
                    delay(100)
                    println("A")
                }
            } finally {
                println("A finished")
            }
        }
        childJob = launch {
            try {
                println("startuje B")
                delay(2000)
                println("B")
            } catch (e: CancellationException) {
                println("B cancelled")
            }
        }
        launch {
            println("startuje C")
            delay(3000)
            println("C")
        }.invokeOnCompletion {
            println("C finished")
        }
    }
    delay(400)
    job.cancel()
    job.join()
    println("Cancelled successfully")
    println(childJob?.isCancelled)
}