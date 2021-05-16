package com.zup.keymanager.setup.options

import com.zup.keymanager.pixkey.PixKeyRepository

enum class PixKeyRepositoryOption {

    CLEAN_ALL {
        override fun apply(repository: PixKeyRepository) = repository.deleteAll()
    },
    NOTHING {
        override fun apply(repository: PixKeyRepository) {}
    };

    abstract fun apply(repository: PixKeyRepository)

    fun isChosen() = this != NOTHING
}