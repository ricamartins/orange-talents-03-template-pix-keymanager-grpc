package com.zup.keymanager.pixkey

import javax.persistence.Embeddable

@Embeddable
class AccountDetails(
    val branch: String,
    val number: String,
    val bankName: String,
    val ownerName: String
)
