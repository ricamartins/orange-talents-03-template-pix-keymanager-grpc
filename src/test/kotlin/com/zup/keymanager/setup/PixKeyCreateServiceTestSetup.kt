package com.zup.keymanager.setup

import com.zup.keymanager.pixkey.PixKeyRepository
import com.zup.keymanager.pixkey.clients.ErpClient
import com.zup.keymanager.proto.PixKeyCreateRequest
import com.zup.keymanager.setup.options.*
import javax.inject.Singleton

@Singleton
class PixKeyCreateServiceTestSetup(
    private val repository: PixKeyRepository,
    private val erpClient: ErpClient
) {

    fun options(
        requestOption: PixKeyCreateRequestOption = PixKeyCreateRequestOption.VALID_WITH_RANDOM_KEY_TYPE,
        erpClientOption: ErpClientMockOption = ErpClientMockOption.NOTHING,
        repositoryOption: PixKeyRepositoryOption = PixKeyRepositoryOption.CLEAN_ALL,
        accountOption: AccountDetailsResponseOption = AccountDetailsResponseOption.ANY,
        scenarioOption: PixKeyCreateScenarioOption = PixKeyCreateScenarioOption.NOTHING
    ): PixKeyCreateRequest {

        if (scenarioOption.isChosen())
            return scenarioOption.apply(repository)!!

        val request = requestOption.apply()

        if (repositoryOption.isChosen())
            repositoryOption.apply(repository)

        if (erpClientOption.isChosen())
            erpClientOption.apply(erpClient, request, accountOption.apply())

        return request
    }

}
