package com.zup.keymanager.setup.options

import com.zup.keymanager.extensions.toBcbAccountType
import com.zup.keymanager.extensions.toBcbKeyType
import com.zup.keymanager.extensions.toPixKey
import com.zup.keymanager.pixkey.PixKey
import com.zup.keymanager.pixkey.PixKeyRepository
import com.zup.keymanager.pixkey.clients.*
import com.zup.keymanager.proto.PixKeyCreateRequest
import com.zup.keymanager.proto.PixKeyInfoRequest
import io.micronaut.http.HttpResponse
import org.mockito.Mockito
import java.time.LocalDateTime

enum class PixKeyInfoScenarioOption {

    VALID_PIX_KEY_WITH_INFO_PAIR {
        override fun apply(repository: PixKeyRepository, bcbClient: BcbClient): PixKeyInfoRequest {
            val createRequest = PixKeyCreateRequestOption.VALID_WITH_RANDOM_KEY_TYPE.apply()
            val bcbResponse = createRequest.toBcbCreatePixKeyRequest(AccountDetailsResponseOption.ANY.apply())
            val pixKey = repository.save(createRequest.toPixKey(bcbResponse))
            return pixKey.toPixKeyInfoRequestInfoPair()
        }
    },
    VALID_PIX_KEY_WITH_KEY_VALUE {
        override fun apply(repository: PixKeyRepository, bcbClient: BcbClient): PixKeyInfoRequest {
            val createRequest = PixKeyCreateRequestOption.VALID_WITH_DOCUMENT_KEY_TYPE.apply()
            val bcbResponse = createRequest.toBcbCreatePixKeyRequest(AccountDetailsResponseOption.ANY.apply())
            val pixKey = repository.save(createRequest.toPixKey(bcbResponse))
            return pixKey.toPixKeyInfoRequestKeyValue()
        }
    },
    VALID_PIX_KEY_AT_CENTRAL_BANK_WITH_KEY_VALUE {
        override fun apply(repository: PixKeyRepository, bcbClient: BcbClient): PixKeyInfoRequest {
            val createRequest = PixKeyCreateRequestOption.VALID_WITH_DOCUMENT_KEY_TYPE.apply()
            val bcbResponse = createRequest.toBcbCreatePixKeyRequest(AccountDetailsResponseOption.ANY.apply())
            val pixKey = repository.save(createRequest.toPixKey(bcbResponse))
            Mockito.`when`(bcbClient.getKey(pixKey.keyValue)).thenReturn(HttpResponse.ok(bcbResponse))
            repository.deleteById(pixKey.id)
            return pixKey.toPixKeyInfoRequestKeyValue()
        }
    };

    abstract fun apply(repository: PixKeyRepository, bcbClient: BcbClient): PixKeyInfoRequest
}

fun PixKey.toPixKeyInfoRequestInfoPair(): PixKeyInfoRequest {
    return PixKeyInfoRequest.newBuilder()
        .setInfoPair(PixKeyInfoRequest.PixKeyInfoPair.newBuilder()
            .setClientId(this.clientId)
            .setPixId(this.id)
            .build())
        .build()
}

fun PixKey.toPixKeyInfoRequestKeyValue(): PixKeyInfoRequest {
    return PixKeyInfoRequest.newBuilder().setKeyValue(this.keyValue).build()
}

fun PixKeyCreateRequest.toBcbCreatePixKeyRequest(accountDetailsResponse: AccountDetailsResponse): BcbCreatePixKeyRequest {
    return BcbCreatePixKeyRequest(
        this.keyType.toBcbKeyType(),
        keyValue,
        BcbBankAccountRequest(
            accountDetailsResponse.bankDetails.participant,
            accountDetailsResponse.branch,
            accountDetailsResponse.number,
            this.accountType.toBcbAccountType()
        ),
        BcbOwnerRequest(
            BcbOwnerRequest.PersonType.NATURAL_PERSON,
            accountDetailsResponse.ownerDetails.name,
            accountDetailsResponse.ownerDetails.document
        ),
        LocalDateTime.now().toString()
    )
}