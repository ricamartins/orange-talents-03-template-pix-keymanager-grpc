package com.zup.keymanager.pixkey.create

import com.zup.keymanager.setup.GrpcClientHandler
import com.zup.keymanager.setup.ProtoAnnotatorExtension
import com.zup.keymanager.setup.options.PixKeyCreateRequestOption.*
import com.zup.keymanager.validations.annotations.ValidKeyValue
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@MicronautTest(transactional=false)
@ExtendWith(ProtoAnnotatorExtension::class)
class CreatePixKeyValidationTest(
    private val grpcClient: GrpcClientHandler,
) {

    @Test
    fun `should return error message when client id is invalid UUID`() {

        val request = INVALID_CLIENT_ID_UUID_FORMAT.apply()

        val result = grpcClient.create(request)

        with (result) {
            assertTrue(hasFailure())
            assertEquals("3 INVALID_ARGUMENT", status)
            println(failure.errorsList)
            assertEquals("clientId", failure.errorsList[0].field)
            assertEquals("Must be a valid UUID", failure.errorsList[0].message)
        }
    }


    @Test
    fun `should return error message when key type is null`() {

        val request = INVALID_KEY_TYPE_NULL.apply()

        val result = grpcClient.create(request)

        with (result) {
            assertTrue(hasFailure())
            assertEquals("3 INVALID_ARGUMENT", status)
            assertEquals("keyType", failure.errorsList[0].field)
            assertEquals("Must be a valid key type", failure.errorsList[0].message)
        }

    }


    @Test
    fun `should return error message when key value size is greater than 77`() {

        val request = INVALID_KEY_VALUE_GREATER_THAN_77.apply()

        val result = grpcClient.create(request)

        with (result) {
            assertTrue(hasFailure())
            assertEquals("3 INVALID_ARGUMENT", status)
            assertEquals("keyValue", failure.errorsList[0].field)
            assertEquals("tamanho deve ser entre 0 e 77", failure.errorsList[0].message)
        }

    }

    @Test
    fun `with document key type, should return error message when invalid document`() {

        val request = INVALID_KEY_VALUE_DOCUMENT.apply()

        val result = grpcClient.create(request)

        with (result) {
            assertTrue(hasFailure())
            assertEquals("3 INVALID_ARGUMENT", status)
            assertEquals("keyValue", failure.errorsList[0].field)
            assertEquals("número do registro de contribuinte individual brasileiro (CPF) inválido", failure.errorsList[0].message)
//            using micronaut validator
//            assertEquals("must match \"([0-9]{3}[.]?[0-9]{3}[.]?[0-9]{3}-[0-9]{2})|([0-9]{11})\"", failure.errorsList[0].message)
        }

    }

    @Test
    fun `with phone key type, should return error message when invalid phone number`() {

        val request = INVALID_KEY_VALUE_PHONE.apply()

        val result = grpcClient.create(request)

        with (result) {
            assertTrue(hasFailure())
            assertEquals("3 INVALID_ARGUMENT", status)
            assertEquals("keyValue", failure.errorsList[0].field)
            assertEquals("Must be a valid phone number", failure.errorsList[0].message)
        }

    }

    @Test
    fun `with email key type, should return error message when invalid email`() {

        val request = INVALID_KEY_VALUE_EMAIL.apply()

        val result = grpcClient.create(request)

        with (result) {
            assertTrue(hasFailure())
            assertEquals("3 INVALID_ARGUMENT", status)
            assertEquals("keyValue", failure.errorsList[0].field)
            assertEquals("deve ser um endereço de e-mail bem formado", failure.errorsList[0].message)
//            assertEquals("must be a well-formed email address", failure.errorsList[0].message)
        }

    }

    @Test
    fun `with random key type, should return error message when key value is not blank or null`() {

//        annotateProtoClasses()

        val request = INVALID_KEY_VALUE_NOT_BLANK_FOR_RANDOM_TYPE.apply()

        val result = grpcClient.create(request)

        request::class.java.declaredMethods.filter { it.isAnnotationPresent(ValidKeyValue::class.java) }
            .forEach(::println)

        with (result) {
            assertTrue(hasFailure())
            assertEquals("3 INVALID_ARGUMENT", status)
            assertEquals("keyValue", failure.errorsList[0].field)
            assertEquals("Must be null or blank", failure.errorsList[0].message)
        }

    }


    @Test
    fun `should return error message when account type is null`() {

        val request = INVALID_ACCOUNT_TYPE_NULL.apply()

        val result = grpcClient.create(request)

        with (result) {
            assertTrue(hasFailure())
            assertEquals("3 INVALID_ARGUMENT", status)
            assertEquals("accountType", failure.errorsList[0].field)
            assertEquals("Must be a valid account type", failure.errorsList[0].message)
        }

    }

}