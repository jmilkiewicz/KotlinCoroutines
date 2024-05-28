import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume

data class User(val name: String, val age: Int)

private val executor =
    Executors.newSingleThreadScheduledExecutor {
        Thread(it, "scheduler").apply { isDaemon = true }
    }

suspend fun main() {
    println("Before")
    val userId = 1L
    val user = requestUser(userId)

    println(user)
    println("After")
}

private suspend fun requestUser(userId: Long): User {
    return suspendCancellableCoroutine { continuation ->
        fetchUser(userId, continuation)
    }
}

fun fetchUser(userId: Long, continuation: CancellableContinuation<User>) {
    executor.schedule({ continuation.resume(User("Jack", 25)) }, 1000, TimeUnit.MILLISECONDS)

}

//suspend fun requestUser(userId: Long, continuation: CancellableContinuation<User>) {
//    myDelay(1000)
//    continuation.resume(User("adam",12))
//}

