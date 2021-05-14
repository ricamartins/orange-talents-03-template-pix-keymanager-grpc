package com.zup.keymanager.pixkey

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository

@Repository
interface PixKeyRepository : JpaRepository<PixKey, String> {
    fun existsByKeyValue(keyValue: String): Boolean
    fun existsByClientId(clientId: String): Boolean
}