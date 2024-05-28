import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume

suspend fun main() {
    println("Before")
    myDelay(1000)
    println("After")
}

private val executor =
    Executors.newSingleThreadScheduledExecutor {
        Thread(it, "scheduler").apply { isDaemon = true }
    }

suspend fun myDelay(millis: Long) {
    suspendCancellableCoroutine<Unit> { continuation ->
        executor.schedule(
            { continuation.resume(Unit) },
            millis,
            TimeUnit.MILLISECONDS
        )
    }
}
