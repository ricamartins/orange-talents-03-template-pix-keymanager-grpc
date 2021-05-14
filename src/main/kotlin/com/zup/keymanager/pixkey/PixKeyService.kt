package com.zup.keymanager.pixkey

import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.map
import com.github.michaelbull.result.mapBoth
import com.zup.keymanager.extensions.toPixKey
import com.zup.keymanager.extensions.toPixKeyResponse
import com.zup.keymanager.extensions.toPixKeyResult
import com.zup.keymanager.proto.PixKeyRequest
import com.zup.keymanager.proto.PixKeyResult
import com.zup.keymanager.proto.PixKeyServiceGrpc.*
import com.zup.keymanager.validations.BeanValidator
import com.zup.keymanager.validations.PixKeyRequestValidator
import io.grpc.stub.StreamObserver
import javax.inject.Singleton

@Singleton
class PixKeyService(
    private val validator: BeanValidator<PixKeyRequest>,
    private val client: ErpClientHandler,
    private val repository: PixKeyRepository
) : PixKeyServiceImplBase() {

    override fun create(request: PixKeyRequest, observer: StreamObserver<PixKeyResult>) {
        validator.validate(request)
            .andThen(client::getAccountDetails)
            .map(request::toPixKey)
            .map(repository::save)
            .map(::toPixKeyResponse)
            .mapBoth(::toPixKeyResult,::toPixKeyResult)
            .let(observer::onNext).also { observer.onCompleted() }
    }

}
