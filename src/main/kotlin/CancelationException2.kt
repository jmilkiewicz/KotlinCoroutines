import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import kotlin.coroutines.cancellation.CancellationException

class UserNotFoundException : CancellationException()

suspend fun main() {
    try {
        updateUserData()
    } catch (e: UserNotFoundException) {
        println("Won't be printed as launch ate CancellationException")
    }
}

suspend fun updateUserData() = coroutineScope {
    launch { updateUser() }
    launch { updateTweets() }
}

suspend fun updateTweets() {
    println("start Updating tweets")
    delay(1000)
    println("printed as it is run from coroutine being as sibbling of coroutine that threw CancellationException")
}

suspend fun updateUser() {
    throw UserNotFoundException()
}