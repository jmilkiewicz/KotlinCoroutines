import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import kotlin.coroutines.cancellation.CancellationException

class UserNotFoundException2 : RuntimeException("user not found")

suspend fun main() {
    try {
        updateUserData2()
    } catch (e: UserNotFoundException2) {
        println("Shall be printed")
    }
}

suspend fun updateUserData2() = coroutineScope {
    launch { updateTweets2() }
    launch { updateUser2() }
}

suspend fun updateTweets2() {
    println("start Updating tweets")
    delay(1000)
    println("shall not be printed as this coroutine shall be cancelled")
}

suspend fun updateUser2() {
    throw UserNotFoundException2()
}