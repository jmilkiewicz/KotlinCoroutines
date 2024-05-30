import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

suspend fun main() {
    val job1 = GlobalScope.launch(CoroutineName("cor1")) {
        println(
            " ${coroutineContext[CoroutineName.Key]} is executing on thread : ${Thread.currentThread().name}"
        )
        delay(1000L)
        println("World!")
    }
    val job2 = GlobalScope.launch(CoroutineName("cor2")) {
        println(
            " ${coroutineContext[CoroutineName.Key]} is executing on thread : ${Thread.currentThread().name}"
        )
        delay(1000L)
        println("World!")
    }
    val job3 = GlobalScope.launch(CoroutineName("cor3")) {
        println(
            " ${coroutineContext[CoroutineName.Key]} is executing on thread : ${Thread.currentThread().name}"
        )
        delay(1000L)
        println("World!")
    }

    println("Hello,")
    //without sleep or join it terminates as launch runs in async
    //Thread.sleep(2000L)
    job1.join()
    job2.join()

}