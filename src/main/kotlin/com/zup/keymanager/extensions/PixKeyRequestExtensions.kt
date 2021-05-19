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
        accountDetailsResponse.toAccountDetails()
    )
}

fun PixKeyCreateRequest.toPixKey(bcbResponse: BcbCreatePixKeyRequest): PixKey {
    return PixKey(
        UUID.randomUUID().toString(),
        clientId,
        bcbResponse.key,
        PixKey.KeyType.valueOf(keyType.name),
        PixKey.AccountType.valueOf(accountType.name),
        bcbResponse.toAccountDetails()
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

fun PixKeyCreateRequest.KeyType.toBcbKeyType(): BcbCreatePixKeyRequest.KeyType {
    return when(this) {
        PixKeyCreateRequest.KeyType.DOCUMENT -> BcbCreatePixKeyRequest.KeyType.CPF
        PixKeyCreateRequest.KeyType.PHONE -> BcbCreatePixKeyRequest.KeyType.PHONE
        PixKeyCreateRequest.KeyType.EMAIL -> BcbCreatePixKeyRequest.KeyType.EMAIL
        PixKeyCreateRequest.KeyType.RANDOM -> BcbCreatePixKeyRequest.KeyType.RANDOM
        else -> throw Status.ABORTED with "Validation process went wrong"
    }
}

fun PixKeyCreateRequest.AccountType.toBcbAccountType(): BcbBankAccountRequest.AccountType {
    return when(this) {
        PixKeyCreateRequest.AccountType.CHECKING -> BcbBankAccountRequest.AccountType.CACC
        PixKeyCreateRequest.AccountType.SAVINGS -> BcbBankAccountRequest.AccountType.SVGS
        else -> throw Status.ABORTED with "Validation process went wrong"
    }
}

fun PixKeyCreateRequest.AccountType.translate(): String {
    return when (this) {
        PixKeyCreateRequest.AccountType.CHECKING -> "CONTA_CORRENTE"
        PixKeyCreateRequest.AccountType.SAVINGS -> "CONTA_POUPANCA"
        else -> throw Status.ABORTED with "Validation process went wrong"
    }
}

fun toPixKeyCreateResponse(pixKey: PixKey): PixKeyCreateResponse {
    return PixKeyCreateResponse.newBuilder().setClientId(pixKey.clientId).setPixId(pixKey.id).build()
}

fun void(): Void = Void.newBuilder().build()
