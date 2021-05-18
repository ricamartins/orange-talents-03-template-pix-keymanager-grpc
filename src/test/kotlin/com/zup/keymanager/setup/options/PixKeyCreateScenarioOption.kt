package com.zup.keymanager.setup.options

import com.zup.keymanager.extensions.toPixKey
import com.zup.keymanager.pixkey.PixKeyRepository
import com.zup.keymanager.proto.PixKeyDeleteRequest
import com.zup.keymanager.proto.PixKeyCreateRequest

enum class PixKeyCreateScenarioOption {

    PIX_KEY_CREATE_REQUEST_PIX_KEY_ALREADY_REGISTERED {
        override fun apply(repository: PixKeyRepository): PixKeyCreateRequest? {
            repository.deleteAll()
            val request = PixKeyCreateRequestOption.VALID_WITH_DOCUMENT_KEY_TYPE.apply()
            repository.save(request.toPixKey(AccountDetailsResponseOption.ANY.apply()))
            return request
        }
    },
    NOTHING {
        override fun apply(repository: PixKeyRepository) = null
    };

    abstract fun apply(repository: PixKeyRepository): PixKeyCreateRequest?

    fun isChosen() = this != NOTHING
}