package com.zup.keymanager.handler

import com.zup.keymanager.extensions.toStatusException
import io.grpc.BindableService
import io.grpc.stub.StreamObserver
import io.micronaut.aop.InterceptorBean
import io.micronaut.aop.MethodInterceptor
import io.micronaut.aop.MethodInvocationContext
import io.micronaut.context.ApplicationContext
import io.micronaut.inject.BeanDefinition
import java.lang.reflect.ParameterizedType
import javax.inject.Singleton

@Singleton @InterceptorBean(ErrorHandler::class)
class ErrorHandlerInterceptor(private val applicationContext: ApplicationContext) : MethodInterceptor<BindableService, Any> {

    private val validators = getRegisteredValidators()

    override fun intercept(context: MethodInvocationContext<BindableService, Any>): Any? {

        val (request, observer) = context.parameterValues

        return try {
            validators[request.javaClass]?.validate(request)
            context.proceed()
        } catch (e: Exception) {
            observer as StreamObserver<*>
            e.toStatusException().let(observer::onError)
        }

    }

    private fun getRegisteredValidators(): Map<Class<Any>, Validator<Any>> {
        return applicationContext.allBeanDefinitions
            .filter(::isValidator)
            .filter(::notIntercepted)
            .map { getTypeParameter(it) to getValidatorBean(it) }.toMap()
    }

    private fun isValidator(definition: BeanDefinition<*>): Boolean {
        return Validator::class.java.isAssignableFrom(definition.declaringType.orElse(Any::class.java))
    }

    private fun notIntercepted(definition: BeanDefinition<*>): Boolean {
        return !definition.name.contains("Intercepted")
    }

    @Suppress("unchecked_cast")
    private fun getTypeParameter(definition: BeanDefinition<*>): Class<Any> {
        return (definition.beanType.genericInterfaces[0] as ParameterizedType).actualTypeArguments[0] as Class<Any>
    }

    @Suppress("unchecked_cast")
    private fun getValidatorBean(definition: BeanDefinition<*>): Validator<Any> {
        return applicationContext.createBean(definition.beanType) as Validator<Any>
    }

}
