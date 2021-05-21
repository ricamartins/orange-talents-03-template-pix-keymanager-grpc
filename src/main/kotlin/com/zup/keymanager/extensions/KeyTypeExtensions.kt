package com.zup.keymanager.extensions

import com.zup.keymanager.pixkey.PixKey
import com.zup.keymanager.pixkey.clients.BcbCreatePixKeyRequest
import com.zup.keymanager.proto.KeyType
import io.grpc.Status

fun KeyType.toBcbKeyType(): BcbCreatePixKeyRequest.KeyType {
    return when(this) {
        KeyType.DOCUMENT -> BcbCreatePixKeyRequest.KeyType.CPF
        KeyType.PHONE -> BcbCreatePixKeyRequest.KeyType.PHONE
        KeyType.EMAIL -> BcbCreatePixKeyRequest.KeyType.EMAIL
        KeyType.RANDOM -> BcbCreatePixKeyRequest.KeyType.RANDOM
        else -> throw Status.ABORTED with "Validation process went wrong"
    }
}

fun toResponseKeyType(keyType: PixKey.KeyType): KeyType {
    return KeyType.valueOf(keyType.name)
}

fun toResponseKeyType(keyType: BcbCreatePixKeyRequest.KeyType): KeyType {
    return when(keyType) {
        BcbCreatePixKeyRequest.KeyType.CPF -> KeyType.DOCUMENT
        BcbCreatePixKeyRequest.KeyType.PHONE -> KeyType.PHONE
        BcbCreatePixKeyRequest.KeyType.EMAIL -> KeyType.EMAIL
        BcbCreatePixKeyRequest.KeyType.RANDOM -> KeyType.RANDOM
        else -> throw Status.INTERNAL with "Unknown key type from central bank"
    }
}