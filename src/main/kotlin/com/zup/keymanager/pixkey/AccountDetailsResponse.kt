package com.zup.keymanager.pixkey

data class AccountDetailsResponse(
    val agencia: String,
    val numero: String,
    val instituicao: BankDetailsResponse,
    val titular: OwnerDetailsResponse
) {
    fun toAccountDetails() = AccountDetails(agencia, numero, instituicao.nome, titular.nome)
}

data class BankDetailsResponse(val nome: String)
data class OwnerDetailsResponse(val nome: String)
