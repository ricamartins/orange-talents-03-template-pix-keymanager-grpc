package com.zup.keymanager.pixkey.delete

import com.zup.keymanager.pixkey.PixKeyRepository
import com.zup.keymanager.pixkey.clients.BcbClient
import com.zup.keymanager.pixkey.clients.BcbDeletePixKeyRequest
import com.zup.keymanager.setup.GrpcClientHandler
import com.zup.keymanager.setup.ProtoAnnotatorExtension
import com.zup.keymanager.setup.options.PixKeyDeleteScenarioOption.VALID_REQUEST_FOR_REGISTERED_PIX_KEY
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.mockito.Mockito.`when`

@MicronautTest(transactional=false)
@ExtendWith(ProtoAnnotatorExtension::class)
class DeletePixKeySuccessTest(
    private val repository: PixKeyRepository,
    private val grpcClient: GrpcClientHandler,
    private val bcbClient: BcbClient
) {

    @Test
    fun `should delete pix key`() {

        val request = VALID_REQUEST_FOR_REGISTERED_PIX_KEY.apply(repository)
        val pixKey = repository.findById(request.pixId).get()
        val bcbRequest = BcbDeletePixKeyRequest(pixKey)
        `when`(bcbClient.delete(pixKey.keyValue, bcbRequest))
            .thenReturn(HttpResponse.ok(bcbRequest))

        val result = grpcClient.delete(request)

        with(result) {
            assertTrue(hasSuccess())
            assertEquals("0 OK", status)
            assertFalse(repository.existsById(request.pixId))
        }
    }

    @MockBean(BcbClient::class)
    fun bcbClient() = Mockito.mock(BcbClient::class.java)
}