package com.zup.keymanager.pixkey

import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.map
import com.github.michaelbull.result.mapBoth
import com.zup.keymanager.extensions.toPixKey
import com.zup.keymanager.extensions.toPixKeyResponse
import com.zup.keymanager.extensions.toPixKeyResult
import com.zup.keymanager.proto.PixKeyDeleteRequest
import com.zup.keymanager.proto.PixKeyRequest
import com.zup.keymanager.proto.PixKeyResult
import com.zup.keymanager.proto.PixKeyServiceGrpc.PixKeyServiceImplBase
import com.zup.keymanager.validations.BeanValidator
import com.zup.keymanager.validations.ValidatorType
import io.grpc.stub.StreamObserver
import io.micronaut.grpc.annotation.GrpcService
import javax.inject.Singleton

@GrpcService
class PixKeyService(
    private val client: ErpClientHandler,
    private val repository: PixKeyRepository,
    @ValidatorType("PixKeyRequest")
    private val createRequestValidator: BeanValidator<PixKeyRequest>,
    @ValidatorType("PixKeyDeleteRequest")
    private val deleteRequestValidator: BeanValidator<PixKeyDeleteRequest>
) : PixKeyServiceImplBase() {

    override fun create(request: PixKeyRequest, observer: StreamObserver<PixKeyResult>) {
        createRequestValidator.validate(request)
            .andThen(client::getAccountDetails)
            .map(request::toPixKey)
            .map(repository::save)
            .map(::toPixKeyResponse)
            .mapBoth(::toPixKeyResult,::toPixKeyResult)
            .let(observer::onNext).also { observer.onCompleted() }
    }

    override fun delete(request: PixKeyDeleteRequest, observer: StreamObserver<PixKeyResult>) {
        deleteRequestValidator.validate(request)
            .map { repository.deleteById(request.pixId) }
            .mapBoth(::toPixKeyResult,::toPixKeyResult)
            .let(observer::onNext).also { observer.onCompleted() }
    }

}
