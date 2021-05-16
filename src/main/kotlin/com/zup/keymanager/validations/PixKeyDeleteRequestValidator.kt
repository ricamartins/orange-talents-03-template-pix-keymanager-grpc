package com.zup.keymanager.validations

import com.zup.keymanager.extensions.with
import com.zup.keymanager.pixkey.PixKeyRepository
import com.zup.keymanager.proto.ErrorResponse
import com.zup.keymanager.proto.PixKeyDeleteRequest
import com.zup.keymanager.validations.annotations.ValidUUID
import io.grpc.Status
import io.micronaut.validation.Validated
import javax.inject.Singleton
import javax.validation.ConstraintViolationException
import javax.validation.constraints.NotBlank

@Validated @Singleton
class PixKeyDeleteRequestValidator(val repository: PixKeyRepository) : CustomStatusValidator<PixKeyDeleteRequest> {

    override fun validateIllegalArguments(target: PixKeyDeleteRequest) {
        validateIllegalArguments(target.clientId, target.pixId)
    }

    override fun customStatusValidations(target: PixKeyDeleteRequest): List<() -> Unit> {
        return listOf(
            { validatePixKeyExists(target.pixId) },
            { validatePixKeyOwner(target.clientId, target.pixId) }
        )
    }

    override fun customErrorMappers(): List<(ConstraintViolationException) -> ErrorResponse> {
        return listOf(
            { Status.NOT_FOUND with ("pixId" to "Pix ID does not exists") },
            { Status.PERMISSION_DENIED with "Pix key does not belong to this client" }
        )
    }

    fun validateIllegalArguments(
        @ValidUUID clientId: String,
        @ValidUUID pixId: String
    ) {}

    fun validatePixKeyExists(pixId: String) {
        if (!repository.existsById(pixId))
            throw ConstraintViolationException("Pix key does not exists", null)
    }

    fun validatePixKeyOwner(clientId: String, pixId: String) {
        repository.findById(pixId).map { pixKey ->
            if (pixKey.clientId != clientId)
                throw ConstraintViolationException("Pix key does not belong to this client", null)
        }
    }

}
