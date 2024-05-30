import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main(): Unit = runBlocking {
    val name = CoroutineName("Some name")
    val parent = coroutineContext.job
    launch(name) { // the new job replaces one from parent
        println(coroutineContext.job.parent == parent)
        delay(1000)
        println("Must be printed")

    }
}