package com.zup.keymanager.validations

import com.zup.keymanager.extensions.with
import com.zup.keymanager.handler.Validator
import com.zup.keymanager.pixkey.PixKeyRepository
import com.zup.keymanager.proto.PixKeyInfoRequest
import com.zup.keymanager.validations.annotations.ValidUUID
import io.grpc.Status
import io.micronaut.validation.Validated
import javax.inject.Singleton
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Validated @Singleton
class PixKeyInfoRequestValidator(val repository: PixKeyRepository) : Validator<PixKeyInfoRequest> {

    override fun validate(target: PixKeyInfoRequest) {
        if (target.hasInfoPair()) {
            validateInfoPair(target.infoPair.clientId, target.infoPair.pixId)
            validatePixKeyExistsAndOwner(target.infoPair.clientId, target.infoPair.pixId)
        } else {
            validateKeyValue(target.keyValue)
        }
    }

    fun validateKeyValue(@NotBlank @Size(max=77) keyValue: String) {}

    fun validateInfoPair(@ValidUUID clientId: String, @ValidUUID pixId: String) {}

    private fun validatePixKeyExistsAndOwner(clientId: String, pixId: String) {
        repository.findById(pixId).map { pixKey ->
            if (pixKey.clientId != clientId)
                throw Status.PERMISSION_DENIED with "Pix key does not belong to this client"
        }.orElseThrow { Status.NOT_FOUND with "Pix ID does not exists" }
    }


}