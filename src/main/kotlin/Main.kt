import kotlinx.coroutines.delay


suspend fun a() {
// Suspends the coroutine for 1 second
    delay(1000)
    println("A")
}
// Suspending main is started by Kotlin in a coroutine
suspend fun main() {
    println("Before")
    a()
    println("After")
}