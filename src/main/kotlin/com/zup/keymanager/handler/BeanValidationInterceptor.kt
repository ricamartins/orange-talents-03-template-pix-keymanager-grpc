package com.zup.keymanager.handler

import io.grpc.BindableService
import io.grpc.stub.StreamObserver
import io.micronaut.aop.InterceptorBean
import io.micronaut.aop.MethodInterceptor
import io.micronaut.aop.MethodInvocationContext
import io.micronaut.validation.Validated
import javax.inject.Singleton
import javax.validation.ConstraintViolationException
import javax.validation.Validator

@Singleton @InterceptorBean(Validated::class)
class BeanValidationInterceptor(val validator: Validator): MethodInterceptor<BindableService, Any> {

    override fun getOrder() = 2

    override fun intercept(context: MethodInvocationContext<BindableService, Any>): Any? {

        if (canValidate(context)) {
            validator.validate(context.parameterValues[0])
                .let { if (it.isNotEmpty()) throw ConstraintViolationException(it) }
        }

        return context.proceed()
    }

    private fun canValidate(context: MethodInvocationContext<BindableService, Any>): Boolean {
        return isGrpcService(context.targetMethod.declaringClass) && isGrpcEndpoint(context.parameterValues)
    }

    private fun isGrpcService(declaringClass: Class<*>): Boolean {
        return BindableService::class.java.isAssignableFrom(declaringClass)
    }

    private fun isGrpcEndpoint(parameters: Array<Any>): Boolean {
        return parameters.size == 2 && parameters[1] is StreamObserver<*>
    }
}