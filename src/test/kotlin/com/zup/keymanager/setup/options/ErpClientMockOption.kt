package com.zup.keymanager.setup.options

import com.zup.keymanager.extensions.translate
import com.zup.keymanager.pixkey.AccountDetailsResponse
import com.zup.keymanager.pixkey.ErpClient
import com.zup.keymanager.proto.PixKeyRequest
import io.micronaut.http.HttpResponse
import org.mockito.Mockito

enum class ErpClientMockOption {

    OK_RESPONSE {
        override fun apply(client: ErpClient, request: PixKeyRequest, accountDetailsResponse: AccountDetailsResponse) {
            Mockito.`when`(client.getAccountDetails(request.clientId, request.accountType.translate()))
                .thenReturn(HttpResponse.ok(accountDetailsResponse))
        }
    },
    NOT_FOUND_RESPONSE {
        override fun apply(
            client: ErpClient,
            request: PixKeyRequest,
            accountDetailsResponse: AccountDetailsResponse
        ) {
            Mockito.`when`(client.getAccountDetails(request.clientId, request.accountType.translate()))
                .thenReturn(HttpResponse.notFound())
        }
    },
    BAD_REQUEST_RESPONSE {
        override fun apply(
            client: ErpClient,
            request: PixKeyRequest,
            accountDetailsResponse: AccountDetailsResponse
        ) {
            Mockito.`when`(client.getAccountDetails(request.clientId, request.accountType.translate()))
                .thenReturn(HttpResponse.badRequest())
        }
    },
    NOTHING {
        override fun apply(client: ErpClient, request: PixKeyRequest, accountDetailsResponse: AccountDetailsResponse) {}
    };

    abstract fun apply(client: ErpClient, request: PixKeyRequest, accountDetailsResponse: AccountDetailsResponse): Unit

    fun isChosen() = this != NOTHING
}