package com.zup.keymanager.extensions

import com.zup.keymanager.pixkey.PixKey
import com.zup.keymanager.pixkey.clients.*
import com.zup.keymanager.proto.*
import io.grpc.Status
import java.util.*

fun PixKeyCreateRequest.toPixKey(key: String, accountDetailsResponse: AccountDetailsResponse): PixKey {
    return PixKey(
        UUID.randomUUID().toString(),
        clientId,
        key,
        PixKey.KeyType.valueOf(keyType.name),
        PixKey.AccountType.valueOf(accountType.name),
        accountDetailsResponse.toAccountDetails(),
        accountDetailsResponse.ownerDetails.toOwnerDetails()
    )
}

fun PixKeyCreateRequest.toPixKey(bcbResponse: BcbCreatePixKeyRequest): PixKey {
    return PixKey(
        UUID.randomUUID().toString(),
        clientId,
        bcbResponse.key,
        PixKey.KeyType.valueOf(keyType.name),
        PixKey.AccountType.valueOf(accountType.name),
        bcbResponse.toAccountDetails(),
        bcbResponse.toOwnerDetails()
    )
}

fun PixKeyCreateRequest.toBcbCreatePixKeyRequest(accountDetailsResponse: AccountDetailsResponse): BcbCreatePixKeyRequest {
    return BcbCreatePixKeyRequest(
        this.keyType.toBcbKeyType(),
        keyValue,
        BcbBankAccountRequest(
            accountDetailsResponse.bankDetails.participant,
            accountDetailsResponse.branch,
            accountDetailsResponse.number,
            this.accountType.toBcbAccountType()
        ),
        BcbOwnerRequest(
            BcbOwnerRequest.PersonType.NATURAL_PERSON,
            accountDetailsResponse.ownerDetails.name,
            accountDetailsResponse.ownerDetails.document
        )
    )
}

fun toPixKeyCreateResponse(pixKey: PixKey): PixKeyCreateResponse {
    return PixKeyCreateResponse.newBuilder().setClientId(pixKey.clientId).setPixId(pixKey.id).build()
}

fun void(): Void = Void.newBuilder().build()