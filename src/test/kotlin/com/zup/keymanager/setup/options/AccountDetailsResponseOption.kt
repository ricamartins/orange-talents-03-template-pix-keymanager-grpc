package com.zup.keymanager.setup.options

import com.zup.keymanager.pixkey.clients.AccountDetailsResponse
import com.zup.keymanager.pixkey.clients.BankDetailsResponse
import com.zup.keymanager.pixkey.clients.OwnerDetailsResponse

enum class AccountDetailsResponseOption {

    ANY {
        override fun apply(): AccountDetailsResponse = account
    };

    abstract fun apply(): AccountDetailsResponse

    private val owner = OwnerDetailsResponse("Rafael M C Ponte", "82742320032")
    private val bank = BankDetailsResponse("60701190")
    val account: AccountDetailsResponse = AccountDetailsResponse("0001", "291900", bank, owner)
}