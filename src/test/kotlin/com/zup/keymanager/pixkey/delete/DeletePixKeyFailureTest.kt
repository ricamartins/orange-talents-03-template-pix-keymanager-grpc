package com.zup.keymanager.pixkey.delete

import com.zup.keymanager.pixkey.PixKeyRepository
import com.zup.keymanager.pixkey.clients.BcbClient
import com.zup.keymanager.pixkey.clients.BcbDeletePixKeyRequest
import com.zup.keymanager.setup.GrpcClientHandler
import com.zup.keymanager.setup.options.PixKeyDeleteScenarioOption
import com.zup.keymanager.setup.options.PixKeyDeleteScenarioOption.INVALID_REQUEST_NOT_FOUND
import com.zup.keymanager.setup.options.PixKeyDeleteScenarioOption.INVALID_REQUEST_NOT_OWNER
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito

@MicronautTest(transactional=false)
class DeletePixKeyFailureTest(
    private val repository: PixKeyRepository,
    private val grpcClient: GrpcClientHandler,
    private val bcbClient: BcbClient
) {

    @BeforeEach
    fun cleanUp() { repository.deleteAll() }

    @Test
    fun `should return not_found error message when pix id does not exists`() {

        val request = INVALID_REQUEST_NOT_FOUND.apply(repository)

        val result = grpcClient.delete(request)

        with(result) {
            assertTrue(hasFailure())
            assertEquals("5 NOT_FOUND", status)
            assertEquals("Pix ID does not exists", failure.message)
            assertFalse(repository.existsById(request.pixId))
        }
    }

    @Test
    fun `should return permission_denied error message when pix key does not belong to client`() {

        val request = INVALID_REQUEST_NOT_OWNER.apply(repository)

        val result = grpcClient.delete(request)

        with(result) {
            assertTrue(hasFailure())
            assertEquals("7 PERMISSION_DENIED", status)
            assertEquals("Pix key does not belong to this client", failure.message)
            assertTrue(repository.existsById(request.pixId))
        }
    }

    @Test
    fun `should return permission_denied when could not remove pix key from the central bank`() {

        val request = PixKeyDeleteScenarioOption.VALID_REQUEST_FOR_REGISTERED_PIX_KEY.apply(repository)
        val pixKey = repository.findById(request.pixId).get()
        val bcbRequest = BcbDeletePixKeyRequest(pixKey)
        Mockito.`when`(bcbClient.delete(pixKey.keyValue, bcbRequest)).thenThrow(forbiddenException())

        val result = grpcClient.delete(request)

        with(result) {
            assertTrue(hasFailure())
            assertEquals("7 PERMISSION_DENIED", status)
            assertEquals("Client can not delete this pix key", failure.message)
            assertTrue(repository.existsById(request.pixId))
        }
    }

    private fun forbiddenException(): HttpClientResponseException {
        return HttpClientResponseException("Proibido realizar operação", HttpResponse.status<Any>(HttpStatus.FORBIDDEN))
    }

    @MockBean(BcbClient::class)
    fun bcbClient() = Mockito.mock(BcbClient::class.java)

}