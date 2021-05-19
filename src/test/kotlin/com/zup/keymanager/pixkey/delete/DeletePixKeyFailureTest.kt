package com.zup.keymanager.pixkey.delete

import com.zup.keymanager.pixkey.PixKeyRepository
import com.zup.keymanager.setup.GrpcClientHandler
import com.zup.keymanager.setup.options.PixKeyDeleteScenarioOption.INVALID_REQUEST_NOT_FOUND
import com.zup.keymanager.setup.options.PixKeyDeleteScenarioOption.INVALID_REQUEST_NOT_OWNER
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

@MicronautTest(transactional=false)
class DeletePixKeyFailureTest(
    private val repository: PixKeyRepository,
    private val grpcClient: GrpcClientHandler,
) {

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

}