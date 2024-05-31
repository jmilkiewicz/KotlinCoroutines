import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers

class TestTest {

    @Test
    fun test1() = runTest {

        assertThat(currentTime, Matchers.`is`(0))
        delay(1000)
        assertThat(currentTime, Matchers.`is`(1000))
    }
    @Test
    fun test2() = runTest {
        assertThat(currentTime, Matchers.`is`(0))
        coroutineScope {
            launch { delay(1000) }
            launch { delay(1500) }
            launch { delay(2000) }
        }
        assertThat(currentTime, Matchers.`is`(2000))

    }
}