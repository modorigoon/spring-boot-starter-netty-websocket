package me.modorigoon.spring.starter.nettywebsocket.netty

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.Channel
import jakarta.annotation.PreDestroy
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.net.InetSocketAddress


@Component
class NettyServer(
    private val serverBootstrap: ServerBootstrap,
    private val nettyTcpPort: InetSocketAddress
) {

    private val logger = LoggerFactory.getLogger(NettyServer::class.java)
    private var channel: Channel? = null

    fun start() {
        try {
            channel = serverBootstrap.bind(nettyTcpPort).sync().channel()
            logger.info("Netty server started on port: ${nettyTcpPort.port}")
        } catch (e: Exception) {
            logger.error("Failed to start Netty server", e)
            throw e
        }
    }

    @PreDestroy
    fun stop() {
        try {
            channel?.apply {
                close().sync()
                parent()?.close()?.sync()
            }
            logger.info("Netty server stopped")
        } catch (e: Exception) {
            logger.error("Failed to stop Netty server", e)
        }
    }
}
