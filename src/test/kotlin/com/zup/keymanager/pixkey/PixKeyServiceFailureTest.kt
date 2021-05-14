package com.zup.keymanager.pixkey

import com.zup.keymanager.pixkey.PixKeyServiceTestSetup.*
import com.zup.keymanager.proto.PixKeyRequest
import com.zup.keymanager.proto.PixKeyServiceGrpc.PixKeyServiceBlockingStub
import com.zup.keymanager.proto.PixKeyServiceGrpc.newBlockingStub
import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito

@MicronautTest(transactional=false)
class PixKeyServiceFailureTest(
    private val repository: PixKeyRepository,
    private val grpcClient: PixKeyServiceBlockingStub,
    private val setup: PixKeyServiceTestSetup
) {

    lateinit var request: PixKeyRequest

    @Test
    fun `should return not_found when client or account does not exists`() {

        request = setup.options(
            Database.CLEAN_ALL,
            Request.VALID_WITH_RANDOM_KEY_TYPE,
            Mock.NOT_FOUND_RESPONSE
        )

        val result = grpcClient.create(request)

        with (result) {
            assertTrue(hasFailure())
            assertEquals("5 NOT_FOUND", failure.status)
            assertEquals("client", failure.errorsList[0].field)
            assertEquals("Client or account does not exists", failure.errorsList[0].message)
            assertFalse(repository.existsByClientId(request.clientId))
        }

    }

    @Test
    fun `should return already_exists when key value is already registered`() {

        request = setup.options(
            Database.CLEAN_ALL_AND_REGISTER_PIXKEY,
            Request.VALID_WITH_DOCUMENT_KEY_TYPE,
            Mock.NOT_FOUND_RESPONSE
        )

        val result = grpcClient.create(request)
        println(result)

        with (result) {
            assertTrue(hasFailure())
            assertEquals("6 ALREADY_EXISTS", failure.status)
            assertEquals("keyValue", failure.errorsList[0].field)
            assertEquals("Key value is already registered", failure.errorsList[0].message)
            assertTrue(repository.existsByKeyValue(request.keyValue))
        }

    }


    @Test
    fun `should return internal when erp client call fails`() {

        request = setup.options(
            Database.CLEAN_ALL,
            Request.VALID_WITH_RANDOM_KEY_TYPE,
            Mock.OTHER_ERROR_RESPONSE
        )

        val result = grpcClient.create(request)

        with (result) {
            assertTrue(hasFailure())
            assertEquals("13 INTERNAL", failure.status)
            assertEquals("Something went wrong", failure.errorsList[0].message)
            assertFalse(repository.existsByClientId(request.clientId))
        }

    }

    @MockBean(ErpClient::class)
    fun erpClient() = Mockito.mock(ErpClient::class.java)

    @Factory
    class Clients() {
        @Bean
        fun clientStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel) = newBlockingStub(channel)
    }
}