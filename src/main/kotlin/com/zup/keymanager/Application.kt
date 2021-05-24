package com.zup.keymanager

import com.zup.keymanager.annotator.annotateProtoClasses
import io.micronaut.runtime.Micronaut.*
fun main(args: Array<String>) {
	annotateProtoClasses()
	build()
	    .args(*args)
		.packages("com.zup.keymanager")
		.start()
}

