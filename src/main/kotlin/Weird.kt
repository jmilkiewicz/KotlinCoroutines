import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

var continuation: Continuation<Unit>? = null
suspend fun suspendAndSetContinuation() {
    suspendCancellableCoroutine<Unit> { cont ->
        continuation = cont
    }
}

suspend fun main() = coroutineScope {
    println("Before")
    launch {
        delay(100)
        continuation?.resume(Unit)
    }
    suspendAndSetContinuation()

    println("After")
}