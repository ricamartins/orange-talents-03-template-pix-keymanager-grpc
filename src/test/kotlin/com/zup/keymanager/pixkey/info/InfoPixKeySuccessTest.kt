package com.zup.keymanager.pixkey.info

import com.zup.keymanager.extensions.toBcbCreatePixKeyRequest
import com.zup.keymanager.extensions.toPixKey
import com.zup.keymanager.pixkey.PixKeyRepository
import com.zup.keymanager.pixkey.clients.BcbClient
import com.zup.keymanager.proto.PixKeyCreateRequest
import com.zup.keymanager.proto.PixKeyInfoRequest
import com.zup.keymanager.setup.GrpcClientHandler
import com.zup.keymanager.setup.ProtoAnnotatorExtension
import com.zup.keymanager.setup.options.AccountDetailsResponseOption
import com.zup.keymanager.setup.options.PixKeyCreateRequestOption
import com.zup.keymanager.setup.options.PixKeyInfoScenarioOption
import com.zup.keymanager.setup.options.PixKeyInfoScenarioOption.*
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.mockito.Mockito.`when`

@MicronautTest(transactional = false)
@ExtendWith(ProtoAnnotatorExtension::class)
class InfoPixKeySuccessTest(
    private val repository: PixKeyRepository,
    private val grpcClient: GrpcClientHandler,
    private val bcbClient: BcbClient
) {

    @BeforeEach
    fun cleanUp() { repository.deleteAll() }


    @Test
    fun `should return pix key info when pix key is valid and info pair is provided`() {

        val request = VALID_PIX_KEY_WITH_INFO_PAIR.apply(repository, bcbClient)

        val result = grpcClient.info(request)

        with(result) {
            assertTrue(hasSuccess())
            assertEquals(request.infoPair.clientId, success.infoResponse.clientId)
            assertEquals(request.infoPair.pixId, success.infoResponse.pixId)
            assertNotNull(success.infoResponse.keyType)
            assertTrue(success.infoResponse.keyValue.isNotBlank())
        }
    }

    @Test
    fun `should return pix key info when pix key exists in our database and key value is provided`() {

        val request = VALID_PIX_KEY_WITH_KEY_VALUE.apply(repository, bcbClient)

        val result = grpcClient.info(request)

        with(result) {
            assertTrue(hasSuccess())
            assertTrue(success.infoResponse.clientId.isNotBlank())
            assertTrue(success.infoResponse.pixId.isNotBlank())
            assertNotNull(success.infoResponse.keyType)
            assertTrue(success.infoResponse.keyValue.isNotBlank())
        }
    }

    @Test
    fun `should return pix key info when pix key exists in central bank and key value is provided`() {

        val request = VALID_PIX_KEY_AT_CENTRAL_BANK_WITH_KEY_VALUE.apply(repository, bcbClient)

        val result = grpcClient.info(request)

        with(result) {
            assertTrue(hasSuccess())
            assertTrue(success.infoResponse.clientId.isBlank())
            assertTrue(success.infoResponse.pixId.isBlank())
            assertNotNull(success.infoResponse.keyType)
            assertTrue(success.infoResponse.keyValue.isNotBlank())
        }
    }

    @MockBean(BcbClient::class)
    fun bcbClient() = Mockito.mock(BcbClient::class.java)

}