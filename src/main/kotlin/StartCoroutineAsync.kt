import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay

suspend fun main() {
    val deferred1 = GlobalScope.async (CoroutineName("cor1")) {
        println(
            " ${coroutineContext[CoroutineName.Key]} is executing on thread : ${Thread.currentThread().name}"
        )
        delay(2000L)
        1
    }
    val deferred2 = GlobalScope.async(CoroutineName("cor2")) {
        println(
            " ${coroutineContext[CoroutineName.Key]} is executing on thread : ${Thread.currentThread().name}"
        )
        delay(2000L)
        2
    }
    val deferred3 = GlobalScope.async(CoroutineName("cor3")) {
        println(
            " ${coroutineContext[CoroutineName.Key]} is executing on thread : ${Thread.currentThread().name}"
        )
        delay(2000L)
        3
    }

    println("Started")

    print(deferred1.await())
    print(deferred2.await())
    print(deferred3.await())

    println("Finished")
}