package me.modorigoon.spring.starter.nettywebsocket.netty

import com.fasterxml.jackson.databind.ObjectMapper
import io.netty.channel.Channel
import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame
import me.modorigoon.spring.starter.nettywebsocket.RequestEntity
import me.modorigoon.spring.starter.nettywebsocket.ResponseEntity
import me.modorigoon.spring.starter.nettywebsocket.ResponseStatus
import me.modorigoon.spring.starter.nettywebsocket.invoker.WebSocketAdviceInvoker
import me.modorigoon.spring.starter.nettywebsocket.invoker.WebSocketControllerInvoker
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture


@Component("webSocketInboundHandler")
@ChannelHandler.Sharable
class WebSocketInboundHandler(
    private val webSocketControllerInvoker: WebSocketControllerInvoker,
    private val webSocketAdviceInvoker: WebSocketAdviceInvoker,
    private val channelGroupManager: ChannelGroupManager,
    private val channelManager: ChannelManager,
    private val objectMapper: ObjectMapper
) : SimpleChannelInboundHandler<TextWebSocketFrame>() {

    val log: Logger = LoggerFactory.getLogger(WebSocketInboundHandler::class.java)

    override fun channelInactive(ctx: ChannelHandlerContext) {
        channelManager.removeChannel(ctx.channel())
        channelGroupManager.removeChannelInAllGroup(ctx.channel())
    }

    override fun channelRead0(ctx: ChannelHandlerContext, frame: TextWebSocketFrame) {
        val future = CompletableFuture<ResponseEntity>()
        val req = objectMapper.readValue(frame.retain().text(), RequestEntity::class.java)
        if (req?.mapper.isNullOrBlank()) {
            future.completeExceptionally(WebSocketInboundException("Invalid request mapper name."))
        } else {
            webSocketControllerInvoker.invoke(req, ctx, future)
        }

        future.exceptionally {
            webSocketAdviceInvoker.invoke(it)?.let { response ->
                sendResponse(ctx.channel(), response as ResponseEntity)
            }
            log.error("[WebSocketInboundHandler] error: ", it)
            null
        }.thenAccept { response ->
            response?.let { sendResponse(ctx.channel(), it) }
        }
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        sendResponse(ctx.channel(), ResponseEntity(ResponseStatus.ERROR, "Server error."))
    }

    private fun sendResponse(channel: Channel, response: ResponseEntity) {
        channel.writeAndFlush(TextWebSocketFrame(objectMapper.writeValueAsString(response)))
    }
}
