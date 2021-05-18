package com.zup.keymanager.setup

import com.zup.keymanager.pixkey.ErpClient
import com.zup.keymanager.pixkey.PixKeyRepository
import com.zup.keymanager.proto.PixKeyDeleteRequest
import com.zup.keymanager.setup.options.*
import javax.inject.Singleton

@Singleton
class PixKeyDeleteServiceTestSetup(
    private val repository: PixKeyRepository,
    private val erpClient: ErpClient
) {

    fun options(
        repositoryOption: PixKeyRepositoryOption = PixKeyRepositoryOption.CLEAN_ALL,
        scenarioOption: PixKeyDeleteScenarioOption = PixKeyDeleteScenarioOption.NOTHING
    ): PixKeyDeleteRequest {

        if (repositoryOption.isChosen())
            repositoryOption.apply(repository)

        return scenarioOption.apply(repository)!!
    }

}
