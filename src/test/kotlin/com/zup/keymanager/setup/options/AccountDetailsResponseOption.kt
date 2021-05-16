package com.zup.keymanager.setup.options

import com.zup.keymanager.pixkey.AccountDetailsResponse
import com.zup.keymanager.pixkey.BankDetailsResponse
import com.zup.keymanager.pixkey.OwnerDetailsResponse

enum class AccountDetailsResponseOption {

    ANY {
        override fun apply(): AccountDetailsResponse = account
    };

    abstract fun apply(): AccountDetailsResponse

    private val owner = OwnerDetailsResponse("Rafael M C Ponte")
    private val bank = BankDetailsResponse("ITAÚ UNIBANCO S.A.")
    val account: AccountDetailsResponse = AccountDetailsResponse("0001", "291900", bank, owner)
}