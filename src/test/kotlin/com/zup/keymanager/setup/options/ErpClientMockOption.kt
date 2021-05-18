package com.zup.keymanager.setup.options

import com.zup.keymanager.extensions.translate
import com.zup.keymanager.pixkey.AccountDetailsResponse
import com.zup.keymanager.pixkey.ErpClient
import com.zup.keymanager.proto.PixKeyCreateRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.client.exceptions.HttpClientResponseException
import org.mockito.Mockito

enum class ErpClientMockOption {

    OK_RESPONSE {
        override fun apply(client: ErpClient, request: PixKeyCreateRequest, accountDetailsResponse: AccountDetailsResponse) {
            Mockito.`when`(client.getAccountDetails(request.clientId, request.accountType.translate()))
                .thenReturn(HttpResponse.ok(accountDetailsResponse))
        }
    },
    NOT_FOUND_RESPONSE {
        override fun apply(
            client: ErpClient,
            request: PixKeyCreateRequest,
            accountDetailsResponse: AccountDetailsResponse
        ) {
            Mockito.`when`(client.getAccountDetails(request.clientId, request.accountType.translate()))
                .thenReturn(HttpResponse.notFound())
        }
    },
    BAD_REQUEST_RESPONSE {
        override fun apply(
            client: ErpClient,
            request: PixKeyCreateRequest,
            accountDetailsResponse: AccountDetailsResponse
        ) {
            Mockito.`when`(client.getAccountDetails(request.clientId, request.accountType.translate()))
                .thenThrow(HttpClientResponseException("Bad Request", HttpResponse.badRequest<Any>()))
        }
    },
    NOTHING {
        override fun apply(client: ErpClient, request: PixKeyCreateRequest, accountDetailsResponse: AccountDetailsResponse) {}
    };

    abstract fun apply(client: ErpClient, request: PixKeyCreateRequest, accountDetailsResponse: AccountDetailsResponse): Unit

    fun isChosen() = this != NOTHING
}