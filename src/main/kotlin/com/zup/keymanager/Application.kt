package com.zup.keymanager

import io.micronaut.runtime.Micronaut.*
fun main(args: Array<String>) {
	build()
	    .args(*args)
		.packages("com.zup.keymanager")
		.start()
}

