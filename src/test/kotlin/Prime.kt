package coroutines.sequences.prime

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import java.math.BigInteger
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals

fun isPrime(n: BigInteger): Boolean {
    return (2..n.sqrt().toLong()).none { n.divideAndRemainder(it.toBigInteger())[1] == BigInteger.ZERO }
}

val primes: Sequence<BigInteger> = sequence {
    var start = BigInteger.TWO
    while (true) {
        if (isPrime(start)) {
            yield(start)
        }
        start++
    }
}

class PrimesTest {
    @Test
    fun `should calculate the first 10 prime numbers`() {
        val primes = primes.take(10).toList()
        val expected = listOf(
            BigInteger("2"),
            BigInteger("3"),
            BigInteger("5"),
            BigInteger("7"),
            BigInteger("11"),
            BigInteger("13"),
            BigInteger("17"),
            BigInteger("19"),
            BigInteger("23"),
            BigInteger("29"),
        )
        assertEquals(expected, primes)
    }

    @Test
    @Timeout(1000, unit = TimeUnit.MILLISECONDS)
    fun `should calculate 1000'th prime number`() {
        val prime = primes.drop(1000).first()
        assertEquals(BigInteger("7927"), prime)
    }
}
