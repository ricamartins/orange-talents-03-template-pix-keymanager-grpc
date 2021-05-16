package com.zup.keymanager.pixkey.create

import com.zup.keymanager.proto.PixKeyServiceGrpc.PixKeyServiceBlockingStub
import com.zup.keymanager.setup.PixKeyCreateServiceTestSetup
import com.zup.keymanager.setup.options.PixKeyCreateRequestOption.*
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

@MicronautTest(transactional=false)
class CreatePixKeyValidationTest(
    private val grpcClient: PixKeyServiceBlockingStub,
    private val setup: PixKeyCreateServiceTestSetup
) {

    @Test
    fun `should return error message when client id is invalid UUID`() {

        val request = setup.options(
            requestOption = INVALID_CLIENT_ID_UUID_FORMAT,
        )

        val result = grpcClient.create(request)

        with (result) {
            assertTrue(hasFailure())
            assertEquals("3 INVALID_ARGUMENT", failure.status)
            assertEquals("clientId", failure.errorsList[0].field)
            assertEquals("Must be a valid UUID", failure.errorsList[0].message)
        }
    }


    @Test
    fun `should return error message when key type is null`() {

        val request = setup.options(
            requestOption = INVALID_KEY_TYPE_NULL
        )

        val result = grpcClient.create(request)

        with (result) {
            assertTrue(hasFailure())
            assertEquals("3 INVALID_ARGUMENT", failure.status)
            assertEquals("keyType", failure.errorsList[0].field)
            assertEquals("Must be a valid key type", failure.errorsList[0].message)
        }

    }


    @Test
    fun `should return error message when key value size is greater than 77`() {

        val request = setup.options(
            requestOption = INVALID_KEY_VALUE_GREATER_THAN_77
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

        val request = setup.options(
            requestOption = INVALID_KEY_VALUE_DOCUMENT
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

        val request = setup.options(
            requestOption = INVALID_KEY_VALUE_PHONE
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

        val request = setup.options(
            requestOption = INVALID_KEY_VALUE_EMAIL
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

        val request = setup.options(
            requestOption = INVALID_KEY_VALUE_NOT_BLANK_FOR_RANDOM_TYPE
        )

        val result = grpcClient.create(request)

        with (result) {
            assertTrue(hasFailure())
            assertEquals("3 INVALID_ARGUMENT", failure.status)
            assertEquals("keyValue", failure.errorsList[0].field)
            assertEquals("Must be null or blank", failure.errorsList[0].message)
        }

    }


    @Test
    fun `should return error message when account type is null`() {

        val request = setup.options(
            requestOption = INVALID_ACCOUNT_TYPE_NULL
        )

        val result = grpcClient.create(request)

        with (result) {
            assertTrue(hasFailure())
            assertEquals("3 INVALID_ARGUMENT", failure.status)
            assertEquals("accountType", failure.errorsList[0].field)
            assertEquals("Must be a valid account type", failure.errorsList[0].message)
        }

    }

}