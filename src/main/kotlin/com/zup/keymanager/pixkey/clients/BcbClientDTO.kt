package com.zup.keymanager.pixkey.clients

import com.zup.keymanager.pixkey.AccountDetails
import com.zup.keymanager.pixkey.PixKey

data class BcbCreatePixKeyRequest(
    val keyType: KeyType,
    val key: String,
    val bankAccount: BcbBankAccountRequest,
    val owner: BcbOwnerRequest,
    val createdAt: String? = null
) {

    fun toAccountDetails(): AccountDetails  {
        return AccountDetails(
            bankAccount.branch,
            bankAccount.accountNumber,
            bankAccount.participant,
            owner.name
        )
    }

    enum class KeyType { CPF, CNPJ, PHONE, EMAIL, RANDOM }
}

data class BcbBankAccountRequest(
    val participant: String,
    val branch: String,
    val accountNumber: String,
    val accountType: AccountType
) {
    enum class AccountType { CACC, SVGS }

    companion object  {
        val ITAU_PARTICIPANT = "60701190"
    }
}

data class BcbOwnerRequest(
    val type: PersonType,
    val name: String,
    val taxIdNumber: String
) {
    enum class PersonType { NATURAL_PERSON, LEGAL_PERSON }
}

data class BcbCreatePixKeyResponse(val smth: String)

data class BcbDeletePixKeyRequest(
    val key: String,
    val participant: String,
    val deletedAt: String? = null
) {
    constructor(pixKey: PixKey): this(pixKey.keyValue, pixKey.account.participant)
}

data class BcbDeletePixKeyResponse(val smth: String)