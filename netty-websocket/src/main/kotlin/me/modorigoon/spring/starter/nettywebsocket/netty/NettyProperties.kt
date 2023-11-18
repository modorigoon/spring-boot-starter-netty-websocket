package me.modorigoon.spring.starter.nettywebsocket.netty

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration


@Configuration
@ConfigurationProperties(prefix = "websocket.netty")
data class NettyProperties(
    val socketPath: String = "",
    val maxContentLength: Int = 65535,
    val port: Int = 8090,
    val bossThread: Int = 1,
    val workerThread: Int = 2
)
