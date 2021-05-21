package com.zup.keymanager.pixkey

import com.zup.keymanager.extensions.with
import io.grpc.Status

enum class Institutions(val number: String, val formattedName: String) {
    ITAU("60701190", "Itaú Unibanco SA");

    companion object {
        fun fromNumber(number: String): Institutions {
            return values().find { it.number == number } ?: throw Status.INTERNAL with "Invalid participant number"
        }
    }
}