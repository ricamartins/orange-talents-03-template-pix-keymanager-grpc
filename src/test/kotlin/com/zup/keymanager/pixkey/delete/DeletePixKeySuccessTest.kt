package com.zup.keymanager.pixkey.delete

import com.zup.keymanager.pixkey.PixKeyRepository
import com.zup.keymanager.proto.PixKeyServiceGrpc.PixKeyServiceBlockingStub
import com.zup.keymanager.setup.PixKeyDeleteServiceTestSetup
import com.zup.keymanager.setup.options.PixKeyDeleteScenarioOption.VALID_REQUEST_FOR_REGISTERED_PIX_KEY
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

@MicronautTest(transactional=false)
class DeletePixKeySuccessTest(
    private val repository: PixKeyRepository,
    private val grpcClient: PixKeyServiceBlockingStub,
    private val setup: PixKeyDeleteServiceTestSetup
) {

    @Test
    fun `should delete pix key`() {

        val request = setup.options(
            scenarioOption = VALID_REQUEST_FOR_REGISTERED_PIX_KEY
        )

        val result = grpcClient.delete(request)

        with(result) {
            assertTrue(hasSuccess())
            assertEquals("0 OK", status)
            assertFalse(repository.existsById(request.pixId))
        }
    }

}