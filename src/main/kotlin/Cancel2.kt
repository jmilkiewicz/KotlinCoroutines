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
        try {
            repeat(1_000) { i ->
                delay(200)
                println("Printing $i")
            }
        } catch (e: CancellationException) {
            println("Cancelled with $e")
            throw e
        } finally {
            println("Finally")
        }
    }
    delay(700)
    job.cancel()
    //we wait for job to finish, ie catch and finally. Better to go with job.cancelAndJoin()
    job.join()
    println("Cancelled successfully")
}