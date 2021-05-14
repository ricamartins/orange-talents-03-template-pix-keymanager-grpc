package com.zup.keymanager.pixkey

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.zup.keymanager.extensions.translate
import com.zup.keymanager.extensions.with
import com.zup.keymanager.proto.ErrorResponse
import com.zup.keymanager.proto.PixKeyRequest
import io.grpc.Status
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client
import javax.inject.Singleton

@Client("\${client.erp.url}")
interface ErpClient {

    @Get("/clientes/{clientId}/contas")
    fun getAccountDetails(@PathVariable clientId: String, @QueryValue tipo: String): HttpResponse<AccountDetailsResponse>
}

@Singleton
class ErpClientHandler(private val client: ErpClient) {

    fun getAccountDetails(request: PixKeyRequest): Result<AccountDetailsResponse, ErrorResponse> {
        val response = client.getAccountDetails(request.clientId, request.accountType.translate())
        return when (response.status.name) {
            "OK" -> Ok(response.body.get())
            "NOT_FOUND" -> Err(Status.NOT_FOUND with ("client" to "Client or account does not exists"))
            else -> Err(Status.INTERNAL with "Something went wrong")
        }
    }

}