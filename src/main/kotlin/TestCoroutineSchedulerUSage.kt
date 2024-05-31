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
    val scheduler = TestCoroutineScheduler()
    val testDispatcher = StandardTestDispatcher(scheduler)
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
    println("[${scheduler.currentTime}] Before")
    scheduler.advanceTimeBy(500)

    println("[${scheduler.currentTime}] advanced")
    scheduler.runCurrent()
    println("must see things at 500 now ")

    scheduler.advanceTimeBy(500)
    println("[${scheduler.currentTime}] advanced")
    scheduler.advanceTimeBy(900)
    println("[${scheduler.currentTime}] advanced")
    scheduler.advanceTimeBy(100)
    println("[${scheduler.currentTime}] advanced")
    println("[${scheduler.currentTime}] After")
}