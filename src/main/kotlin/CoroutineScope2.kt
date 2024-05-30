import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay

suspend fun main() {
    coroutineScope {
        val foo = async {
            delay(2000L)
            "foo"
        }
        val bar = async {
            delay(2000L)
            "bar"
        }

        println(foo.await())
        println(bar.await())
        println("Hello,")
    }
}