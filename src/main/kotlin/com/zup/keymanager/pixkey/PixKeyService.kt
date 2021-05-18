package com.zup.keymanager.pixkey

import com.zup.keymanager.extensions.*
import com.zup.keymanager.proto.*
import com.zup.keymanager.proto.PixKeyServiceGrpc.PixKeyServiceImplBase
import com.zup.keymanager.validations.BeanValidator
import com.zup.keymanager.validations.ValidatorType
import io.grpc.stub.StreamObserver
import io.micronaut.grpc.annotation.GrpcService

@GrpcService
class PixKeyService(
    private val client: ErpClientHandler,
    private val repository: PixKeyRepository,
    @ValidatorType("PixKeyCreateRequest")
    private val createRequestValidator: BeanValidator<PixKeyCreateRequest>,
    @ValidatorType("PixKeyDeleteRequest")
    private val deleteRequestValidator: BeanValidator<PixKeyDeleteRequest>
) : PixKeyServiceImplBase() {

    override fun create(request: PixKeyCreateRequest, observer: StreamObserver<PixKeyCreateResponse>) {
        serviceHandler(observer) {
            createRequestValidator.validate(request)
                .let(client::getAccountDetails)
                .let(request::toPixKey)
                .let(repository::save)
                .let(::toPixKeyCreateResponse)
        }
    }

    override fun delete(request: PixKeyDeleteRequest, observer: StreamObserver<Void>) {
        serviceHandler(observer) {
            deleteRequestValidator.validate(request)
                .let { repository.deleteById(request.pixId) }
                void()
        }
    }

}
