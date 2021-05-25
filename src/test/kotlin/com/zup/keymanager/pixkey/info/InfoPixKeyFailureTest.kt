package com.zup.keymanager.pixkey.info

import com.zup.keymanager.pixkey.PixKeyRepository
import com.zup.keymanager.pixkey.clients.BcbClient
import com.zup.keymanager.setup.GrpcClientHandler
import com.zup.keymanager.setup.ProtoAnnotatorExtension
import com.zup.keymanager.setup.options.PixKeyInfoScenarioOption
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito

@MicronautTest(transactional = false)
@ExtendWith(ProtoAnnotatorExtension::class)
class InfoPixKeyFailureTest(
    private val repository: PixKeyRepository,
    private val grpcClient: GrpcClientHandler,
    private val bcbClient: BcbClient
) {

    @BeforeEach
    fun cleanUp() { repository.deleteAll() }

    @Test
    fun `should return not found when could not find pix key with id pair`() {

        val request = PixKeyInfoScenarioOption.INVALID_PIX_KEY_WITH_INFO_PAIR.apply(repository, bcbClient)

        val result = grpcClient.info(request)

        with(result) {
            assertTrue(hasFailure())
            assertEquals("5 NOT_FOUND", status)
            assertEquals("Pix ID does not exists", failure.message)
        }
    }

    @Test
    fun `should return not found when could not find pix key with key value`() {

        val request = PixKeyInfoScenarioOption.INVALID_PIX_KEY_WITH_KEY_VALUE.apply(repository, bcbClient)

        val result = grpcClient.info(request)

        with(result) {
            assertTrue(hasFailure())
            assertEquals("5 NOT_FOUND", status)
            assertEquals("Pix key not registered in the central bank", failure.message)
        }
    }

    @MockBean(BcbClient::class)
    fun bcbClient() = Mockito.mock(BcbClient::class.java)
}