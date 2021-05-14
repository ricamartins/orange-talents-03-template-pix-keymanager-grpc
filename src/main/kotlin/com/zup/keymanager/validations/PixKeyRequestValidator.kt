package com.zup.keymanager.validations

import com.zup.keymanager.extensions.with
import com.zup.keymanager.proto.ErrorResponse
import com.zup.keymanager.proto.PixKeyRequest
import com.zup.keymanager.proto.PixKeyRequest.KeyType
import io.grpc.Status
import io.micronaut.validation.Validated
import org.hibernate.validator.constraints.br.CPF
import javax.inject.Singleton
import javax.validation.ConstraintViolationException
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Validated @Singleton
class PixKeyRequestValidator : Validator<PixKeyRequest> {

    override fun validateConstructor(target: PixKeyRequest) {
        with(target) { validateWithConstructor(clientId, keyType, keyValue, accountType) }
    }

    override fun validateWithCustomStatus(target: PixKeyRequest) {
        with(target) { validateUniqueKey(keyValue) }
    }

    override fun toCustomError(e: ConstraintViolationException): ErrorResponse {
        return Status.ALREADY_EXISTS with ("keyValue" to "Key value is already registered")
    }

    fun validateWithConstructor(
        @NotBlank @ValidUUID clientId: String,
        @NotNull @ValidKeyType keyType: KeyType,
        @Size(max=77) keyValue: String,
        @NotNull @ValidAccountType accountType: PixKeyRequest.AccountType
    ) {
        when(keyType) {
            KeyType.DOCUMENT -> validateDocument(keyValue)
            KeyType.PHONE -> validatePhone(keyValue)
            KeyType.EMAIL -> validateEmail(keyValue)
            KeyType.RANDOM -> validateRandom(keyValue)
        }
    }

    fun validateDocument(@NotBlank @CPF keyValue: String) {}
    fun validatePhone(@Phone @NotBlank keyValue: String) {}
    fun validateEmail(@NotBlank @Email keyValue: String) {}
    fun validateRandom(@Blank keyValue: String) {}

    fun validateUniqueKey(@Unique keyValue: String) {}
}