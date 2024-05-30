import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main(): Unit = runBlocking {
    launch {
        println("job1 started")
        delay(1000)
        println("job1 completed")
    }

    launch {
        println("job2 stated")
        delay(1000)
        println("job2 completed")
    }

    println("waiting for children to finish")
    coroutineContext.job.children.forEach { it.join() }
    println("Children finished")
}