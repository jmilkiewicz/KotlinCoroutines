import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


fun main(): Unit = runBlocking {

    launch { fetchUserDetails() }
    launch {
        println("sibling to fetchUserDetails start" )
        delay(2000)
        println("sibling to fetchUserDetails end" )
    }

}


suspend fun fetchUserDetails(): String? = try {
    coroutineScope {
        val userData = async { f1() }
        val userPreferences = async { f2() }
        userData.await() + " _ " + userPreferences.await()
    }
} catch (e: Throwable) {
    println("Error in fetchUserDetails: $e")
    null
}

suspend fun f1():String {
    println("starting f1")
    delay(1000)
    println("not printed as f1 shall be cancelled")
    return "f1"
}

suspend fun f2():String {
    println("starting f2")
    delay(100)
    throw RuntimeException("can not f2")
}
