import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

suspend fun foo2() {
    println("foo start")
    delay(1000)
    println("will be printed although callee is already cancelled vut it is called within NonCancellable")
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
            withContext(NonCancellable) {
                launch {
                    println("Children executed")
                }
                delay(1000L)
                println("Cleanup done")
                foo2()
            }
        }
    }
    delay(100)
    job.cancelAndJoin()
    println("Done")
}