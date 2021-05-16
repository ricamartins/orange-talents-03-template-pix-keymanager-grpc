package com.zup.keymanager.pixkey

import com.fasterxml.jackson.annotation.JsonProperty

data class AccountDetailsResponse(
    @JsonProperty("agencia") val branch: String,
    @JsonProperty("numero") val number: String,
    @JsonProperty("instituicao") val bankDetails: BankDetailsResponse,
    @JsonProperty("titular") val ownerDetails: OwnerDetailsResponse
) {
    fun toAccountDetails() = AccountDetails(branch, number, bankDetails.nome, ownerDetails.nome)
}

data class BankDetailsResponse(val nome: String)
data class OwnerDetailsResponse(val nome: String)
