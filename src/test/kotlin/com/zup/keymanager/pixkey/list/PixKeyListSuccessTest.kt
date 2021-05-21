package com.zup.keymanager.pixkey.list

import com.zup.keymanager.extensions.toPixKey
import com.zup.keymanager.pixkey.PixKeyRepository
import com.zup.keymanager.proto.PixKeyListRequest
import com.zup.keymanager.setup.GrpcClientHandler
import com.zup.keymanager.setup.options.AccountDetailsResponseOption
import com.zup.keymanager.setup.options.PixKeyCreateRequestOption
import com.zup.keymanager.setup.options.toBcbCreatePixKeyRequest
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

@MicronautTest(transactional = false)
class PixKeyListSuccessTest(
    private val repository: PixKeyRepository,
    private val grpcClient: GrpcClientHandler
) {

    @BeforeEach
    fun cleanUp() { repository.deleteAll() }

    @Test //To complete
    fun `should list all three pix keys the client has`() {

        val clientId = UUID.randomUUID().toString()
        repeat(3) {
            val req = PixKeyCreateRequestOption.create(clientId = clientId)
            val bcb = req.toBcbCreatePixKeyRequest(AccountDetailsResponseOption.ANY.apply())
            repository.save(req.toPixKey(bcb))
        }

        val request = PixKeyListRequest.newBuilder().setClientId(clientId).build()

        val response = grpcClient.list(request)

        with(response) {
            assertTrue(hasSuccess())
            assertTrue(success.listResponse.pixKeysList.size == 3)
        }
    }

}