package com.zup.keymanager.pixkey.create

import com.zup.keymanager.extensions.toBcbCreatePixKeyRequest
import com.zup.keymanager.extensions.translate
import com.zup.keymanager.pixkey.PixKeyRepository
import com.zup.keymanager.pixkey.clients.BcbClient
import com.zup.keymanager.pixkey.clients.ErpClient
import com.zup.keymanager.setup.GrpcClientHandler
import com.zup.keymanager.setup.ProtoAnnotatorExtension
import com.zup.keymanager.setup.options.AccountDetailsResponseOption
import com.zup.keymanager.setup.options.PixKeyCreateRequestOption.VALID_WITH_RANDOM_KEY_TYPE
import com.zup.keymanager.setup.options.PixKeyCreateScenarioOption.PIX_KEY_CREATE_REQUEST_PIX_KEY_ALREADY_REGISTERED
import io.micronaut.http.HttpResponse
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.mockito.Mockito.`when`

@MicronautTest(transactional=false)
@ExtendWith(ProtoAnnotatorExtension::class)
class CreatePixKeyFailureTest(
    private val repository: PixKeyRepository,
    private val grpcClient: GrpcClientHandler,
    private val erpClient: ErpClient,
    private val bcbClient: BcbClient
) {

    @BeforeEach
    fun cleanUp() { repository.deleteAll() }

    @Test
    fun `should return not_found when client or account does not exists`() {

        val request = VALID_WITH_RANDOM_KEY_TYPE.apply()
        `when`(erpClient.getAccountDetails(request.clientId, request.accountType.translate()))
            .thenReturn(HttpResponse.notFound())

        val result = grpcClient.create(request)

        with (result) {
            assertTrue(hasFailure())
            assertEquals("5 NOT_FOUND", status)
            assertEquals("Client or account does not exists", failure.message)
            assertFalse(repository.existsByClientId(request.clientId))
        }

    }

    @Test
    fun `should return already_exists when key value is already registered`() {

        val request = PIX_KEY_CREATE_REQUEST_PIX_KEY_ALREADY_REGISTERED.apply(repository)

        val result = grpcClient.create(request!!)

        with (result) {
            assertTrue(hasFailure())
            assertEquals("6 ALREADY_EXISTS", status)
            assertEquals("Key value is already registered", failure.message)
            assertTrue(repository.existsByKeyValue(request.keyValue))
        }

    }

    @Test
    fun `should return internal when erp client call fails`() {

        val request = VALID_WITH_RANDOM_KEY_TYPE.apply()
        `when`(erpClient.getAccountDetails(request.clientId, request.accountType.translate()))
            .thenThrow(HttpClientResponseException("Bad Request", HttpResponse.badRequest<Any>()))

        val result = grpcClient.create(request)

        with (result) {
            assertTrue(hasFailure())
            assertEquals("13 INTERNAL", status)
            assertEquals("Something went wrong", failure.message)
            assertFalse(repository.existsByClientId(request.clientId))
        }

    }

    @Test
    fun `should return failed_precondition when could not register pix key at the central bank`() {

        val request = VALID_WITH_RANDOM_KEY_TYPE.apply()

        val accountDetails = AccountDetailsResponseOption.ANY.apply()
        `when`(erpClient.getAccountDetails(request.clientId, request.accountType.translate()))
            .thenReturn(HttpResponse.ok(accountDetails))

        val bcbRequest = request.toBcbCreatePixKeyRequest(accountDetails)
        `when`(bcbClient.create(bcbRequest)).thenThrow(unprocessableEntityException())

        val result = grpcClient.create(request)

        with (result) {
            assertTrue(hasFailure())
            assertEquals("6 ALREADY_EXISTS", status)
            assertEquals("Pix key already registered at the Central Bank", failure.message)
            assertFalse(repository.existsByClientId(request.clientId))
        }

    }

    private fun unprocessableEntityException(): HttpClientResponseException {
        return HttpClientResponseException("Chave pix já registrada", HttpResponse.unprocessableEntity<Any>())
    }

    @MockBean(ErpClient::class)
    fun erpClient() = Mockito.mock(ErpClient::class.java)

    @MockBean(BcbClient::class)
    fun bcbClient() = Mockito.mock(BcbClient::class.java)
}