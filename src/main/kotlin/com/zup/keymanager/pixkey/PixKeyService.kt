package com.zup.keymanager.pixkey

import com.zup.keymanager.extensions.toBcbCreatePixKeyRequest
import com.zup.keymanager.extensions.toPixKey
import com.zup.keymanager.extensions.toPixKeyCreateResponse
import com.zup.keymanager.extensions.void
import com.zup.keymanager.handler.ErrorHandler
import com.zup.keymanager.pixkey.clients.BcbClientHandler
import com.zup.keymanager.pixkey.clients.BcbDeletePixKeyRequest
import com.zup.keymanager.pixkey.clients.ErpClientHandler
import com.zup.keymanager.proto.PixKeyCreateRequest
import com.zup.keymanager.proto.PixKeyCreateResponse
import com.zup.keymanager.proto.PixKeyDeleteRequest
import com.zup.keymanager.proto.PixKeyServiceGrpc.PixKeyServiceImplBase
import com.zup.keymanager.proto.Void
import io.grpc.stub.StreamObserver
import io.micronaut.grpc.annotation.GrpcService

@GrpcService @ErrorHandler
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

}
