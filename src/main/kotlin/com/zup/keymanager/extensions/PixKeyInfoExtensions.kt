package com.zup.keymanager.extensions

import com.zup.keymanager.pixkey.Institutions
import com.zup.keymanager.pixkey.PixKey
import com.zup.keymanager.pixkey.clients.BcbCreatePixKeyRequest
import com.zup.keymanager.proto.*

fun toPixKeyInfoResponse(
    bcbResponse: BcbCreatePixKeyRequest,
    idPair: PixKeyInfoRequest.PixKeyInfoPair? = null,
): PixKeyInfoResponse {
    return with(PixKeyInfoResponse.newBuilder()) {
        clientId = idPair?.clientId ?: ""
        pixId = idPair?.pixId ?: ""
        keyValue = bcbResponse.key
        keyType = toResponseKeyType(bcbResponse.keyType)
        owner = OwnerDetails.newBuilder()
            .setName(bcbResponse.owner.name)
            .setDocument(bcbResponse.owner.taxIdNumber).build()
        account = AccountDetails.newBuilder()
            .setName(Institutions.fromNumber(bcbResponse.bankAccount.participant).formattedName)
            .setBranch(bcbResponse.bankAccount.branch)
            .setNumber(bcbResponse.bankAccount.accountNumber)
            .setAccountType(toResponseAccountType(bcbResponse.bankAccount.accountType)).build()
        createdAt = bcbResponse.createdAt
        build()
    }
}

fun toPixKeyInfoResponse(pixKey: PixKey): PixKeyInfoResponse {
    return with(PixKeyInfoResponse.newBuilder()) {
        clientId = pixKey.clientId
        pixId = pixKey.id
        keyValue = pixKey.keyValue
        keyType = toResponseKeyType(pixKey.keyType)
        owner = OwnerDetails.newBuilder()
            .setName(pixKey.owner.name)
            .setDocument(pixKey.owner.document).build()
        account = AccountDetails.newBuilder()
            .setName(Institutions.fromNumber(pixKey.account.participant).formattedName)
            .setBranch(pixKey.account.branch)
            .setNumber(pixKey.account.number)
            .setAccountType(toResponseAccountType(pixKey.accountType)).build()
        createdAt = pixKey.createdAt.toString()
        build()
    }
}

fun toPixKeyInfoResponseShort(pixKey: PixKey): PixKeyInfoResponse {
    return with(PixKeyInfoResponse.newBuilder()) {
        clientId = pixKey.clientId
        pixId = pixKey.id
        keyValue = pixKey.keyValue
        keyType = toResponseKeyType(pixKey.keyType)
        account = AccountDetails.newBuilder()
            .setAccountType(toResponseAccountType(pixKey.accountType)).build()
        createdAt = pixKey.createdAt.toString()
        build()
    }
}

fun toPixKeyListResponse(pixKeys: List<PixKeyInfoResponse>): PixKeyListResponse {
    return PixKeyListResponse.newBuilder().addAllPixKeys(pixKeys).build()
}