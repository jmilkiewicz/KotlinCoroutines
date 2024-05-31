import coroutines.starting.showuserdata.Friend
import coroutines.starting.showuserdata.Profile
import coroutines.starting.showuserdata.UserDataRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class FetchUserUseCase(
    private val repo: UserDataRepository,
) {
    suspend fun fetchUserData(): coroutines.starting.showuserdata.User = coroutineScope {
        val name = async { repo.getName() }
        val friends = async { repo.getFriends() }
        val profile = async { repo.getProfile() }
        coroutines.starting.showuserdata.User(
            name = name.await(), friends = friends.await(), profile = profile.await()
        )
    }

    suspend fun fetchUserData2(): coroutines.starting.showuserdata.User {
        val name = repo.getName()
        val friends = repo.getFriends()
        val profile = repo.getProfile()
        return coroutines.starting.showuserdata.User(
            name = name, friends = friends, profile = profile
        )
    }
}

class FetchUserDataTest {
    class FakeUserDataRepository : UserDataRepository {
        override suspend fun notifyProfileShown() {
            TODO("Not yet implemented")
        }

        override suspend fun getName(): String = "Ben"
        override suspend fun getFriends(): List<Friend> = listOf(Friend("some-friend-id-1"))

        override suspend fun getProfile(): Profile = Profile("Example description")
    }


    @Test
    fun fetchUserDataReturnsDataFromRepos(): Unit = runBlocking {
        val sut = FetchUserUseCase(FakeUserDataRepository())

        val result = sut.fetchUserData()

        //This test does not check if actually we are calling async/await
        //val result = sut.fetchUserData2()

        val expectedUser = coroutines.starting.showuserdata.User(
            name = "Ben",
            friends = listOf(Friend("some-friend-id-1")),
            profile = Profile("Example description")
        )
        assertThat(result, equalTo(expectedUser))
    }
}