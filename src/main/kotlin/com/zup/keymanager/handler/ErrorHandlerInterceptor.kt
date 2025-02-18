package com.zup.keymanager.handler

import com.zup.keymanager.extensions.toStatusException
import io.grpc.BindableService
import io.grpc.stub.StreamObserver
import io.micronaut.aop.InterceptorBean
import io.micronaut.aop.MethodInterceptor
import io.micronaut.aop.MethodInvocationContext
import javax.inject.Singleton

@Singleton @InterceptorBean(ErrorHandler::class)
class ErrorHandlerInterceptor: MethodInterceptor<BindableService, Any> {

    override fun getOrder() = 1

    override fun intercept(context: MethodInvocationContext<BindableService, Any>): Any? {

        if (isNotGrpcService(context.targetMethod.declaringClass))
            return context.proceed()

        if (isNotGrpcEndpoint(context.parameterValues))
            return context.proceed()

        val observer = context.parameterValues[1] as StreamObserver<*>

        return try {
            context.proceed()
        } catch (e: Exception) {
            e.toStatusException().let(observer::onError)
        }

    }

    private fun isNotGrpcService(declaringClass: Class<*>): Boolean {
        return !BindableService::class.java.isAssignableFrom(declaringClass)
    }

    private fun isNotGrpcEndpoint(parameters: Array<Any>): Boolean {
        return parameters.size != 2 || parameters[1] !is StreamObserver<*>
    }
}
