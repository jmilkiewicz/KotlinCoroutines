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
        job.complete()
        println("is job completed? ${job}")
    }

    launch(job) {
        delay(2000)
        println("It will be printed as job is not completed")
    }

    job.join()
    println("is job completed?  ${job}")
    launch(job) {
        println("Will not be printed")
    }
    println("Done")
}