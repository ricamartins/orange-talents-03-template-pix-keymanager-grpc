package com.zup.keymanager.validations

import com.zup.keymanager.extensions.with
import com.zup.keymanager.pixkey.PixKeyRepository
import com.zup.keymanager.proto.PixKeyDeleteRequest
import com.zup.keymanager.validations.annotations.ValidUUID
import io.grpc.Status
import io.micronaut.validation.Validated
import javax.inject.Singleton

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

    fun validateIllegalArguments(@ValidUUID clientId: String, @ValidUUID pixId: String) {}

    fun validatePixKeyExists(pixId: String) {
        if (!repository.existsById(pixId))
            throw Status.NOT_FOUND with "Pix ID does not exists"
    }

    fun validatePixKeyOwner(clientId: String, pixId: String) {
        repository.findById(pixId).map { pixKey ->
            if (pixKey.clientId != clientId)
                throw Status.PERMISSION_DENIED with "Pix key does not belong to this client"
        }
    }

}
