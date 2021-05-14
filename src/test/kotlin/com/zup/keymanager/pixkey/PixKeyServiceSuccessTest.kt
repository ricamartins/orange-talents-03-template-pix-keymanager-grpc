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
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito

@MicronautTest(transactional=false)
internal class PixKeyServiceSuccessTest(
    private val repository: PixKeyRepository,
    private val grpcClient: PixKeyServiceBlockingStub,
    private val setup: PixKeyServiceTestSetup
) {

    lateinit var request: PixKeyRequest

    @Test
    fun `should register pix key with document key type`() {

        request = setup.options(
            Database.CLEAN_ALL,
            Request.VALID_WITH_DOCUMENT_KEY_TYPE,
            Mock.OK_RESPONSE
        )

        val result = grpcClient.create(request)

        with (result) {
            assertTrue(hasSuccess())
            assertEquals(request.clientId, success.clientId)
            assertTrue(repository.existsById(success.pixId))
        }
    }

    @Test
    fun `should register pix key with phone key type`() {

        request = setup.options(
            Database.CLEAN_ALL,
            Request.VALID_WITH_PHONE_KEY_TYPE,
            Mock.OK_RESPONSE
        )

        val result = grpcClient.create(request)

        with (result) {
            assertTrue(hasSuccess())
            assertEquals(request.clientId, success.clientId)
            assertTrue(repository.existsById(success.pixId))
        }
    }

    @Test
    fun `should register pix key with email key type`() {

        request = setup.options(
            Database.CLEAN_ALL,
            Request.VALID_WITH_EMAIL_KEY_TYPE,
            Mock.OK_RESPONSE
        )

        val result = grpcClient.create(request)

        with (result) {
            assertTrue(hasSuccess())
            assertEquals(request.clientId, success.clientId)
            assertTrue(repository.existsById(success.pixId))
        }
    }

    @Test
    fun `should register pix key with random key type`() {

        request = setup.options(
            Database.CLEAN_ALL,
            Request.VALID_WITH_RANDOM_KEY_TYPE,
            Mock.OK_RESPONSE
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

    @Factory
    class Clients() {
        @Bean
        fun clientStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel) = newBlockingStub(channel)
    }
}