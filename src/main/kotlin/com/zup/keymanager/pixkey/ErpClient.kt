package com.zup.keymanager.pixkey

import com.zup.keymanager.extensions.translate
import com.zup.keymanager.extensions.with
import com.zup.keymanager.proto.PixKeyCreateRequest
import io.grpc.Status
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client
import javax.inject.Singleton

@Client("\${client.erp.url}")
interface ErpClient {

    @Get("/clientes/{clientId}/contas")
    fun getAccountDetails(@PathVariable clientId: String, @QueryValue("tipo") type: String): HttpResponse<AccountDetailsResponse>
}

@Singleton
class ErpClientHandler(private val client: ErpClient) {

    fun getAccountDetails(request: PixKeyCreateRequest): AccountDetailsResponse {
        val response = client.getAccountDetails(request.clientId, request.accountType.translate())

        if (response.status == HttpStatus.NOT_FOUND)
            throw Status.NOT_FOUND with "Client or account does not exists"

        return response.body()!!
    }

}