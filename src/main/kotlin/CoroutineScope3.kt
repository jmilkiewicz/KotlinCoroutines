import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay

suspend fun getArticle(): List<String> {
    println("bede pobieral article")
    delay(2000)
    return listOf("article1", "article2", "article3")
}

fun hasAccess(user: String, it: String): Boolean {
    return true
}

fun toArticleJson(article: String): String {
    return "{$article}"
}

suspend fun main() {
    val coroutineScope = coroutineScope {
        val user = async {
            println("bede pobieral usera")
            delay(2000)
            "user"
        }
        val articles = getArticle()
        articles
            .filter { hasAccess(user.await(), it) }
            .map { toArticleJson(it) }
    }

    println(coroutineScope)
}