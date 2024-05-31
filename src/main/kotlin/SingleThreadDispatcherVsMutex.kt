import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.time.Duration
import kotlin.time.measureTime


fun main(): Unit {
    val iterations = 2000
    val delayTime = Duration.ofMillis(2)
    var counter = 0
    val op = { counter++ }
    val mutex: suspend CoroutineScope.() -> String = {
        val took = measureTime { withMutex(Mutex(false), iterations, delayTime, op) }
        "Counter: $counter and took $took for mutex"
    }
    println(runBlocking(block = mutex))


    counter = 0
    val withSingleThreadDispatcher: suspend CoroutineScope.() -> String = {
        val took = measureTime {
            withSingleThrededDispatcher(
                Dispatchers.Default.limitedParallelism(1),
                iterations,
                delayTime,
                op
            )
        }

        "Counter: $counter and took $took for single dispatcher"
    }
    println(runBlocking(block = withSingleThreadDispatcher))


    counter = 0
    val withoutAnySynch: suspend CoroutineScope.() -> String = {
        val took = measureTime {
            withoutSynch(
                iterations,
                delayTime,
                op
            )
        }
        "Counter: $counter and took $took without synch"
    }

    println(runBlocking(block = withoutAnySynch))

}

suspend fun withMutex(mutex: Mutex, iterations: Int, delayTime: Duration, function: () -> Int) =
    withContext(Dispatchers.IO) {
        repeat(iterations) {
            launch {
                mutex.withLock {
                    function.invoke()
                    delay(delayTime.toMillis())
                }
            }
        }

    }


suspend fun withoutSynch(iterations: Int, delayTime: Duration, op: () -> Int) = withContext(Dispatchers.IO) {
    repeat(iterations) {
        launch {
            op.invoke()
            delay(delayTime.toMillis())
        }
    }

}

suspend fun withSingleThrededDispatcher(
    limitedParallelism: CoroutineDispatcher,
    iterations: Int,
    delayTime: Duration,
    op: () -> Int
) = withContext(Dispatchers.IO) {
    repeat(iterations) {
        launch(limitedParallelism) {
            op.invoke()
            delay(delayTime.toMillis())
        }

    }
}




