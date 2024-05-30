import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield


suspend fun main(): Unit = coroutineScope {
    val job = Job()
    launch(job) {
        repeat(100) { i ->
            cpuIntensiveOperation() // We might have some
// complex operations or reading files here
            yield()
            println("Printing $i")
        }
    }
    delay(1000)
    job.cancelAndJoin()
    println("Cancelled successfully")
}

private fun cpuIntensiveOperation() {
    Thread.sleep(200)
}