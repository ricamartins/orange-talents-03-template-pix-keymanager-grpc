package com.zup.keymanager.setup.options

import com.zup.keymanager.extensions.toBcbCreatePixKeyRequest
import com.zup.keymanager.extensions.toPixKey
import com.zup.keymanager.pixkey.PixKeyRepository
import com.zup.keymanager.pixkey.clients.BcbCreatePixKeyRequest
import com.zup.keymanager.proto.PixKeyDeleteRequest
import com.zup.keymanager.proto.PixKeyCreateRequest

enum class PixKeyCreateScenarioOption {

    PIX_KEY_CREATE_REQUEST_PIX_KEY_ALREADY_REGISTERED {
        override fun apply(repository: PixKeyRepository): PixKeyCreateRequest {
            repository.deleteAll()
            val request = PixKeyCreateRequestOption.VALID_WITH_DOCUMENT_KEY_TYPE.apply()
            val bcbrequest = request.toBcbCreatePixKeyRequest(AccountDetailsResponseOption.ANY.apply())
            repository.save(request.toPixKey(bcbrequest))
            return request
        }
    },
    NOTHING {
        override fun apply(repository: PixKeyRepository) = null
    };

    abstract fun apply(repository: PixKeyRepository): PixKeyCreateRequest?

    fun isChosen() = this != NOTHING
}