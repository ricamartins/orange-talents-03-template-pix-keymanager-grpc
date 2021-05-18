package com.zup.keymanager.pixkey

import com.zup.keymanager.extensions.toStatusException
import io.grpc.stub.StreamObserver

fun <R> serviceHandler(observer: StreamObserver<R>, task: () -> R) {
    runCatching {
        task.invoke()
    }.onSuccess { response -> observer.onNext(response).also { observer.onCompleted() }
    }.onFailure { error -> error.toStatusException().let(observer::onError) }
}