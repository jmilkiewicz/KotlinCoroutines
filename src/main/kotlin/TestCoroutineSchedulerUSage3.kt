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
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.currentTime
import kotlin.random.Random
import kotlin.system.measureTimeMillis

fun main() {
    val testScope = TestScope()
    testScope.launch {
        delay(1000)
        println("Coroutine 1 done")
    }

    testScope.launch {
        delay(500)
        println("Different work")
    }

    val time = measureTimeMillis {
        println("[${testScope.currentTime}] Before")
        testScope.advanceUntilIdle()
        println("[${testScope.currentTime}] After")
    }

    println("physical time $time")

}