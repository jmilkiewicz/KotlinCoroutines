import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

fun main(): Unit = runBlocking {
    var counter = 0
    val mysingleThreadDispatcher: CoroutineDispatcher = Dispatchers.IO.limitedParallelism(1)
    doSth(100000, mysingleThreadDispatcher) { counter++ }
    println(counter)
}


suspend fun doSth(count: Int, dispatcher: CoroutineDispatcher, action: (Unit) -> Unit) = coroutineScope {
    withContext(Dispatchers.IO) {
        repeat(count) {
            launch(dispatcher) { action.invoke(Unit) }

            //!!!!WRONG
            //launch(Dispatchers.IO.limitedParallelism(1)) { action.invoke(Unit) }
            //as each repeat will get its own dispatcher!!!
        }
    }
}
