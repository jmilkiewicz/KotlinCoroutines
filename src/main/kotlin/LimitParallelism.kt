import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

var counter = 0
val singleThreadDispatcher: CoroutineDispatcher = Dispatchers.IO.limitedParallelism(1)
fun main(): Unit = runBlocking {
    repeat(200000) {
        launch { increment() }
    }

    delay(5000)
    println(counter)
}

suspend fun increment() = coroutineScope {
    launch(singleThreadDispatcher) {
        println("enter")
//        !!!!
//        without this delay singleThreadDispatcher runs everything synchronised as interweaved enter/EXIT as it allows
//        just one thread. BUT with delay it allows one thread but coroutine that enters delay is suspended
//        so other coroutine can enter -> we have a mix of enter and EXIT.
        delay(1)
        counter++
        println("EXIT")
    }
}

