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
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@MicronautTest(transactional=false)
class PixKeyServiceValidationTest(
    private val grpcClient: PixKeyServiceBlockingStub,
    private val setup: PixKeyServiceTestSetup
) {

    lateinit var request: PixKeyRequest

    @Nested
    inner class ClientId {}

    @Test
    fun `should return error message when invalid UUID`() {

        request = setup.options(
            Database.NOTHING,
            Request.INVALID_CLIENT_ID_UUID_FORMAT,
            Mock.NOTHING
        )

        val result = grpcClient.create(request)

        with (result) {
            assertTrue(hasFailure())
            assertEquals("3 INVALID_ARGUMENT", failure.status)
            assertEquals("clientId", failure.errorsList[0].field)
            assertEquals("Must be a valid UUID", failure.errorsList[0].message)
        }
    }

    @Nested
    inner class KeyType {}

    @Test
    fun `should return error message when key type is null`() {

        request = setup.options(
            Database.NOTHING,
            Request.INVALID_KEY_TYPE_NULL,
            Mock.NOTHING
        )

        val result = grpcClient.create(request)

        with (result) {
            assertTrue(hasFailure())
            assertEquals("3 INVALID_ARGUMENT", failure.status)
            assertEquals("keyType", failure.errorsList[0].field)
            assertEquals("Must be a valid key type", failure.errorsList[0].message)
        }

    }

    @Nested
    inner class KeyValue {}

    @Test
    fun `should return error message when key value size greater than 77`() {

        request = setup.options(
            Database.NOTHING,
            Request.INVALID_KEY_VALUE_GREATER_THAN_77,
            Mock.NOTHING
        )

        val result = grpcClient.create(request)

        with (result) {
            assertTrue(hasFailure())
            assertEquals("3 INVALID_ARGUMENT", failure.status)
            assertEquals("keyValue", failure.errorsList[0].field)
            assertEquals("size must be between 0 and 77", failure.errorsList[0].message)
        }

    }

    @Test
    fun `with document key type, should return error message when invalid document`() {

        request = setup.options(
            Database.NOTHING,
            Request.INVALID_KEY_VALUE_DOCUMENT,
            Mock.NOTHING
        )

        val result = grpcClient.create(request)

        with (result) {
            assertTrue(hasFailure())
            assertEquals("3 INVALID_ARGUMENT", failure.status)
            assertEquals("keyValue", failure.errorsList[0].field)
            //to fix: weird message
            assertEquals("must match \"([0-9]{3}[.]?[0-9]{3}[.]?[0-9]{3}-[0-9]{2})|([0-9]{11})\"", failure.errorsList[0].message)
        }

    }

    @Test
    fun `with phone key type, should return error message when invalid phone number`() {

        request = setup.options(
            Database.NOTHING,
            Request.INVALID_KEY_VALUE_PHONE,
            Mock.NOTHING
        )

        val result = grpcClient.create(request)

        with (result) {
            assertTrue(hasFailure())
            assertEquals("3 INVALID_ARGUMENT", failure.status)
            assertEquals("keyValue", failure.errorsList[0].field)
            assertEquals("Must be a valid phone number", failure.errorsList[0].message)
        }

    }

    @Test
    fun `with email key type, should return error message when invalid email`() {

        request = setup.options(
            Database.NOTHING,
            Request.INVALID_KEY_VALUE_EMAIL,
            Mock.NOTHING
        )

        val result = grpcClient.create(request)

        with (result) {
            assertTrue(hasFailure())
            assertEquals("3 INVALID_ARGUMENT", failure.status)
            assertEquals("keyValue", failure.errorsList[0].field)
            assertEquals("must be a well-formed email address", failure.errorsList[0].message)
        }

    }

    @Test
    fun `with random key type, should return error message when key value is not blank or null`() {

        request = setup.options(
            Database.NOTHING,
            Request.INVALID_KEY_VALUE_NOT_BLANK_FOR_RANDOM_TYPE,
            Mock.NOTHING
        )

        val result = grpcClient.create(request)

        with (result) {
            assertTrue(hasFailure())
            assertEquals("3 INVALID_ARGUMENT", failure.status)
            assertEquals("keyValue", failure.errorsList[0].field)
            assertEquals("Must be null or blank", failure.errorsList[0].message)
        }

    }


    @Nested
    inner class AccountType {}

    @Test
    fun `should return error message when account type null`() {

        request = setup.options(
            Database.NOTHING,
            Request.INVALID_ACCOUNT_TYPE_NULL,
            Mock.NOTHING
        )

        val result = grpcClient.create(request)

        with (result) {
            assertTrue(hasFailure())
            assertEquals("3 INVALID_ARGUMENT", failure.status)
            assertEquals("accountType", failure.errorsList[0].field)
            assertEquals("Must be a valid account type", failure.errorsList[0].message)
        }

    }

    @Factory
    class Clients() {
        @Bean
        fun clientStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel) = newBlockingStub(channel)
    }
}