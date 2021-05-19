package com.zup.keymanager.pixkey.clients

import com.fasterxml.jackson.annotation.JsonProperty
import com.zup.keymanager.pixkey.AccountDetails

data class AccountDetailsResponse(
    @JsonProperty("agencia") val branch: String,
    @JsonProperty("numero") val number: String,
    @JsonProperty("instituicao") val bankDetails: BankDetailsResponse,
    @JsonProperty("titular") val ownerDetails: OwnerDetailsResponse
) {
    fun toAccountDetails() = AccountDetails(branch, number, bankDetails.participant, ownerDetails.name)
}

data class BankDetailsResponse(@JsonProperty("ispb") val participant: String)

data class OwnerDetailsResponse(
    @JsonProperty("nome") val name: String,
    @JsonProperty("cpf") val document: String
)
