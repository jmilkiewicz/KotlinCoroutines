import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.CoroutineContext

fun main(): Unit = runBlocking {
    val name = CoroutineName("Some name")
    val job = Job()
    val trueParent = coroutineContext.job
    val context: CoroutineContext = name + job
    launch(context) { // the new job replaces one from parent
        println("this job has refernce to true parent ? ${coroutineContext.job.parent == trueParent}")
        println("this job has reference to argument job ? ${coroutineContext.job.parent == job}")
        // so parent job does not know its children job so can not wait for it
        delay(1000)
        println("Will not be printed")
    }
}