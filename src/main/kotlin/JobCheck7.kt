import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val job = Job()
    launch(job) {
        println("start intenal task")
        repeat(5) { num ->
            delay(200)
            println("Rep$num")
        }
    }

    job.complete()
    job.join()
    //when job is completed it looks like no new children jobs can be created
    launch(job) {
        println("AND ???")
    }

    println("is job completed?  ${job}")
    launch(job) {
        println("Will not be printed")
    }
    println("Done")
}