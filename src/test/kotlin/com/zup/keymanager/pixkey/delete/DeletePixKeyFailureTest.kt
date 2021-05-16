package com.zup.keymanager.pixkey.delete

import com.zup.keymanager.pixkey.PixKeyRepository
import com.zup.keymanager.proto.PixKeyServiceGrpc.PixKeyServiceBlockingStub
import com.zup.keymanager.setup.PixKeyDeleteServiceTestSetup
import com.zup.keymanager.setup.options.PixKeyDeleteScenarioOption.INVALID_REQUEST_NOT_FOUND
import com.zup.keymanager.setup.options.PixKeyDeleteScenarioOption.INVALID_REQUEST_NOT_OWNER
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

@MicronautTest(transactional=false)
class DeletePixKeyFailureTest(
    private val repository: PixKeyRepository,
    private val grpcClient: PixKeyServiceBlockingStub,
    private val setup: PixKeyDeleteServiceTestSetup
) {

    @Test
    fun `should return not_found error message when pix id does not exists`() {

        val request = setup.options(
            scenarioOption = INVALID_REQUEST_NOT_FOUND
        )

        val result = grpcClient.delete(request)

        with(result) {
            assertTrue(hasFailure())
            assertEquals("5 NOT_FOUND", failure.status)
            assertEquals("pixId", failure.errorsList[0].field)
            assertEquals("Pix ID does not exists", failure.errorsList[0].message)
            assertFalse(repository.existsById(request.pixId))
        }
    }

    @Test
    fun `should return permission_denied error message when pix key does not belong to client`() {

        val request = setup.options(
            scenarioOption = INVALID_REQUEST_NOT_OWNER
        )

        val result = grpcClient.delete(request)

        with(result) {
            assertTrue(hasFailure())
            assertEquals("7 PERMISSION_DENIED", failure.status)
            assertEquals("Pix key does not belong to this client", failure.errorsList[0].message)
            assertTrue(repository.existsById(request.pixId))
        }
    }

}