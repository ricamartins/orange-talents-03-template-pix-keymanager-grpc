package com.zup.keymanager.pixkey.create

import com.zup.keymanager.pixkey.ErpClient
import com.zup.keymanager.pixkey.PixKeyRepository
import com.zup.keymanager.proto.PixKeyServiceGrpc.PixKeyServiceBlockingStub
import com.zup.keymanager.setup.GrpcClientHandler
import com.zup.keymanager.setup.PixKeyCreateServiceTestSetup
import com.zup.keymanager.setup.options.ErpClientMockOption.BAD_REQUEST_RESPONSE
import com.zup.keymanager.setup.options.ErpClientMockOption.NOT_FOUND_RESPONSE
import com.zup.keymanager.setup.options.PixKeyCreateRequestOption.VALID_WITH_RANDOM_KEY_TYPE
import com.zup.keymanager.setup.options.PixKeyCreateScenarioOption.PIX_KEY_CREATE_REQUEST_PIX_KEY_ALREADY_REGISTERED
import io.grpc.Status
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito

@MicronautTest(transactional=false)
class CreatePixKeyFailureTest(
    private val repository: PixKeyRepository,
    private val grpcClient: GrpcClientHandler,
    private val setup: PixKeyCreateServiceTestSetup
) {

    @Test
    fun `should return not_found when client or account does not exists`() {

        val request = setup.options(
            requestOption = VALID_WITH_RANDOM_KEY_TYPE,
            erpClientOption = NOT_FOUND_RESPONSE
        )

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

        val request = setup.options(
            scenarioOption = PIX_KEY_CREATE_REQUEST_PIX_KEY_ALREADY_REGISTERED
        )

        val result = grpcClient.create(request)

        with (result) {
            assertTrue(hasFailure())
            assertEquals("6 ALREADY_EXISTS", status)
            assertEquals("Key value is already registered", failure.message)
            assertTrue(repository.existsByKeyValue(request.keyValue))
        }

    }

    @Test
    fun `should return internal when erp client call fails`() {

        val request = setup.options(
            requestOption = VALID_WITH_RANDOM_KEY_TYPE,
            erpClientOption = BAD_REQUEST_RESPONSE
        )

        val result = grpcClient.create(request)

        with (result) {
            assertTrue(hasFailure())
            assertEquals("13 INTERNAL", status)
            assertEquals("Something went wrong", failure.message)
            assertFalse(repository.existsByClientId(request.clientId))
        }

    }

    @MockBean(ErpClient::class)
    fun erpClient() = Mockito.mock(ErpClient::class.java)

}