package com.zup.keymanager.setup.options

import com.zup.keymanager.proto.PixKeyRequest
import java.util.*

enum class PixKeyCreateRequestOption {

    VALID_WITH_DOCUMENT_KEY_TYPE {
        override fun apply(): PixKeyRequest =
            create(keyValue = "82742320032", keyType = PixKeyRequest.KeyType.DOCUMENT)
    },
    VALID_WITH_PHONE_KEY_TYPE {
        override fun apply(): PixKeyRequest =
            create(keyValue = "+5511987654321", keyType = PixKeyRequest.KeyType.PHONE)
    },
    VALID_WITH_EMAIL_KEY_TYPE {
        override fun apply(): PixKeyRequest =
            create(keyValue = "rafael@mail.com", keyType = PixKeyRequest.KeyType.EMAIL)
    },
    VALID_WITH_RANDOM_KEY_TYPE {
        override fun apply(): PixKeyRequest =
            create(keyValue = "", keyType = PixKeyRequest.KeyType.RANDOM)
    },
    INVALID_ALREADY_REGISTERED { //implement
        override fun apply(): PixKeyRequest =
            create(clientId = "c56dfef4ds9 202cefb15789@")
    },
    INVALID_CLIENT_ID_UUID_FORMAT {
        override fun apply(): PixKeyRequest =
            create(clientId = "c56dfef4ds9 202cefb15789@")
    },
    INVALID_CLIENT_ID_BLANK { //maybe not needed
        override fun apply(): PixKeyRequest =
            create(clientId = "")
    },
    INVALID_KEY_TYPE_NULL {
        override fun apply(): PixKeyRequest =
            create(clientId = "c56dfef4-7901-44fb-84e2-a2cefb157891", keyValue = "rafael@mail.com",
                accountType = PixKeyRequest.AccountType.SAVINGS, others = OtherFields.EMPTY)
    },
    INVALID_KEY_VALUE_GREATER_THAN_77 {
        override fun apply(): PixKeyRequest =
            create(keyValue = "123456789012345678901234567890123456789012345678901234567890123456789012345678")
    },
    INVALID_KEY_VALUE_DOCUMENT {
        override fun apply(): PixKeyRequest =
            create(keyType = PixKeyRequest.KeyType.DOCUMENT, keyValue = "abcdefghij")
    },
    INVALID_KEY_VALUE_PHONE {
        override fun apply(): PixKeyRequest =
            create(keyType = PixKeyRequest.KeyType.PHONE, keyValue = "abcdge123456")
    },
    INVALID_KEY_VALUE_EMAIL {
        override fun apply(): PixKeyRequest =
            create(keyType = PixKeyRequest.KeyType.EMAIL, keyValue = "rafael.com")
    },
    INVALID_KEY_VALUE_NOT_BLANK_FOR_RANDOM_TYPE {
        override fun apply(): PixKeyRequest =
            create(keyType = PixKeyRequest.KeyType.RANDOM, keyValue = "random key faiewnlc932h8")
    },
    INVALID_ACCOUNT_TYPE_NULL {
        override fun apply(): PixKeyRequest =
            create(clientId = "c56dfef4-7901-44fb-84e2-a2cefb157891", keyType = PixKeyRequest.KeyType.RANDOM,
                keyValue = "", others = OtherFields.EMPTY)
    };

    abstract fun apply(): PixKeyRequest

    fun create(
        clientId: String? = null,
        keyType: PixKeyRequest.KeyType? = null,
        keyValue: String? = null,
        accountType: PixKeyRequest.AccountType? = null,
        others: OtherFields = OtherFields.ANY_VALID
    ): PixKeyRequest {
        val builder = PixKeyRequest.newBuilder()
        when(others) {
            OtherFields.ANY_VALID -> {
                if (clientId != null) builder.clientId = clientId else builder.clientId = UUID.randomUUID().toString()
                if (keyType != null) builder.keyType = keyType else builder.keyType = PixKeyRequest.KeyType.RANDOM
                if (keyValue != null) builder.keyValue = keyValue else builder.keyValue = ""
                if (accountType != null) builder.accountType = accountType else builder.accountType = PixKeyRequest.AccountType.CHECKING
            }
            OtherFields.EMPTY -> {
                if (clientId != null) builder.clientId = clientId
                if (keyType != null) builder.keyType = keyType
                if (keyValue != null) builder.keyValue = keyValue
                if (accountType != null) builder.accountType = accountType
            }
        }
        return builder.build()
    }
}

enum class OtherFields { ANY_VALID, EMPTY }