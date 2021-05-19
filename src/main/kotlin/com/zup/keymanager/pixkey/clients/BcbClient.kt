package com.zup.keymanager.pixkey.clients

import com.zup.keymanager.extensions.with
import io.grpc.Status
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.MediaType.APPLICATION_XML
import io.micronaut.http.annotation.*
import io.micronaut.http.client.annotation.Client
import javax.inject.Singleton

@Client("\${client.bcb.url}")
interface BcbClient {

    @Post("/keys") @Produces(APPLICATION_XML) @Consumes(APPLICATION_XML)
    fun create(@Body request: BcbCreatePixKeyRequest): HttpResponse<BcbCreatePixKeyRequest>

    @Delete("/keys/{key}") @Produces(APPLICATION_XML) @Consumes(APPLICATION_XML)
    fun delete(@PathVariable key: String, @Body request: BcbDeletePixKeyRequest): HttpResponse<BcbDeletePixKeyRequest>

}

@Singleton
class BcbClientHandler(val client: BcbClient) {

    fun create(request: BcbCreatePixKeyRequest): BcbCreatePixKeyRequest {
        return client.create(request).body()!!
    }

    fun delete(request: BcbDeletePixKeyRequest): BcbDeletePixKeyRequest {
        val response = client.delete(request.key, request)

        if (response.status == HttpStatus.NOT_FOUND)
            throw Status.NOT_FOUND with "Pix key not registered in the central bank"

        return response.body()!!
    }


}

