package com.zup.keymanager.validations

import com.zup.keymanager.extensions.with
import com.zup.keymanager.pixkey.PixKeyRepository
import com.zup.keymanager.proto.PixKeyCreateRequest
import com.zup.keymanager.proto.PixKeyCreateRequest.KeyType
import com.zup.keymanager.validations.annotations.*
import io.grpc.Status
import io.micronaut.validation.Validated
import org.hibernate.validator.constraints.br.CPF
import javax.inject.Singleton
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Validated @Singleton
class PixKeyCreateRequestValidator(val repository: PixKeyRepository) : CustomStatusValidator<PixKeyCreateRequest> {

    override fun validateIllegalArguments(target: PixKeyCreateRequest) {
        validateIllegalArguments(target.clientId, target.keyType, target.keyValue, target.accountType)
    }

    override fun customStatusValidations(target: PixKeyCreateRequest): List<() -> Unit> {
        return listOf { validateUniqueKey(target.keyValue) }
    }

    fun validateIllegalArguments(
        @NotBlank @ValidUUID clientId: String,
        @NotNull @ValidKeyType keyType: KeyType,
        @Size(max=77) keyValue: String,
        @NotNull @ValidAccountType accountType: PixKeyCreateRequest.AccountType
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

    fun validateUniqueKey(keyValue: String) {
        if (repository.existsByKeyValue(keyValue))
            throw Status.ALREADY_EXISTS with "Key value is already registered"
    }

}
