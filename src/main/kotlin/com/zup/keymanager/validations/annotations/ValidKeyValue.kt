package com.zup.keymanager.validations.annotations

import com.zup.keymanager.extensions.with
import com.zup.keymanager.proto.KeyType
import com.zup.keymanager.proto.PixKeyCreateRequest
import io.grpc.Status
import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator
import org.hibernate.validator.internal.constraintvalidators.bv.PatternValidator
import org.hibernate.validator.internal.constraintvalidators.hv.br.CPFValidator
import javax.inject.Singleton
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass

@Constraint(validatedBy = [ValidKeyValueValidator::class])
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
annotation class ValidKeyValue(
    val message: String = "Invalid key value",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

@Singleton
class ValidKeyValueValidator: ConstraintValidator<ValidKeyValue, PixKeyCreateRequest> {

    override fun isValid(value: PixKeyCreateRequest, context: ConstraintValidatorContext): Boolean {
        context.disableDefaultConstraintViolation()
        return when(value.keyType) {
            KeyType.DOCUMENT -> { CPFValidator().isValid(value.keyValue, context)
                .also { if (!it) addConstraint(context, "keyValue", "número do registro de contribuinte individual brasileiro (CPF) inválido") }
            }
            KeyType.PHONE -> { value.keyValue.matches("^\\+[1-9][0-9]\\d{1,14}\$".toRegex())
                .also { if (!it) addConstraint(context, "keyValue", "Must be a valid phone number") }
            }
            KeyType.EMAIL -> { EmailValidator().isValid(value.keyValue, context)
                .also { if (!it) addConstraint(context, "keyValue", "deve ser um endereço de e-mail bem formado") }
            }
            KeyType.RANDOM -> { value.keyValue.isNullOrBlank()
                .also { if (!it) addConstraint(context, "keyValue", "Must be null or blank") }
            }
            else -> { false.also { addConstraint(context, "keyType", "Must be a valid key type") }
//                throw Status.INVALID_ARGUMENT with "keyType: Invalid value for key type"
            }
        }
    }

    private fun addConstraint(context: ConstraintValidatorContext, property: String, message: String) {
        context.buildConstraintViolationWithTemplate(message).addPropertyNode(property).addConstraintViolation()
    }
}
