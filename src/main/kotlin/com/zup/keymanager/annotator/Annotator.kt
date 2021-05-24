package com.zup.keymanager.annotator

import net.bytebuddy.ByteBuddy
import net.bytebuddy.agent.ByteBuddyAgent
import net.bytebuddy.asm.MemberAttributeExtension
import net.bytebuddy.description.annotation.AnnotationDescription
import net.bytebuddy.dynamic.DynamicType
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy

fun annotateProtoClasses() {
    ByteBuddyAgent.install()

    getProtoFiles()
        .map(::getMessagesFromProtoFile)
        .flatten().forEach(::addAnnotations)
}

fun toDescription(annotation: ProtoAnnotation): AnnotationDescription {
    var builder = AnnotationDescription.Builder.ofType(annotation.clazz)

    annotation.args.forEach { arg ->
        if (arg.second is String) builder = builder.define(arg.first, arg.second as String)

        if (arg.second is Int) builder = builder.define(arg.first, arg.second as Int)

        if (arg.second is Boolean) builder = builder.define(arg.first, arg.second as Boolean)
    }

    return builder.build()
}

fun addAnnotations(message: Message) {

    var builder = ByteBuddy().redefine(message.clazz)

    builder = annotateClass(message, builder)

    message.fields.forEach { field -> builder = annotateGetter(field, builder) }

    builder.make().load(message.clazz.classLoader, ClassReloadingStrategy.fromInstalledAgent())
}

fun annotateClass(message: Message, builder: DynamicType.Builder<out Any>): DynamicType.Builder<out Any> {
    return builder.annotateType(message.annotations.map(::toDescription))
}

fun annotateGetter(getter: Field, builder: DynamicType.Builder<out Any>): DynamicType.Builder<out Any> {
    return builder.visit(
        MemberAttributeExtension.ForMethod()
        .annotateMethod(getter.annotations.map(::toDescription))
        .on { it.name == getter.name })
}