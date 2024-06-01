import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


fun main() = runBlocking(CoroutineName("main")) {
    println("Started") // [main] Started

    var childrenCounter = 0
    val v1 = async(Dispatchers.IO) {
        println("async#1 start")
        async {
            println("async1#2 start")
            delay(500)
            childrenCounter++
        }
        async {
            println("async1#3 start")
            delay(100)
            childrenCounter++
        }

        launch {
            println("async1#4 start")
            delay(200)
            childrenCounter++
        }

        coroutineScope {
            launch {
                println("async1#5 start")
                delay(300)
                childrenCounter++
            }

        }

        println("async#1 about to finish Finish")
        42
    }

    // v1 must wait for all its children to finish as they are running in the same scope
    println("v1 is finished ${v1.await()} after all its coroutine children are finished: $childrenCounter")

    var v2InternalExecuted = false
    val v2 = async(Dispatchers.IO) {
        println("async2#1 start")
        delay(500)
        GlobalScope.async {
            println("async2#1 start")
            delay(500)
            v2InternalExecuted = true
        }
        println("async2#1 about to finish Finish")
        42
    }

    println(
        "v2 is finished ${v2.await()} but some internal routines are run on different scope so it does not wait " +
                "for them to finish : $v2InternalExecuted  => we are losing structured concurrency"
    )



    println("FINITO")
}