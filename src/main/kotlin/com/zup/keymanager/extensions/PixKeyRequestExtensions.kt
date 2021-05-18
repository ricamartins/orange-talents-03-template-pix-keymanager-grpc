package com.zup.keymanager.extensions

import com.zup.keymanager.pixkey.AccountDetailsResponse
import com.zup.keymanager.pixkey.PixKey
import com.zup.keymanager.proto.*
import io.grpc.Status
import java.util.*

fun PixKeyCreateRequest.toPixKey(accountDetailsResponse: AccountDetailsResponse): PixKey {
    return PixKey(
        UUID.randomUUID().toString(),
        clientId,
        if (keyValue.isNullOrBlank()) UUID.randomUUID().toString() else keyValue,
        PixKey.KeyType.valueOf(keyType.name),
        PixKey.AccountType.valueOf(accountType.name),
        accountDetailsResponse.toAccountDetails()
    )
}

fun PixKeyCreateRequest.AccountType.translate(): String {
    return when (this.name) {
        "CHECKING" -> "CONTA_CORRENTE"
        "SAVINGS" -> "CONTA_POUPANCA"
        else -> ""
    }
}

fun toPixKeyCreateResponse(pixKey: PixKey): PixKeyCreateResponse {
    return PixKeyCreateResponse.newBuilder().setClientId(pixKey.clientId).setPixId(pixKey.id).build()
}

fun void(): Void = Void.newBuilder().build()