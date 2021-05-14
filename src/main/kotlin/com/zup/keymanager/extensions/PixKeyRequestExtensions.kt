package com.zup.keymanager.extensions

import com.zup.keymanager.pixkey.AccountDetailsResponse
import com.zup.keymanager.pixkey.PixKey
import com.zup.keymanager.proto.ErrorResponse
import com.zup.keymanager.proto.PixKeyRequest
import com.zup.keymanager.proto.PixKeyResponse
import com.zup.keymanager.proto.PixKeyResult
import java.util.*

fun PixKeyRequest.toPixKey(accountDetailsResponse: AccountDetailsResponse): PixKey {
    return PixKey(
        UUID.randomUUID().toString(),
        clientId,
        if (keyValue.isNullOrBlank()) UUID.randomUUID().toString() else keyValue,
        PixKey.KeyType.valueOf(keyType.name),
        PixKey.AccountType.valueOf(accountType.name),
        accountDetailsResponse.toAccountDetails()
    )
}

fun PixKeyRequest.AccountType.translate(): String {
    return when (this.name) {
        "CHECKING" -> "CONTA_CORRENTE"
        "SAVINGS" -> "CONTA_POUPANCA"
        else -> ""
    }
}

fun toPixKeyResponse(pixKey: PixKey): PixKeyResponse {
    return PixKeyResponse.newBuilder().setClientId(pixKey.clientId).setPixId(pixKey.id).build()
}

fun toPixKeyResult(pixKeyResponse: PixKeyResponse): PixKeyResult {
    return PixKeyResult.newBuilder().setSuccess(pixKeyResponse).build()
}

fun toPixKeyResult(errorResponse: ErrorResponse): PixKeyResult {
    return PixKeyResult.newBuilder().setFailure(errorResponse).build()
}