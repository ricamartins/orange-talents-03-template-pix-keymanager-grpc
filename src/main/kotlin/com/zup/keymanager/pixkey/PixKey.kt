package com.zup.keymanager.pixkey

import javax.persistence.*

@Entity
@Table(name="tb_pixkeys")
class PixKey(
    @field:Id val id: String,
    val clientId: String,
    val keyValue: String,
    @field:Enumerated(EnumType.STRING) val keyType: KeyType,
    @field:Enumerated(EnumType.STRING) val accountType: AccountType,
    @field:Embedded val account: AccountDetails
) {

    enum class AccountType { SAVINGS, CHECKING }
    enum class KeyType { DOCUMENT, PHONE, EMAIL, RANDOM }
}
