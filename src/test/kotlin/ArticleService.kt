package coroutines.test.articleservice

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class ArticleService(
    private val articleRepository: ArticleRepository,
    private val userService: UserService,
    private val timeProvider: TimeProvider,
) {
    suspend fun getArticles(
        requestAuth: RequestAuth? = null,
        series: ArticleSeries? = null,
    ): List<Article> = coroutineScope {
        val articles = async { articleRepository.getArticles() }
        val currentUser = userService.findUser(requestAuth)
        val time = timeProvider.now()
        articles.await()
            .filter { series == null || it.series == series }
            .filter { canSeeOnList(currentUser, it, time) }
    }

    private fun canSeeOnList(
        user: User?,
        article: Article,
        time: LocalDateTime
    ): Boolean = when {
        user?.isAdmin == true -> true
        article.isPublic && time >= article.releaseDate -> true
        user?.key == article.authorKey -> true
        else -> false
    }
}

interface ArticleRepository {
    suspend fun getArticles(): List<Article>
}

interface UserService {
    suspend fun findUser(requestAuth: RequestAuth?): User?
}

interface TimeProvider {
    fun now(): LocalDateTime
}

class User(val key: String, val isAdmin: Boolean)
class RequestAuth(val token: String)

data class ArticleSeries(val key: String)
data class Article(
    val key: String,
    val title: String,
    val content: String,
    val authorKey: String,
    val isPublic: Boolean,
    val series: ArticleSeries?,
    val releaseDate: LocalDateTime,
)

data class Date(val toEpochSecond: () -> Long)

class ArticleServiceTest {
    companion object {

        val now = LocalDateTime.now()
        val someArticle = Article("key", "title", "content", "authorKey", true, null, now)
    }

    class FakeArticleRepository(private val articles: List<Article>) : ArticleRepository {
        override suspend fun getArticles(): List<Article> {
            delay(1000)
            return articles
        }
    }

    class FakeUserService(private val user: User?) : UserService {
        override suspend fun findUser(requestAuth: RequestAuth?): User? {
            delay(500)
            return user
        }
    }

    class LocalTimeProvider(private val localDateTime: LocalDateTime = now) : TimeProvider {
        override fun now(): LocalDateTime =
            localDateTime
    }


    @Test
    fun `should asynchronously fetch articles and users`() = runTest {
        val sut = ArticleService(
            articleRepository = FakeArticleRepository(emptyList()), userService = FakeUserService(User("key", true)),
            timeProvider = LocalTimeProvider()
        )

        sut.getArticles()

        assertThat(currentTime, Matchers.`is`(1000))

    }

    @Test
    fun `should return previously published public articles`() = runTest {

        val publicArticle1 = someArticle.copy(isPublic = true, key = "key1", releaseDate = now.plusHours(1))
        val publicArticle2 = someArticle.copy(isPublic = true, key = "key2")
        val nonPublicArticle = someArticle.copy(isPublic = false)


        val sut = ArticleService(
            articleRepository = FakeArticleRepository(listOf(publicArticle1, publicArticle2, nonPublicArticle)),
            userService = FakeUserService(null),
            timeProvider = LocalTimeProvider(now)
        )

        val result = sut.getArticles()

        assertThat(result, Matchers.containsInAnyOrder(publicArticle2))
    }

    @Test
    fun `should return all articles for the specified author`() = runTest {
        val user1Article =
            someArticle.copy(key = "key1", authorKey = "auth1")
        val user2Article = someArticle.copy(key = "key2", authorKey = "auth2", isPublic = false)


        val sut = ArticleService(
            articleRepository = FakeArticleRepository(listOf(user2Article, user1Article)),
            userService = FakeUserService(User("auth1", false)),
            timeProvider = LocalTimeProvider()
        )

        val result = sut.getArticles()

        assertThat(result, Matchers.containsInAnyOrder(user1Article))
    }

    @Test
    fun `should show all articles for Admin`() = runTest {
        val user1Article =
            someArticle.copy(key = "key1", authorKey = "auth1")
        val user2Article = someArticle.copy(key = "key2", authorKey = "auth2", isPublic = false)
        val inFutureArticle =
            someArticle.copy(key = "key2", authorKey = "auth2", isPublic = false, releaseDate = now.plusHours(4))


        val sut = ArticleService(
            articleRepository = FakeArticleRepository(listOf(user2Article, user1Article, inFutureArticle)),
            userService = FakeUserService(User("auth1", true)),
            timeProvider = LocalTimeProvider()
        )

        val result = sut.getArticles()

        assertThat(result, Matchers.containsInAnyOrder(user1Article, user2Article, inFutureArticle))
    }


}
