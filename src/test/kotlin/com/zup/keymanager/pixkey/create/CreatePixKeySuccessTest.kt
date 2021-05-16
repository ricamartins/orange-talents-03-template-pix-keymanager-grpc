package com.zup.keymanager.pixkey.create

import com.zup.keymanager.pixkey.ErpClient
import com.zup.keymanager.pixkey.PixKeyRepository
import com.zup.keymanager.proto.PixKeyServiceGrpc.PixKeyServiceBlockingStub
import com.zup.keymanager.setup.PixKeyCreateServiceTestSetup
import com.zup.keymanager.setup.options.ErpClientMockOption.OK_RESPONSE
import com.zup.keymanager.setup.options.PixKeyCreateRequestOption.*
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito

@MicronautTest(transactional=false)
class CreatePixKeySuccessTest(
    private val repository: PixKeyRepository,
    private val grpcClient: PixKeyServiceBlockingStub,
    private val setup: PixKeyCreateServiceTestSetup
) {

    @Test
    fun `should register pix key with valid document key type`() {

        val request = setup.options(
            requestOption = VALID_WITH_DOCUMENT_KEY_TYPE,
            erpClientOption = OK_RESPONSE
        )

        val result = grpcClient.create(request)

        with (result) {
            assertTrue(hasSuccess())
            assertEquals(request.clientId, success.clientId)
            assertTrue(repository.existsById(success.pixId))
        }
    }

    @Test
    fun `should register pix key with valid phone key type`() {

        val request = setup.options(
            requestOption = VALID_WITH_PHONE_KEY_TYPE,
            erpClientOption = OK_RESPONSE
        )

        val result = grpcClient.create(request)

        with (result) {
            assertTrue(hasSuccess())
            assertEquals(request.clientId, success.clientId)
            assertTrue(repository.existsById(success.pixId))
        }
    }

    @Test
    fun `should register pix key with valid email key type`() {

        val request = setup.options(
            requestOption = VALID_WITH_EMAIL_KEY_TYPE,
            erpClientOption = OK_RESPONSE
        )

        val result = grpcClient.create(request)

        with (result) {
            assertTrue(hasSuccess())
            assertEquals(request.clientId, success.clientId)
            assertTrue(repository.existsById(success.pixId))
        }
    }

    @Test
    fun `should register pix key with valid random key type`() {

        val request = setup.options(
            requestOption = VALID_WITH_RANDOM_KEY_TYPE,
            erpClientOption = OK_RESPONSE
        )

        val result = grpcClient.create(request)

        with (result) {
            assertTrue(hasSuccess())
            assertEquals(request.clientId, success.clientId)
            assertTrue(repository.existsById(success.pixId))
        }
    }

    @MockBean(ErpClient::class)
    fun erpClient() = Mockito.mock(ErpClient::class.java)

}