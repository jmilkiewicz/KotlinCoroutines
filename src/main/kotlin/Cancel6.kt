import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.cancellation.CancellationException


suspend fun foo() {
    println("foo start")
    delay(1000)
    println("won't be printed as callee is already cancelled")
}
suspend fun main(): Unit = coroutineScope {
    val job = Job()
    launch(job) {
        try {
            println("Coroutine started")
            delay(200)
            println("Coroutine finished")
        } finally {
            println("Finally")
            launch {
                println("will not be printed as job is already cancelled")
            }
            println("Finally 2")
            foo()
            delay(1000L)
            println("Wil not be printed as it is after delay done")
        }
    }
    delay(100)
    job.cancelAndJoin()
    println("Done")
}