import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler

fun main() {
    val testDispatcher = StandardTestDispatcher()
    CoroutineScope(testDispatcher).launch {
        println("Some work 1")
        delay(1000)
        println("Some work 2")
        delay(1000)
        println("Coroutine done")
    }
    CoroutineScope(testDispatcher).launch {
        delay(500)
        println("Different work")
    }
    println("[${testDispatcher.scheduler.currentTime}] Before")
    testDispatcher.scheduler.advanceUntilIdle()
    println("[${testDispatcher.scheduler.currentTime}] After")
}