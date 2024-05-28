import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume

suspend fun main() {
    println("Before")
    suspendCancellableCoroutine<Unit> { continuation ->
        resumeAfter(continuation, Unit,1000)
    }
    println("After")
}

private val executor =
    Executors.newSingleThreadScheduledExecutor {
        Thread(it, "scheduler").apply { isDaemon = true }
    }

fun <T> resumeAfter(continuation: CancellableContinuation<T>, t:T, millis: Long) {
    executor.schedule({ continuation.resume(t) }, millis, TimeUnit.MILLISECONDS)
}
