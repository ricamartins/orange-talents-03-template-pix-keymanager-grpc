package com.zup.keymanager.setup

import com.zup.keymanager.annotator.annotateProtoClasses
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ExtensionContext.Namespace.*

class ProtoAnnotatorExtension: BeforeAllCallback {

    override fun beforeAll(context: ExtensionContext) {

        val uniqueId = this::class.java.name

        val setupClass = context.root.getStore(GLOBAL).get(uniqueId)

        if(setupClass == null) {
            context.root.getStore(GLOBAL).put(uniqueId, this)
            annotateProtoClasses()
        }
    }
}