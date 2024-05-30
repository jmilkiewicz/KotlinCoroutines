import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

var mycounter = 0
val mysingleThreadDispatcher: CoroutineDispatcher = Dispatchers.IO.limitedParallelism(1)
fun main(): Unit = runBlocking {
    doSth(100000) { mycounter++ }
    println(mycounter)
}


suspend fun doSth(count: Int, action: (Unit) -> Unit) = coroutineScope {
    withContext(Dispatchers.IO) {
        repeat(count) {
            launch(mysingleThreadDispatcher) { action.invoke(Unit) }

            //!!!!WRONG
            //launch(Dispatchers.IO.limitedParallelism(1)) { action.invoke(Unit) }
            //as each repeat will get its own dispatcher!!!
        }
    }

}

