package coroutines.test.notificationsendertest

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class NotificationSender(
    private val client: NotificationClient,
    private val exceptionCollector: ExceptionCollector,
    dispatcher: CoroutineDispatcher,
) {
    private val exceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            exceptionCollector.collectException(throwable)
        }
    val scope: CoroutineScope = CoroutineScope(
        SupervisorJob() + dispatcher + exceptionHandler
    )

    fun sendNotifications(notifications: List<Notification>) {
        notifications.forEach { notification ->
            scope.launch {
                client.send(notification)
            }
        }
    }

    fun cancel() {
        scope.coroutineContext.cancelChildren()
    }
}

data class Notification(val id: String)

interface NotificationClient {
    suspend fun send(notification: Notification)
}

interface ExceptionCollector {
    fun collectException(throwable: Throwable)
}

class NotificationSenderTest {

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `should send notifications concurrently`() = runTest {
        val client = FakeNotificationClient(100)
        val notifications = List(100) { Notification(it.toString()) }
        val sut = NotificationSender(
            client = client,
            exceptionCollector = FakeExceptionCollector(),
            dispatcher = currentCoroutineContext().get(CoroutineDispatcher.Key)!!
        )

        sut.sendNotifications(notifications)

        //!!! this is crucial
        advanceUntilIdle()

        assertThat(currentTime, equalTo(100))
        assertThat(client.sent, equalTo(notifications))
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `should cancel all coroutines when cancel is called`() = runTest {
        val client = FakeNotificationClient(delayTime = 100)
        val notifications = List(100) { Notification(it.toString()) }
        val sut = NotificationSender(
            client = client,
            exceptionCollector = FakeExceptionCollector(),
            dispatcher = currentCoroutineContext().get(CoroutineDispatcher.Key)!!
        )

        sut.sendNotifications(notifications)
        advanceTimeBy(99)
        sut.cancel()

        advanceUntilIdle()

        assertThat(client.sent, Matchers.empty())

    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `should not cancel other sending processes when one of them fails`() = runTest {
        val client = FakeNotificationClient(
            delayTime = 100,
            failEvery = 2
        )
        val notifications = List(4) { Notification(it.toString()) }
        val sut = NotificationSender(
            client = client,
            exceptionCollector = FakeExceptionCollector(),
            dispatcher = currentCoroutineContext().get(CoroutineDispatcher.Key)!!
        )

        sut.sendNotifications(notifications)

        advanceTimeBy(100)
        runCurrent()

        assertThat(client.sent, containsInAnyOrder(Notification("0"), Notification("2")))
        assertThat(currentTime, equalTo(100))
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `should collect exceptions from all coroutines`() = runTest {
        val notifications = List(4) { Notification(it.toString()) }
        val exceptionCollector = FakeExceptionCollector()
        val sut = NotificationSender(
            client = FakeNotificationClient(
                delayTime = 1,
                failEvery = 2
            ),
            exceptionCollector = exceptionCollector,
            dispatcher = currentCoroutineContext().get(CoroutineDispatcher.Key)!!
        )

        sut.sendNotifications(notifications)

        advanceTimeBy(1)
        runCurrent()

        assertThat(
            exceptionCollector.collected,
            containsInAnyOrder(FakeFailure(Notification("1")), FakeFailure(Notification("3")))
        )

    }
}

class FakeNotificationClient(
    val delayTime: Long = 0L,
    val failEvery: Int = Int.MAX_VALUE
) : NotificationClient {
    var sent = emptyList<Notification>()
    var counter = 0
    var usedThreads = emptyList<String>()

    override suspend fun send(notification: Notification) {
        if (delayTime > 0) delay(delayTime)
        usedThreads += Thread.currentThread().name
        counter++
        if (counter % failEvery == 0) {
            println("FAILURE")
            throw FakeFailure(notification)
        }
        sent += notification
    }
}

data class FakeFailure(val notification: Notification) : Throwable("Planned fail for notification ${notification.id}")

class FakeExceptionCollector : ExceptionCollector {
    var collected = emptyList<Throwable>()

    override fun collectException(throwable: Throwable) = synchronized(this) {
        collected += throwable
    }
}
