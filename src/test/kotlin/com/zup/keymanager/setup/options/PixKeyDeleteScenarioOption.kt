package com.zup.keymanager.setup.options

import com.zup.keymanager.extensions.toBcbCreatePixKeyRequest
import com.zup.keymanager.extensions.toPixKey
import com.zup.keymanager.pixkey.PixKeyRepository
import com.zup.keymanager.proto.PixKeyDeleteRequest

enum class PixKeyDeleteScenarioOption {

    VALID_REQUEST_FOR_REGISTERED_PIX_KEY {
        override fun apply(repository: PixKeyRepository): PixKeyDeleteRequest {
            val request = PixKeyCreateRequestOption.VALID_WITH_DOCUMENT_KEY_TYPE.apply()
            val bcbrequest = request.toBcbCreatePixKeyRequest(AccountDetailsResponseOption.ANY.apply())
            val pixKey = repository.save(request.toPixKey(bcbrequest))
            return PixKeyDeleteRequestOption.VALID.apply(pixKey)
        }
    },
    INVALID_REQUEST_NOT_OWNER {
        override fun apply(repository: PixKeyRepository): PixKeyDeleteRequest {
            repository.deleteAll()
            val request = PixKeyCreateRequestOption.VALID_WITH_DOCUMENT_KEY_TYPE.apply()
            val bcbRequest = request.toBcbCreatePixKeyRequest(AccountDetailsResponseOption.ANY.apply())
            val pixKey = repository.save(request.toPixKey(bcbRequest))
            return PixKeyDeleteRequestOption.INVALID_NOT_OWNER.apply(pixKey)
        }
    },
    INVALID_REQUEST_NOT_FOUND {
        override fun apply(repository: PixKeyRepository): PixKeyDeleteRequest {
            val request = PixKeyCreateRequestOption.VALID_WITH_DOCUMENT_KEY_TYPE.apply()
            val bcbRequest = request.toBcbCreatePixKeyRequest(AccountDetailsResponseOption.ANY.apply())
            val pixKey = request.toPixKey(bcbRequest)
            return PixKeyDeleteRequestOption.VALID.apply(pixKey)
        }
    };

    abstract fun apply(repository: PixKeyRepository): PixKeyDeleteRequest

    fun isChosen() = this != null
}