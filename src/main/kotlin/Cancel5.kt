import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.cancellation.CancellationException


class MyContextScope(context: CoroutineContext) : CoroutineScope {
    override val coroutineContext: CoroutineContext = context

    // CoroutineScope is used intentionally for user-friendly representation
    override fun toString(): String = "CoroutineScope(coroutineContext=$coroutineContext)"
}

suspend fun main() {
    val scope = CoroutineScope(Job())
    val job1 = scope.launch { // will be ignored
        println("startuje job1")
        try {
            delay(10000)
            println("Nie zobacze bo cancel")
        } finally {
            println("finally job1")
        }
    }

    scope.coroutineContext.cancelChildren()
    scope.launch {
        println("It shall be printed as paren job (withing scope) is not cancelled - only its children are cancelled ")
    }
    delay(1)
    scope.cancel()
    scope.launch { // will be ignored
        println("It will not be printed as parent job is cancelled so it can not start new children")
    }

}