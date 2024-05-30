import kotlinx.coroutines.*
import kotlin.coroutines.cancellation.CancellationException


suspend fun main(): Unit = coroutineScope {

}

suspend fun updateThisUser() = withContext(Dispatchers.IO) {
    val user = readUser() // blocking
    yield()
    val settings = readUserSettings(user) // blocking

    try {
        updateUserInDatabase(user, settings) // suspending
    } catch (e: CancellationException) {
        withContext(NonCancellable) {
            revertUnfinishedTransactions() // suspending
        }
        throw e
    }
}

suspend fun updateThisUser2() {
    val user = withContext(Dispatchers.IO) { readUser() }
    val settings = withContext(Dispatchers.IO) { readUserSettings(user) }

    try {
        updateUserInDatabase(user, settings) // suspending
    } catch (e: CancellationException) {
        withContext(NonCancellable) {
            revertUnfinishedTransactions() // suspending
        }
        throw e
    }
}


fun updateUserInDatabase(user: String, settings: String): Unit {
    TODO("Not yet implemented")
}

suspend fun revertUnfinishedTransactions() {

}

fun readUserSettings(id: String): String {

    return "settings"
}

fun readUser(): String {
    return "user"
}
