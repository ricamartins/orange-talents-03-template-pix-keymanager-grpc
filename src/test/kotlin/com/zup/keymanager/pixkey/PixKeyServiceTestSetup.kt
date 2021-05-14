package com.zup.keymanager.pixkey

import com.zup.keymanager.extensions.toPixKey
import com.zup.keymanager.extensions.translate
import com.zup.keymanager.proto.PixKeyRequest
import io.micronaut.http.HttpResponse
import org.mockito.Mockito
import org.mockito.Mockito.*
import javax.inject.Singleton

@Singleton
class PixKeyServiceTestSetup(
    val repository: PixKeyRepository,
    val client: ErpClient
) {

    var owner = OwnerDetailsResponse("Rafael M C Ponte")
    val bank = BankDetailsResponse("ITAÚ UNIBANCO S.A.")
    val account: AccountDetailsResponse = AccountDetailsResponse("0001", "291900", bank, owner)

    fun options(databaseOption: Database, requestOption: Request, mockOption: Mock): PixKeyRequest {
        val request = requestOption.apply()

        if (databaseOption != Database.NOTHING)
            databaseOption.apply(repository, request.toPixKey(account))

        if (mockOption != Mock.NOTHING)
            mockOption.apply(client, request, account)

        return request
    }

    enum class Request {
        VALID_WITH_DOCUMENT_KEY_TYPE {
            override fun apply(): PixKeyRequest {
                return with (PixKeyRequest.newBuilder()) {
                    clientId = "c56dfef4-7901-44fb-84e2-a2cefb157890"
                    keyType = PixKeyRequest.KeyType.DOCUMENT
                    keyValue = "82742320032"
                    accountType = PixKeyRequest.AccountType.CHECKING
                    build()
                }
            }
        },
        VALID_WITH_PHONE_KEY_TYPE {
            override fun apply(): PixKeyRequest {
                return with (PixKeyRequest.newBuilder()) {
                    clientId = "c56dfef4-7901-44fb-84e2-a2cefb157890"
                    keyType = PixKeyRequest.KeyType.PHONE
                    keyValue = "+5511987654321"
                    accountType = PixKeyRequest.AccountType.SAVINGS
                    build()
                }
            }
        },
        VALID_WITH_EMAIL_KEY_TYPE {
            override fun apply(): PixKeyRequest {
                return with (PixKeyRequest.newBuilder()) {
                    clientId = "c56dfef4-7901-44fb-84e2-a2cefb157890"
                    keyType = PixKeyRequest.KeyType.EMAIL
                    keyValue = "rafael@mail.com"
                    accountType = PixKeyRequest.AccountType.CHECKING
                    build()
                }
            }
        }, VALID_WITH_RANDOM_KEY_TYPE {
            override fun apply(): PixKeyRequest {
                return with (PixKeyRequest.newBuilder()) {
                    clientId = "c56dfef4-7901-44fb-84e2-a2cefb157890"
                    keyType = PixKeyRequest.KeyType.RANDOM
                    keyValue = ""
                    accountType = PixKeyRequest.AccountType.SAVINGS
                    build()
                }
            }
        }, INVALID_CLIENT_ID_UUID_FORMAT {
            override fun apply(): PixKeyRequest {
                return with (PixKeyRequest.newBuilder()) {
                    clientId = "c56dfef4ds9 202cefb15789@"
                    keyType = PixKeyRequest.KeyType.RANDOM
                    keyValue = ""
                    accountType = PixKeyRequest.AccountType.SAVINGS
                    build()
                }
            }
        }, INVALID_KEY_TYPE_NULL {
            override fun apply(): PixKeyRequest {
                return with (PixKeyRequest.newBuilder()) {
                    clientId = "c56dfef4-7901-44fb-84e2-a2cefb157891"
                    keyValue = "rafael@mail.com"
                    accountType = PixKeyRequest.AccountType.SAVINGS
                    build()
                }
            }
        }, INVALID_KEY_VALUE_GREATER_THAN_77 {
            override fun apply(): PixKeyRequest {
                return with (PixKeyRequest.newBuilder()) {
                    clientId = "c56dfef4-7901-44fb-84e2-a2cefb157891"
                    keyType = PixKeyRequest.KeyType.RANDOM
                    keyValue = "123456789012345678901234567890123456789012345678901234567890123456789012345678"
                    accountType = PixKeyRequest.AccountType.CHECKING
                    build()
                }
            }
        }, INVALID_KEY_VALUE_DOCUMENT {
            override fun apply(): PixKeyRequest {
                return with (PixKeyRequest.newBuilder()) {
                    clientId = "c56dfef4-7901-44fb-84e2-a2cefb157891"
                    keyType = PixKeyRequest.KeyType.DOCUMENT
                    keyValue = "abcdefghij"
                    accountType = PixKeyRequest.AccountType.SAVINGS
                    build()
                }
            }
        }, INVALID_KEY_VALUE_PHONE {
            override fun apply(): PixKeyRequest {
                return with (PixKeyRequest.newBuilder()) {
                    clientId = "c56dfef4-7901-44fb-84e2-a2cefb157891"
                    keyType = PixKeyRequest.KeyType.PHONE
                    keyValue = "abcdge123456"
                    accountType = PixKeyRequest.AccountType.SAVINGS
                    build()
                }
            }
        }, INVALID_KEY_VALUE_EMAIL {
            override fun apply(): PixKeyRequest {
                return with (PixKeyRequest.newBuilder()) {
                    clientId = "c56dfef4-7901-44fb-84e2-a2cefb157891"
                    keyType = PixKeyRequest.KeyType.EMAIL
                    keyValue = "rafael.com"
                    accountType = PixKeyRequest.AccountType.CHECKING
                    build()
                }
            }
        }, INVALID_KEY_VALUE_NOT_BLANK_FOR_RANDOM_TYPE {
            override fun apply(): PixKeyRequest {
                return with (PixKeyRequest.newBuilder()) {
                    clientId = "c56dfef4-7901-44fb-84e2-a2cefb157891"
                    keyType = PixKeyRequest.KeyType.RANDOM
                    keyValue = "random key faiewnlc932h8"
                    accountType = PixKeyRequest.AccountType.SAVINGS
                    build()
                }
            }
        }, INVALID_ACCOUNT_TYPE_NULL {
            override fun apply(): PixKeyRequest {
                return with (PixKeyRequest.newBuilder()) {
                    clientId = "c56dfef4-7901-44fb-84e2-a2cefb157891"
                    keyType = PixKeyRequest.KeyType.RANDOM
                    keyValue = ""
                    build()
                }
            }
        };

        abstract fun apply(): PixKeyRequest
    }

    enum class Mock {
        OK_RESPONSE {
            override fun apply(client: ErpClient, request: PixKeyRequest, accountDetailsResponse: AccountDetailsResponse) {
                `when`(client.getAccountDetails(request.clientId, request.accountType.translate()))
                    .thenReturn(HttpResponse.ok(accountDetailsResponse))
            }
        }, NOT_FOUND_RESPONSE {
            override fun apply(client: ErpClient, request: PixKeyRequest, accountDetailsResponse: AccountDetailsResponse) {
                `when`(client.getAccountDetails(request.clientId, request.accountType.translate()))
                    .thenReturn(HttpResponse.notFound())
            }
        }, OTHER_ERROR_RESPONSE {
            override fun apply(client: ErpClient, request: PixKeyRequest, accountDetailsResponse: AccountDetailsResponse) {
                `when`(client.getAccountDetails(request.clientId, request.accountType.translate()))
                    .thenReturn(HttpResponse.badRequest())
            }
        }, NOTHING {
            override fun apply(client: ErpClient, request: PixKeyRequest, accountDetailsResponse: AccountDetailsResponse) {}
        };

        abstract fun apply(client: ErpClient, request: PixKeyRequest, accountDetailsResponse: AccountDetailsResponse): Unit
    }

    enum class Database {
        CLEAN_ALL {
            override fun apply(repository: PixKeyRepository, pixKey: PixKey) {
                repository.deleteAll()
            }
        }, CLEAN_ALL_AND_REGISTER_PIXKEY {
            override fun apply(repository: PixKeyRepository, pixKey: PixKey) {
                repository.deleteAll()
                repository.save(pixKey)
            }
        }, NOTHING {
            override fun apply(repository: PixKeyRepository, pixKey: PixKey) {}
        }
        ;

        abstract fun apply(repository: PixKeyRepository, pixKey: PixKey): Unit
    }
}