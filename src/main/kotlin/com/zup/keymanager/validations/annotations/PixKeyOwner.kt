package com.zup.keymanager.validations.annotations

import com.zup.keymanager.extensions.with
import com.zup.keymanager.pixkey.PixKeyRepository
import com.zup.keymanager.proto.PixKeyDeleteRequest
import com.zup.keymanager.proto.PixKeyInfoRequest
import io.grpc.Status
import javax.inject.Singleton
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass

@Constraint(validatedBy = [PixKeyOwnerValidator::class])
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
annotation class PixKeyOwner(
    val message: String = "",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

@Singleton
class PixKeyOwnerValidator(val repository: PixKeyRepository): ConstraintValidator<PixKeyOwner, Any> {

    override fun isValid(value: Any, context: ConstraintValidatorContext?): Boolean {
        return when(value) {
            is PixKeyDeleteRequest -> validate(value.clientId, value.pixId)
            is PixKeyInfoRequest.PixKeyInfoPair -> validate(value.clientId, value.pixId)
            else -> true // throw Status.INTERNAL with "Annotated type not supported"
        }
    }

    private fun validate(clientId: String, pixId: String): Boolean {
        return repository.findById(pixId).map { pixKey ->
            if (pixKey.clientId != clientId)
                throw Status.PERMISSION_DENIED with "Pix key does not belong to this client"
            else true
        }.orElseThrow { Status.NOT_FOUND with "Pix ID does not exists" }
    }
}