package effective.safe.companydetailsrepository

import SuspendLazy
import kotlinx.coroutines.*
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import suspendLazy
import java.math.BigDecimal
import java.util.concurrent.ConcurrentHashMap
import kotlin.test.assertEquals
import kotlin.time.measureTime

class CompanyDetailsRepositoryLazySuspend(
    private val client: CompanyDetailsClient,
    dispatcher: CoroutineDispatcher
) {
    private val details = ConcurrentHashMap<Company, SuspendLazy<CompanyDetails>>()


    suspend fun getDetails(company: Company): CompanyDetails {
        return details.computeIfAbsent(company) { suspendLazy { client.fetchDetails(company) } }.invoke()
    }

    fun getDetailsOrNull(company: Company): CompanyDetails? =
        details[company]?.valueOrNull()

    fun getReadyDetails(): Map<Company, CompanyDetails> =
        details
            .mapNotNull { (k, v) -> v.valueOrNull()?.let { k to it } }
            .toMap()

    fun clear() {
        details.clear()
    }
}


@OptIn(ExperimentalStdlibApi::class)
class CompanyDetailsRepository2Test {
    @Test
    fun `detailsFor should fetch details from client`() = runTest {
        // given
        val company = Company("1")
        val details = CompanyDetails("Company", "Address", BigDecimal.TEN)
        val client = FakeCompanyDetailsClient()
        client.setDetails(company, details)
        val repository = CompanyDetailsRepositoryDeferred(client, coroutineContext[CoroutineDispatcher]!!)

        // when
        val result = repository.getDetails(company)

        // then
        assertEquals(details, result)
    }

    @Test
    fun `detailsFor should cache details`() = runTest {
        // given
        val company = Company("1")
        val details = CompanyDetails("Company", "Address", BigDecimal.TEN)
        val client = FakeCompanyDetailsClient(
            details = mapOf(company to details),
            delay = 1000
        )
        val repository = CompanyDetailsRepositoryDeferred(client, coroutineContext[CoroutineDispatcher]!!)

        // when
        val result1 = repository.getDetails(company)

        // then
        assertEquals(details, result1)
        assertEquals(1000, currentTime)

        // when
        client.clear()

        // then
        val result = repository.getDetails(company)
        assertEquals(details, result)
    }

    @Test
    fun `getReadyDetails should return all details`() = runTest {
        // given
        val company1 = Company("1")
        val company2 = Company("2")
        val details1 = CompanyDetails("Company1", "Address1", BigDecimal.TEN)
        val details2 = CompanyDetails("Company2", "Address2", BigDecimal.ONE)
        val client = FakeCompanyDetailsClient(
            details = mapOf(company1 to details1, company2 to details2)
        )
        val repository = CompanyDetailsRepositoryDeferred(client, coroutineContext[CoroutineDispatcher]!!)

        // when
        repository.getDetails(company1)
        repository.getDetails(company2)
        val result = repository.getReadyDetails()

        // then
        assertEquals(mapOf(company1 to details1, company2 to details2), result)
    }

    @Test
    fun `getReadyDetails should fetch details asynchronously`() = runTest {
        // given
        val company1 = Company("1")
        val company2 = Company("2")
        val details1 = CompanyDetails("Company1", "Address1", BigDecimal.TEN)
        val details2 = CompanyDetails("Company2", "Address2", BigDecimal.ONE)
        val client = FakeCompanyDetailsClient(
            details = mapOf(company1 to details1, company2 to details2),
            delay = 1000,
        )
        val repository = CompanyDetailsRepositoryDeferred(client, coroutineContext[CoroutineDispatcher]!!)

        // when
        coroutineScope {
            launch {
                repository.getDetails(company1)
                assertEquals(1000, currentTime)
            }
            launch {
                repository.getDetails(company2)
                assertEquals(1000, currentTime)
            }
        }
        val result = repository.getReadyDetails()

        // then
        assertEquals(mapOf(company1 to details1, company2 to details2), result)
        assertEquals(1000, currentTime)
    }

    @Test
    fun `should not expose internal collection`() = runTest {
        // given
        val company = Company("1")
        val details = CompanyDetails("Company", "Address", BigDecimal.TEN)
        val client = FakeCompanyDetailsClient(
            details = mapOf(company to details)
        )
        val repository = CompanyDetailsRepositoryDeferred(client, coroutineContext[CoroutineDispatcher]!!)
        val detailsMap = repository.getReadyDetails()

        // when
        repository.getDetails(company)

        // then
        assertEquals(mapOf(), detailsMap)
    }

    @Test
    fun `getDetailsOrNull should return details if exists`() = runTest {
        // given
        val company = Company("1")
        val company2 = Company("2")
        val details = CompanyDetails("Company", "Address", BigDecimal.TEN)
        val client = FakeCompanyDetailsClient(
            details = mapOf(company to details)
        )
        val repository = CompanyDetailsRepositoryDeferred(client, coroutineContext[CoroutineDispatcher]!!)

        // then
        assertEquals(null, repository.getDetailsOrNull(company))
        assertEquals(null, repository.getDetailsOrNull(company2))

        // when
        repository.getDetails(company)

        // then
        assertEquals(details, repository.getDetailsOrNull(company))
        assertEquals(null, repository.getDetailsOrNull(company2))
    }

    // Synchronization tests

    @Test
    fun `should cache all details`() = runBlocking(Dispatchers.IO) {
        val parallelCalls = 10_000
        val companies = (0 until parallelCalls).map { Company(it.toString()) }
        val client = FakeCompanyDetailsClient(
            details = buildMap {
                companies.forEach { put(it, CompanyDetails("Company${it.id}", "Address${it.id}", BigDecimal.TEN)) }
            }
        )
        val repository = CompanyDetailsRepositoryDeferred(client, coroutineContext[CoroutineDispatcher]!!)

        coroutineScope {
            for (company in companies) {
                launch {
                    val details = repository.getDetails(company)
                    assertEquals(
                        CompanyDetails("Company${company.id}", "Address${company.id}", BigDecimal.TEN),
                        details
                    )
                }
            }
        }

        assertEquals(parallelCalls, repository.getReadyDetails().size)
    }

    @Test
    fun `should not have conflict when changing details and getting all`() = runBlocking(Dispatchers.IO) {
        val parallelCalls = 10_000
        val companies = (0 until parallelCalls).map { Company(it.toString()) }
        val client = FakeCompanyDetailsClient(
            details = buildMap {
                companies.forEach { put(it, CompanyDetails("Company${it.id}", "Address${it.id}", BigDecimal.TEN)) }
            }
        )
        val repository = CompanyDetailsRepositoryDeferred(client, coroutineContext[CoroutineDispatcher]!!)

        for (company in companies) {
            launch { repository.getDetails(company) }
        }
        repeat(1000) {
            launch { repository.getReadyDetails() }
        }
    }

    @Test
    @Disabled
    fun `should not fetch the same details twice`() = runTest {
        val company = Company("1")
        val details = CompanyDetails("Company", "Address", BigDecimal.TEN)
        val client = FakeCompanyDetailsClient(
            details = mapOf(company to details),
            delay = 1000
        )
        val repository = CompanyDetailsRepositoryDeferred(client, coroutineContext[CoroutineDispatcher]!!)

        coroutineScope {
            launch { repository.getDetails(company) }
            launch { repository.getDetails(company) }
        }

        assertEquals(1, client.calls)
    }
}
