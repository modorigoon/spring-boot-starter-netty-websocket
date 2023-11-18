package me.modorigoon.spring.starter.nettywebsocket.netty

import io.netty.channel.Channel
import io.netty.channel.ChannelInitializer
import io.netty.handler.codec.http.HttpObjectAggregator
import io.netty.handler.codec.http.HttpServerCodec
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler
import org.springframework.stereotype.Component


@Component
class NettyChannelInitializer(
    private val webSocketInboundHandler: WebSocketInboundHandler,
    private val nettyProperties: NettyProperties
) : ChannelInitializer<Channel>() {

    override fun initChannel(channel: Channel) {
        channel.pipeline().apply {

            // Add HTTP server codec
            addLast(HttpServerCodec())

            // Aggregate HTTP messages into a single full HTTP message
            addLast(HttpObjectAggregator(nettyProperties.maxContentLength))

            // Handle compression for websocket
            addLast(WebSocketServerCompressionHandler())

            // Handle websocket upgrade and frames
            addLast(WebSocketServerProtocolHandler(nettyProperties.socketPath, null, true))

            // Custom handler for websocket message
            addLast(webSocketInboundHandler)
        }
    }
}
