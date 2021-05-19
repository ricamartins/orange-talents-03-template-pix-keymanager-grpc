package com.zup.keymanager.pixkey

import com.zup.keymanager.extensions.toBcbCreatePixKeyRequest
import com.zup.keymanager.extensions.toPixKey
import com.zup.keymanager.extensions.toPixKeyCreateResponse
import com.zup.keymanager.extensions.void
import com.zup.keymanager.pixkey.clients.BcbClientHandler
import com.zup.keymanager.pixkey.clients.BcbCreatePixKeyRequest
import com.zup.keymanager.pixkey.clients.BcbDeletePixKeyRequest
import com.zup.keymanager.pixkey.clients.ErpClientHandler
import com.zup.keymanager.proto.PixKeyCreateRequest
import com.zup.keymanager.proto.PixKeyCreateResponse
import com.zup.keymanager.proto.PixKeyDeleteRequest
import com.zup.keymanager.proto.PixKeyServiceGrpc.PixKeyServiceImplBase
import com.zup.keymanager.proto.Void
import com.zup.keymanager.validations.BeanValidator
import com.zup.keymanager.validations.ValidatorType
import io.grpc.stub.StreamObserver
import io.micronaut.grpc.annotation.GrpcService

@GrpcService
class PixKeyService(
    private val erpClient: ErpClientHandler,
    private val bcbClient: BcbClientHandler,
    private val repository: PixKeyRepository,
    @ValidatorType("PixKeyCreateRequest")
    private val createRequestValidator: BeanValidator<PixKeyCreateRequest>,
    @ValidatorType("PixKeyDeleteRequest")
    private val deleteRequestValidator: BeanValidator<PixKeyDeleteRequest>
) : PixKeyServiceImplBase() {

    override fun create(request: PixKeyCreateRequest, observer: StreamObserver<PixKeyCreateResponse>) {
        serviceHandler(observer) {
            createRequestValidator.validate(request)
                .let(erpClient::getAccountDetails)
                .let { request.toBcbCreatePixKeyRequest(it) }
                .let(bcbClient::create)
                .let(request::toPixKey)
                .let(repository::save)
                .let(::toPixKeyCreateResponse)
        }
    }

    override fun delete(request: PixKeyDeleteRequest, observer: StreamObserver<Void>) {
        serviceHandler(observer) {
            deleteRequestValidator.validate(request)
                .let { repository.findById(it.pixId).get() }
                .let(::BcbDeletePixKeyRequest)
                .let(bcbClient::delete)
                .let { repository.deleteById(request.pixId) }
                void()
        }
    }

}
