package com.zup.keymanager.extensions

import com.zup.keymanager.pixkey.PixKey
import com.zup.keymanager.pixkey.clients.BcbBankAccountRequest
import com.zup.keymanager.proto.AccountType
import io.grpc.Status


fun AccountType.toBcbAccountType(): BcbBankAccountRequest.AccountType {
    return when(this) {
        AccountType.CHECKING -> BcbBankAccountRequest.AccountType.CACC
        AccountType.SAVINGS -> BcbBankAccountRequest.AccountType.SVGS
        else -> throw Status.ABORTED with "Validation process went wrong"
    }
}

fun AccountType.translate(): String {
    return when (this) {
        AccountType.CHECKING -> "CONTA_CORRENTE"
        AccountType.SAVINGS -> "CONTA_POUPANCA"
        else -> throw Status.ABORTED with "Validation process went wrong"
    }
}

fun toResponseAccountType(accountType: PixKey.AccountType): AccountType {
    return AccountType.valueOf(accountType.name)
}

fun toResponseAccountType(accountType: BcbBankAccountRequest.AccountType): AccountType {
    return when(accountType) {
        BcbBankAccountRequest.AccountType.SVGS -> AccountType.SAVINGS
        BcbBankAccountRequest.AccountType.CACC -> AccountType.CHECKING
    }
}