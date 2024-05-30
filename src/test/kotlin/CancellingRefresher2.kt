package effective.safe.cancellingrefresher

import kotlinx.coroutines.*
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CancellingRefresher2(
    private val scope: CoroutineScope,
    private val refreshData: suspend () -> Unit,
) {
    private var refreshJob: Job? = null

    private val singleThreadDispatcher = Dispatchers.Default.limitedParallelism(1)
    // Single thread dispatcher seems to be better than mutex here
    // as mutex will have to allow one call to refresh function, actually meaning that we have no concurrency
    // With Single thread dispatcher as soon as coroutine is suspended other coroutine can enter the method, will cancel
    // previous job (the first coroutine which is suspended) and refresh data. As soon as it is suspended,
    // other coroutine can enter, kill it and ...
    suspend fun refresh() = withContext(singleThreadDispatcher) {
        refreshJob?.cancel()
        refreshJob = scope.launch {
            refreshData()
        }
    }

}

class CancellingRefresherTest2 {
    @Test
    fun `should cancel previous refresh when starting new one`(): Unit = runTest {
        val userRefresher = CancellingRefresher(scope = backgroundScope, refreshData = {
            delay(1000)
        })

        coroutineScope {
            repeat(1000) {
                launch { userRefresher.refresh() }
            }
            delay(1000)
            repeat(1000) {
                launch { userRefresher.refresh() }
            }
            delay(1000)
            repeat(1000) {
                launch { userRefresher.refresh() }
            }
        }
        assertEquals(2000, currentTime) // Delays
        val children = backgroundScope.coroutineContext[Job]!!.children
        assertEquals(1, children.count { it.isActive })
        children.forEach { it.join() }
        assertEquals(3000, currentTime)
    }

    @Test
    fun `should cancel all previous jobs`(): Unit = runTest {
        val userRefresher = CancellingRefresher(scope = backgroundScope, refreshData = { delay(Long.MAX_VALUE) })
        coroutineScope {
            repeat(50_000) {
                launch {
                    userRefresher.refresh()
                }
            }
        }
        delay(1000)
        assertEquals(1, backgroundScope.coroutineContext.job.children.count { it.isActive })
    }

    @Test
    fun `should cancel all previous jobs (real time)`(): Unit = runBlocking(Dispatchers.Default) {
        val backgroundScope = CoroutineScope(Job() + Dispatchers.Default)
        val userRefresher = CancellingRefresher(scope = backgroundScope, refreshData = { delay(Long.MAX_VALUE) })
        coroutineScope {
            repeat(50_000) {
                launch {
                    userRefresher.refresh()
                }
            }
        }
        delay(1000)
        assertEquals(1, backgroundScope.coroutineContext.job.children.count { it.isActive })
        backgroundScope.cancel()
    }
}
