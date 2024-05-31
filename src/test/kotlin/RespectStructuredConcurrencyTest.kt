package coroutines.recipes.mapasync


import kotlinx.coroutines.*
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import kotlin.coroutines.CoroutineContext
import kotlin.test.assertEquals
import kotlin.test.assertTrue

suspend fun <T, R> Iterable<T>.mapAsync2(
    transformation: suspend (T) -> R
): List<R> = coroutineScope {
    map { async { transformation(it) } }.awaitAll()
}

class RespectStructuredConcurrencyTest {

    @Test
    fun should_map_async_and_keep_elements_order() = runTest {
        val transforms: List<suspend () -> String> = listOf(
            { delay(3000); "A" },
            { delay(2000); "B" },
            { delay(4000); "C" },
            { delay(1000); "D" },
        )

        val res = transforms.mapAsync2 { f -> f() }
        assertEquals(listOf("A", "B", "C", "D"), res)
        assertEquals(4000, currentTime)
    }

    @Test
    fun should_support_context_propagation() = runTest {
        var capturedContext: CoroutineContext? = null

        val coroutineName = CoroutineName("some Name")
        withContext(coroutineName) {
            listOf(1, 2).mapAsync2 {
                capturedContext = currentCoroutineContext()
                it
            }
        }

        assertThat(capturedContext!!.get(CoroutineName.Key), `is`(coroutineName))
    }

    @Test
    fun should_support_cancellation() = runTest {
        var capturedJob: Job? = null

        val parentJob = launch {
            listOf("A").mapAsync {
                println("ASDASDAS")
                capturedJob = currentCoroutineContext().job
                delay(Long.MAX_VALUE)
            }
        }

        //!!! this delay or advanceTimeBy is important as we need to cancel parentJob after mapAsync had a chance to run
        // (so we can capture its job)
        //delay(1) or advanceTimeBy(1)
        advanceTimeBy(1)

        parentJob.cancel()

        assertThat(capturedJob!!.isCancelled, `is`(true))
    }

    @Test
    fun should_propagate_exceptions() = runTest {
        // given
        val e = object : Throwable() {}
        val bodies = listOf(
            suspend { "A" },
            suspend { delay(1000); "B" },
            suspend { delay(500); throw e },
            suspend { "C" }
        )

        // when
        val result = runCatching { bodies.mapAsync { it() } }

        // then
        assertTrue(result.isFailure)
        assertEquals(e, result.exceptionOrNull())
        assertEquals(500, currentTime)
    }
}
