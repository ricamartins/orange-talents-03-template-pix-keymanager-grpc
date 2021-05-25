package com.zup.keymanager.pixkey

import com.zup.keymanager.extensions.*
import com.zup.keymanager.handler.ErrorHandler
//import com.zup.keymanager.handler.Validated
import com.zup.keymanager.pixkey.clients.*
import com.zup.keymanager.proto.*
import com.zup.keymanager.proto.PixKeyServiceGrpc.PixKeyServiceImplBase
import io.grpc.stub.StreamObserver
import io.micronaut.grpc.annotation.GrpcService
import io.micronaut.validation.Validated

@GrpcService @ErrorHandler @Validated
class PixKeyService(
    private val erpClient: ErpClientHandler,
    private val bcbClient: BcbClientHandler,
    private val repository: PixKeyRepository
) : PixKeyServiceImplBase() {

    override fun create(request: PixKeyCreateRequest, observer: StreamObserver<PixKeyCreateResponse>) {
        erpClient.getAccountDetails(request)
            .let { request.toBcbCreatePixKeyRequest(it) }
            .let(bcbClient::create)
            .let(request::toPixKey)
            .let(repository::save)
            .let(::toPixKeyCreateResponse)
            .let(observer::onNext).also { observer.onCompleted() }
    }

    override fun delete(request: PixKeyDeleteRequest, observer: StreamObserver<Void>) {
        repository.findById(request.pixId).get()
            .let(::BcbDeletePixKeyRequest)
            .let(bcbClient::delete)
            .let { repository.deleteById(request.pixId) }
            .let { observer.onNext(void()) }.also { observer.onCompleted() }
    }

    override fun info(request: PixKeyInfoRequest, observer: StreamObserver<PixKeyInfoResponse>) {
        if (request.hasInfoPair()) {
            repository.findById(request.infoPair.pixId).get()
            .let(::toPixKeyInfoResponse)
        } else {
            repository.findByKeyValue(request.keyValue)
            .map(::toPixKeyInfoResponse)
            .orElseGet { bcbClient.getKey(request.keyValue)
            .let(::toPixKeyInfoResponse) }
        }.let(observer::onNext).also { observer.onCompleted() }
    }

    override fun list(request: PixKeyListRequest, observer: StreamObserver<PixKeyListResponse>) {
        repository.findAllByClientId(request.clientId)
            .map(::toPixKeyInfoResponseShort)
            .let(::toPixKeyListResponse)
            .let(observer::onNext).also { observer.onCompleted() }
    }

}