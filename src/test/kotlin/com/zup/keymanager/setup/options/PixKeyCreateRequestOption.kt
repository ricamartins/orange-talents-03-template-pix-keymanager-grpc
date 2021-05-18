package com.zup.keymanager.setup.options

import com.zup.keymanager.proto.PixKeyCreateRequest
import java.util.*

enum class PixKeyCreateRequestOption {

    VALID_WITH_DOCUMENT_KEY_TYPE {
        override fun apply(): PixKeyCreateRequest =
            create(keyValue = "82742320032", keyType = PixKeyCreateRequest.KeyType.DOCUMENT)
    },
    VALID_WITH_PHONE_KEY_TYPE {
        override fun apply(): PixKeyCreateRequest =
            create(keyValue = "+5511987654321", keyType = PixKeyCreateRequest.KeyType.PHONE)
    },
    VALID_WITH_EMAIL_KEY_TYPE {
        override fun apply(): PixKeyCreateRequest =
            create(keyValue = "rafael@mail.com", keyType = PixKeyCreateRequest.KeyType.EMAIL)
    },
    VALID_WITH_RANDOM_KEY_TYPE {
        override fun apply(): PixKeyCreateRequest =
            create(keyValue = "", keyType = PixKeyCreateRequest.KeyType.RANDOM)
    },
    INVALID_ALREADY_REGISTERED { //implement
        override fun apply(): PixKeyCreateRequest =
            create(clientId = "c56dfef4ds9 202cefb15789@")
    },
    INVALID_CLIENT_ID_UUID_FORMAT {
        override fun apply(): PixKeyCreateRequest =
            create(clientId = "c56dfef4ds9 202cefb15789@")
    },
    INVALID_CLIENT_ID_BLANK { //maybe not needed
        override fun apply(): PixKeyCreateRequest =
            create(clientId = "")
    },
    INVALID_KEY_TYPE_NULL {
        override fun apply(): PixKeyCreateRequest =
            create(clientId = "c56dfef4-7901-44fb-84e2-a2cefb157891", keyValue = "rafael@mail.com",
                accountType = PixKeyCreateRequest.AccountType.SAVINGS, others = OtherFields.EMPTY)
    },
    INVALID_KEY_VALUE_GREATER_THAN_77 {
        override fun apply(): PixKeyCreateRequest =
            create(keyValue = "123456789012345678901234567890123456789012345678901234567890123456789012345678")
    },
    INVALID_KEY_VALUE_DOCUMENT {
        override fun apply(): PixKeyCreateRequest =
            create(keyType = PixKeyCreateRequest.KeyType.DOCUMENT, keyValue = "abcdefghij")
    },
    INVALID_KEY_VALUE_PHONE {
        override fun apply(): PixKeyCreateRequest =
            create(keyType = PixKeyCreateRequest.KeyType.PHONE, keyValue = "abcdge123456")
    },
    INVALID_KEY_VALUE_EMAIL {
        override fun apply(): PixKeyCreateRequest =
            create(keyType = PixKeyCreateRequest.KeyType.EMAIL, keyValue = "rafael.com")
    },
    INVALID_KEY_VALUE_NOT_BLANK_FOR_RANDOM_TYPE {
        override fun apply(): PixKeyCreateRequest =
            create(keyType = PixKeyCreateRequest.KeyType.RANDOM, keyValue = "random key faiewnlc932h8")
    },
    INVALID_ACCOUNT_TYPE_NULL {
        override fun apply(): PixKeyCreateRequest =
            create(clientId = "c56dfef4-7901-44fb-84e2-a2cefb157891", keyType = PixKeyCreateRequest.KeyType.RANDOM,
                keyValue = "", others = OtherFields.EMPTY)
    };

    abstract fun apply(): PixKeyCreateRequest

    fun create(
        clientId: String? = null,
        keyType: PixKeyCreateRequest.KeyType? = null,
        keyValue: String? = null,
        accountType: PixKeyCreateRequest.AccountType? = null,
        others: OtherFields = OtherFields.ANY_VALID
    ): PixKeyCreateRequest {
        val builder = PixKeyCreateRequest.newBuilder()
        when(others) {
            OtherFields.ANY_VALID -> {
                if (clientId != null) builder.clientId = clientId else builder.clientId = UUID.randomUUID().toString()
                if (keyType != null) builder.keyType = keyType else builder.keyType = PixKeyCreateRequest.KeyType.RANDOM
                if (keyValue != null) builder.keyValue = keyValue else builder.keyValue = ""
                if (accountType != null) builder.accountType = accountType else builder.accountType = PixKeyCreateRequest.AccountType.CHECKING
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