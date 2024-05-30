import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main(): Unit = runBlocking {
    val job: Job = launch {
        delay(100)
        println("job1")
    }

    val job2: Job = launch {
        delay(1000)
        println("job2")
    }

    println(job.isActive)
    delay(200)
    println(job.isCompleted)
    println(job2.isCompleted)
}