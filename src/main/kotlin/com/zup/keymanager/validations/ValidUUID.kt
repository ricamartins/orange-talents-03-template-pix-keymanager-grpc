package com.zup.keymanager.validations

import javax.validation.Constraint
import javax.validation.constraints.Pattern
import kotlin.annotation.AnnotationRetention.*
import kotlin.annotation.AnnotationTarget.*

@Constraint(validatedBy=[])
@Retention(RUNTIME)
@Target(FIELD, VALUE_PARAMETER, CONSTRUCTOR)
@Pattern(
    regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}\$",
    message = "Must be a valid UUID"
)
annotation class ValidUUID
