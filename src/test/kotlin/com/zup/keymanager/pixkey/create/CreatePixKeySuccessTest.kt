package com.zup.keymanager.pixkey.create

import com.zup.keymanager.extensions.toBcbCreatePixKeyRequest
import com.zup.keymanager.extensions.translate
import com.zup.keymanager.pixkey.PixKeyRepository
import com.zup.keymanager.pixkey.clients.BcbClient
import com.zup.keymanager.pixkey.clients.ErpClient
import com.zup.keymanager.proto.PixKeyCreateRequest
import com.zup.keymanager.setup.GrpcClientHandler
import com.zup.keymanager.setup.options.AccountDetailsResponseOption
import com.zup.keymanager.setup.options.PixKeyCreateRequestOption.*
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`

@MicronautTest(transactional=false)
class CreatePixKeySuccessTest(
    private val repository: PixKeyRepository,
    private val grpcClient: GrpcClientHandler,
    private val erpClient: ErpClient,
    private val bcbClient: BcbClient
) {

    @BeforeEach
    fun cleanUp() { repository.deleteAll() }

    @Test
    fun `should register pix key with valid document key type`() {

        val request = VALID_WITH_DOCUMENT_KEY_TYPE.apply()
        setup(request)

        val result = grpcClient.create(request)

        with (result) {
            assertTrue(hasSuccess())
            assertEquals(request.clientId, success.createResponse.clientId)
            assertTrue(repository.existsById(success.createResponse.pixId))
        }
    }

    @Test
    fun `should register pix key with valid phone key type`() {

        val request = VALID_WITH_PHONE_KEY_TYPE.apply()
        setup(request)

        val result = grpcClient.create(request)

        with (result) {
            assertTrue(hasSuccess())
            assertEquals(request.clientId, success.createResponse.clientId)
            assertTrue(repository.existsById(success.createResponse.pixId))
        }
    }

    @Test
    fun `should register pix key with valid email key type`() {

        val request = VALID_WITH_EMAIL_KEY_TYPE.apply()
        setup(request)

        val result = grpcClient.create(request)

        with (result) {
            assertTrue(hasSuccess())
            assertEquals(request.clientId, success.createResponse.clientId)
            assertTrue(repository.existsById(success.createResponse.pixId))
        }
    }

    @Test
    fun `should register pix key with valid random key type`() {

        val request = VALID_WITH_RANDOM_KEY_TYPE.apply()
        setup(request)

        val result = grpcClient.create(request)

        with (result) {
            assertTrue(hasSuccess())
            assertEquals(request.clientId, success.createResponse.clientId)
            assertTrue(repository.existsById(success.createResponse.pixId))
        }
    }

    fun setup(request: PixKeyCreateRequest) {
        val accountDetails = AccountDetailsResponseOption.ANY.apply()
        `when`(erpClient.getAccountDetails(request.clientId, request.accountType.translate()))
            .thenReturn(HttpResponse.ok(accountDetails))

        val bcbRequest = request.toBcbCreatePixKeyRequest(accountDetails)
        `when`(bcbClient.create(bcbRequest)).thenReturn(HttpResponse.created(bcbRequest))
    }

    @MockBean(ErpClient::class)
    fun erpClient() = Mockito.mock(ErpClient::class.java)

    @MockBean(BcbClient::class)
    fun bcbClient() = Mockito.mock(BcbClient::class.java)
}