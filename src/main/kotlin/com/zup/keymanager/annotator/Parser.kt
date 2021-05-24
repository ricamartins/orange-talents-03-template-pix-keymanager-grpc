package com.zup.keymanager.annotator

import java.io.File
import javax.validation.constraints.NotBlank

fun getProtoFiles(path: String = "src/main/proto"): List<File> {

    val dir = File(path)

    if (!dir.isDirectory || dir.listFiles() == null) return listOf()

    return dir.listFiles().filter { it.extension == "proto" }
}

fun getMessagesFromProtoFile(file: File): List<Message> {

    val javaPackage = getJavaPackage(file)
    val annotationsPackage = getCustomAnnotationsPackage(file)

    val messages = mutableListOf<Message>()
    file.readLines().forEach { line ->

        if (isMessage(line))
            messages.add(createMessage(line, javaPackage, annotationsPackage))

        if (isField(line))
            messages.last().fields.add(createField(line, annotationsPackage))

    }

    return messages.filter(::hasAnnotations)
}

fun getJavaPackage(file: File): String {
    return file.readLines()
        .find { it.startsWith("option java_package") }
        ?.split("=")?.get(1)?.trim()
        ?.let { it.subSequence(1, it.length - 2) }
        .toString()
}

fun getCustomAnnotationsPackage(file: File): String {
    return file.readLines()
        .find { it.contains("annotations_package") }
        ?.split("=")?.get(1)?.trim()
        ?.let { it.subSequence(1, it.length - 1) }
        .toString()
}

fun createMessage(line: String, javaPackage: String, annotationsPackage: String): Message {
    val name = line.split(" ")[1]
    try {
        val clazz = Class.forName("$javaPackage.$name")
        return Message(clazz, annotations = getAnnotations(line, annotationsPackage))
    } catch (e: Exception) {
        e.printStackTrace()
        throw java.lang.RuntimeException("No such class: $javaPackage.$name")
    }
}

fun createField(line: String, annotationsPackage: String): Field {
    val getter = "get${line.trim().split(" ")[1].capitalize()}"
    return Field(getter, getAnnotations(line, annotationsPackage))
}

fun getAnnotations(line: String, annotationsPackage: String): List<ProtoAnnotation> {
    val annotations = mutableListOf<ProtoAnnotation>()

    if (!line.contains("//")) return annotations

    line.split("//")[1].split(" ").forEach { comment ->

        if (comment.startsWith("@"))
            annotations.add(searchAnnotation(comment, annotationsPackage))
    }

    return annotations
}

fun searchAnnotation(annotation: String, annotationsPackage: String): ProtoAnnotation {
    val start = annotation.indexOf("@") + 1
    val end = annotation.indexOf("(")
    val annotationName = annotation.substring(start, if (end > -1) end else annotation.length)

    var parameters = if (end > 1) {
        val params = annotation.subSequence(end+1, annotation.indexOf(")")).split(",")
        params.map { it.split("=") }.map { it[0] to convertToType(it[1]) }.toTypedArray()
    } else { arrayOf() }

    return try {
        val clazz = Class.forName("javax.validation.constraints.$annotationName") as Class<out Annotation>
        ProtoAnnotation(clazz, *parameters)
    } catch (e: Exception) {

        try {
            val clazz = Class.forName("$annotationsPackage.$annotationName") as Class<out Annotation>
            ProtoAnnotation(clazz, *parameters)
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException("Could not find annotation class $annotationName", e)
        }
    }
}

fun isMessage(line: String): Boolean {
    return line.startsWith("message")
}

fun isField(line: String): Boolean {
    return (line.startsWith("  ") && !line.startsWith("   ") &&
            line.contains("=") && line.trim().split(" ")[2] == "=")
}

fun hasAnnotations(message: Message): Boolean {
    return message.annotations.isNotEmpty() || message.fields.map { it.annotations.isNotEmpty() }.any { it }
}

fun convertToType(value: String): Any {

    if (value.contains("\"")) return value

    if (value.map(Char::isDigit).all { it }) return value.toInt()

    if (value == "true" || value == "false") return value.toBoolean()

    throw RuntimeException("Could not identify the type for value $value")
}

data class Message(val clazz: Class<*>, val fields: MutableList<Field> = mutableListOf(), val annotations: List<ProtoAnnotation>)
data class Field(val name: String, val annotations: List<ProtoAnnotation>)
class ProtoAnnotation(val clazz: Class<out Annotation>, vararg val args: Pair<String, Any>) {
    override fun toString(): String {
        return "ProtoAnnotation(clazz=$clazz)"
    }
}