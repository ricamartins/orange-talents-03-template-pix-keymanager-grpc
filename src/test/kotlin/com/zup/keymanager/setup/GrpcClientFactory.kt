package com.zup.keymanager.setup

import com.zup.keymanager.proto.PixKeyServiceGrpc.newBlockingStub
import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel

@Factory
class GrpcClientFactory {

    @Bean
    fun blockingClient(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel) = newBlockingStub(channel)
}