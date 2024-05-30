import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val job = Job()
    launch(job) {
        repeat(5) { num ->
            delay(200)
            println("Rep$num")
        }
    }
    launch {
        delay(500)
        job.completeExceptionally(Error("Some error"))
        launch(job) {
            println("Can i run new after completeExceptionally ??? ")
            delay(2000)
            println("If you see it, than yes")
        }    }
    job.join()
    launch(job) {
        println("Will not be printed")
    }
    println("Done")
}