package com.zup.keymanager.setup

import com.fasterxml.jackson.databind.ObjectMapper
import com.zup.keymanager.extensions.formatted
import com.zup.keymanager.proto.*
import com.zup.keymanager.proto.PixKeyResult.Failure
import com.zup.keymanager.proto.PixKeyResult.Success
import com.zup.keymanager.proto.PixKeyServiceGrpc.*
import io.grpc.Status
import io.grpc.StatusRuntimeException
import javax.inject.Singleton

@Singleton
class GrpcClientHandler(private val client: PixKeyServiceBlockingStub) {

    fun create(request: PixKeyCreateRequest): PixKeyResult = runAndConvert { client.create(request) }
    fun delete(request: PixKeyDeleteRequest): PixKeyResult = runAndConvert { client.delete(request) }
    fun info(request: PixKeyInfoRequest): PixKeyResult = runAndConvert { client.info(request) }
    fun list(request: PixKeyListRequest): PixKeyResult = runAndConvert { client.list(request) }

    private fun <R> runAndConvert(call: () -> R): PixKeyResult {
        return with(PixKeyResult.newBuilder()) {
            try {
                val response = call.invoke()
                status = "0 OK"
                when (response) {
                    is PixKeyCreateResponse -> success = Success.newBuilder().setCreateResponse(response).build()
                    is PixKeyInfoResponse -> success = Success.newBuilder().setInfoResponse(response).build()
                    is PixKeyListResponse -> success = Success.newBuilder().setListResponse(response).build()
                    is Void -> success = Success.newBuilder().build()
                }
            } catch (e: StatusRuntimeException) {
                status = e.status.formatted()
                failure = if (e.status.code == Status.INVALID_ARGUMENT.code) {
                    Failure.newBuilder().addAllErrors(e.status.description?.toFieldErrors()).build()
                }else
                    Failure.newBuilder().setMessage(e.status.description).build()
            }
            build()
        }
    }

}

fun String.toFieldErrors(): List<Failure.FieldError> {
    return this.to<List<String>>().map { it.split(": ") }.map {
        with(Failure.FieldError.newBuilder()) {
            field = it[0]
            message = it[1]
            build()
        }
    }
}

inline fun <reified T> String.to(): T = ObjectMapper().readValue(this, T::class.java)