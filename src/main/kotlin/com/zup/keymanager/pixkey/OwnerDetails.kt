package com.zup.keymanager.pixkey

import javax.persistence.Embeddable

@Embeddable
class OwnerDetails(val name: String, val document: String)