import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.random.Random

suspend fun main() = coroutineScope {
    repeat(1000) {
        launch(Dispatchers.IO) { // or launch(Dispatchers.Default) {
// To make it busy
            Thread.sleep(200)
            val threadName = Thread.currentThread().name
            println("Running on thread: $threadName")
        }
    }
}