package coroutines.recipes.mapasync


import coroutines.starting.showuserdata.Friend
import coroutines.starting.showuserdata.Profile
import coroutines.starting.showuserdata.UserDataRepository
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


class InjectCustomDispatcherTest {

    class FetchUserUseCase(
        private val repo: UserDataRepository,
        private val dispatcher: CoroutineDispatcher = Dispatchers.IO
    ) {
        suspend fun fetchUserData(): coroutines.starting.showuserdata.User = withContext(dispatcher) {
            val name = async { repo.getName() }
            val friends = async { repo.getFriends() }
            val profile = async { repo.getProfile() }
            coroutines.starting.showuserdata.User(
                name = name.await(), friends = friends.await(), profile = profile.await()
            )
        }
    }

    companion object {
        val sampleUser = coroutines.starting.showuserdata.User(
            name = "Ben",
            friends = listOf(Friend("some-friend-id-1")),
            profile = Profile("Example description")
        )
    }

    class FakeDelayedUserDataRepository : UserDataRepository {
        override suspend fun notifyProfileShown() {
            TODO("Not yet implemented")
        }

        override suspend fun getName(): String {
            delay(300)
            return sampleUser.name
        }

        override suspend fun getFriends(): List<Friend> {
            delay(1500)
            return sampleUser.friends
        }

        override suspend fun getProfile(): Profile {
            delay(1000)
            return sampleUser.profile
        }
    }


    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun withDispatcherSwitchTest() = runTest{
        val testDispatcher = currentCoroutineContext().get(CoroutineDispatcher.Key)!!

        val fetchUserUseCase = FetchUserUseCase(FakeDelayedUserDataRepository(), testDispatcher)

        val fetchUserData = fetchUserUseCase.fetchUserData()

        assertThat(fetchUserData, `is`(sampleUser))
        assertThat(currentTime, `is`(1500))
    }


}
