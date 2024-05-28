import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

suspend fun b() {
    val a = "ABC"
    suspendCancellableCoroutine<Unit> { continuation ->
        continuation.resume(Unit)
    }
    println(a)
}

suspend fun main() {
    val list = listOf(1, 2, 3)
    val text = "Some text"
    println(text)
    delay(1000)
    b()
    println(list)
}
