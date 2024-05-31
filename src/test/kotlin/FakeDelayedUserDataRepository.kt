import coroutines.starting.showuserdata.Friend
import coroutines.starting.showuserdata.Profile
import coroutines.starting.showuserdata.UserDataRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test


class FetchUserDataTest2 {

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


    @Test
    fun fetchUserDataReturnsDataFromRepos(): Unit = runTest {
        val sut = FetchUserUseCase(FakeDelayedUserDataRepository())

        val result = sut.fetchUserData()

        assertThat(currentTime, `is`(1500))
        assertThat(result, equalTo(sampleUser))
    }

    @Test
    fun fetchUserDataReturnsDataFromReposNoAsync(): Unit = runTest {
        val sut = FetchUserUseCase(FakeDelayedUserDataRepository())

        val result = sut.fetchUserData2()

        assertThat(currentTime, `is`(2800))
        assertThat(result, equalTo(sampleUser))
    }

}