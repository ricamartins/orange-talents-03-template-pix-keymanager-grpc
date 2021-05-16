package com.zup.keymanager.setup.options

import com.zup.keymanager.pixkey.PixKey
import com.zup.keymanager.proto.PixKeyDeleteRequest
import java.util.*

enum class PixKeyDeleteRequestOption {

    VALID {
        override fun apply(pixKey: PixKey): PixKeyDeleteRequest {
            return create(pixKey.clientId, pixKey.id)
        }
    }, INVALID_NOT_OWNER {
        override fun apply(pixKey: PixKey): PixKeyDeleteRequest {
            return create(UUID.randomUUID().toString(), pixKey.id)
        }
    },
    NOTHING {
        override fun apply(pixKey: PixKey) = null
    };

    abstract fun apply(pixKey: PixKey): PixKeyDeleteRequest?

    fun create(clientId: String, pixId: String): PixKeyDeleteRequest {
        return with(PixKeyDeleteRequest.newBuilder()) {
            this.clientId = clientId
            this.pixId = pixId
            build()
        }
    }
}