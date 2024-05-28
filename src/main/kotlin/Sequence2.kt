import java.math.BigInteger
import kotlin.random.Random

fun main() {


    val fibonacci = sequence<BigInteger> {
        var x = BigInteger.ZERO
        var y = BigInteger.ONE
        while (true) {
            yield(x)
            val tmp = x
            x = y
            y += tmp
        }
    }

    println(fibonacci.take(10).toList())


    fun randomUniqueStrings(
        length: Int,
        seed: Long = System.currentTimeMillis()
    ): Sequence<String> = sequence {
        val random = Random(seed)
        val charPool = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        while (true) {
            val randomString = (1..length)
                .map { i -> random.nextInt(charPool.size) }
                .map(charPool::get)
                .joinToString("")
            yield(randomString)
        }
    }.distinct()
}