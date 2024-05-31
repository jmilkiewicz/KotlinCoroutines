import coroutines.starting.showuserdata.Friend
import coroutines.starting.showuserdata.Profile
import coroutines.starting.showuserdata.UserDataRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test


class BackgroudScopeTest {

    @Test
    fun `should increment counter`() = runTest {
        var i = 0
        backgroundScope.launch {
            while (true) {
                delay(1000)
                i++
            }
        }
        advanceTimeBy(5001)

        assertThat(i, equalTo(5))
    }
}