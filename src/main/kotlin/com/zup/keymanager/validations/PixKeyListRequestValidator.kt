package com.zup.keymanager.validations

import com.zup.keymanager.extensions.with
import com.zup.keymanager.handler.Validator
import com.zup.keymanager.pixkey.PixKeyRepository
import com.zup.keymanager.proto.PixKeyListRequest
import com.zup.keymanager.validations.annotations.ValidUUID
import io.grpc.Status
import io.micronaut.validation.Validated
import javax.inject.Singleton

@Validated @Singleton
class PixKeyListRequestValidator(val repository: PixKeyRepository): Validator<PixKeyListRequest> {

    override fun validate(target: PixKeyListRequest) {
        validateClientId(target.clientId)
    }

    fun validateClientId(@ValidUUID clientId: String) {}

}