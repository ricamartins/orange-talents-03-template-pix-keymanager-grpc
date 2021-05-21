package com.zup.keymanager.pixkey.clients

import com.zup.keymanager.extensions.with
import io.grpc.Status
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.MediaType.APPLICATION_XML
import io.micronaut.http.annotation.*
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import javax.inject.Singleton

@Client("\${client.bcb.url}")
interface BcbClient {

    @Post("/keys") @Produces(APPLICATION_XML) @Consumes(APPLICATION_XML)
    fun create(@Body request: BcbCreatePixKeyRequest): HttpResponse<BcbCreatePixKeyRequest>

    @Delete("/keys/{key}") @Produces(APPLICATION_XML) @Consumes(APPLICATION_XML)
    fun delete(@PathVariable key: String, @Body request: BcbDeletePixKeyRequest): HttpResponse<BcbDeletePixKeyRequest>

    @Get("/keys/{key}") @Produces(APPLICATION_XML) @Consumes(APPLICATION_XML)
    fun getKey(@PathVariable key: String): HttpResponse<BcbCreatePixKeyRequest>
}

@Singleton
class BcbClientHandler(private val client: BcbClient) {

    fun create(request: BcbCreatePixKeyRequest): BcbCreatePixKeyRequest {
        try {

            val response = client.create(request)

            if (response.status == HttpStatus.NOT_FOUND)
                throw Status.NOT_FOUND with "Pix key not registered in the central bank"

            return response.body()!!

        } catch (e: HttpClientResponseException) {
            when(e.status) {
                HttpStatus.UNPROCESSABLE_ENTITY -> throw Status.ALREADY_EXISTS with "Pix key already registered at the Central Bank"
                else -> throw Status.INTERNAL with "Failed to get a response from the Central Bank"
            }
        }
    }

    fun delete(request: BcbDeletePixKeyRequest): BcbDeletePixKeyRequest {
        try {

            val response = client.delete(request.key, request)

            if (response.status == HttpStatus.NOT_FOUND)
                throw Status.NOT_FOUND with "Pix key not registered in the central bank"

            return response.body()!!

        } catch (e: HttpClientResponseException) {
            when(e.status) {
                HttpStatus.FORBIDDEN -> throw Status.PERMISSION_DENIED with "Client can not delete this pix key"
                else -> throw Status.INTERNAL with "Failed to get a response from the Central Bank"
            }
        }
    }

    fun getKey(key: String): BcbCreatePixKeyRequest {
        try {

            val response = client.getKey(key)

            if (response.status == HttpStatus.NOT_FOUND)
                throw Status.NOT_FOUND with "Pix key not registered in the central bank"

            return response.body()!!

        } catch (e: HttpClientResponseException) {
            throw Status.INTERNAL with "Failed to get a response from the Central Bank"
        }
    }
}

