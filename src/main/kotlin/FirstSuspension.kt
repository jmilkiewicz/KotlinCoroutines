import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

suspend fun main() {
    println("Before")
    suspendCancellableCoroutine<Int> { continuation ->
        println("inside")
        continuation.resume(2)
        println("after resume")
    }
    println("After")
}