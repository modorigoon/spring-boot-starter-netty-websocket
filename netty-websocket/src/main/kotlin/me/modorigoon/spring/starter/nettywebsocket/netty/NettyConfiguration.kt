package me.modorigoon.spring.starter.nettywebsocket.netty

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer
import java.net.InetSocketAddress


@Configuration
class NettyConfiguration(val nettyProperties: NettyProperties) {

    @Bean
    fun nettyTcpPort(): InetSocketAddress = InetSocketAddress(nettyProperties.port)

    @Bean
    fun propertySourcePlaceholderConfigurer(): PropertySourcesPlaceholderConfigurer =
        PropertySourcesPlaceholderConfigurer()

    @Bean(destroyMethod = "shutdownGracefully")
    fun bossGroup(): NioEventLoopGroup = NioEventLoopGroup(nettyProperties.bossThread)

    @Bean(destroyMethod = "shutdownGracefully")
    fun workerGroup(): NioEventLoopGroup = NioEventLoopGroup(nettyProperties.workerThread)

    @Bean
    fun serverBootstrap(
        nettyChannelInitializer: NettyChannelInitializer,
        bossGroup: NioEventLoopGroup,
        workerGroup: NioEventLoopGroup
    ): ServerBootstrap = ServerBootstrap().apply {
        group(bossGroup, workerGroup)
        channel(NioServerSocketChannel::class.java)
        childHandler(nettyChannelInitializer)
    }
}