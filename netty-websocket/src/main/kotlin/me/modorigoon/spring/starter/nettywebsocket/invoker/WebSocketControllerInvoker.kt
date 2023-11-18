package me.modorigoon.spring.starter.nettywebsocket.invoker

import io.netty.channel.ChannelHandlerContext
import me.modorigoon.spring.starter.nettywebsocket.RequestEntity
import me.modorigoon.spring.starter.nettywebsocket.ResponseEntity
import me.modorigoon.spring.starter.nettywebsocket.annotation.WebSocketController
import me.modorigoon.spring.starter.nettywebsocket.annotation.WebSocketRequestMapping
import org.modelmapper.ModelMapper
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component
import java.lang.IllegalArgumentException
import java.lang.reflect.Method
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CopyOnWriteArrayList


@Component
class WebSocketControllerInvoker(
    private val context: ApplicationContext,
    private val modelMapper: ModelMapper
) {

    private final val controllerBeans: List<Any> =
        CopyOnWriteArrayList(context.getBeansWithAnnotation(WebSocketController::class.java).values)

    private fun getController(bean: Any, name: String): Pair<Method, Any>? =
        bean.javaClass.declaredMethods
            .firstOrNull { it.getAnnotation(WebSocketRequestMapping::class.java)?.value == name }
            ?.let { it to bean }

    fun invoke(
        req: RequestEntity,
        ctx: ChannelHandlerContext,
        future: CompletableFuture<ResponseEntity>
    ): CompletableFuture<ResponseEntity> {
        val (controller, bean) = controllerBeans
            .asSequence()
            .mapNotNull { getController(it, req.mapper) }
            .firstOrNull() ?: return future.also {
            it.completeExceptionally(NullPointerException("WebSocket controller not found."))
        }

        try {
            val parameters = controller.parameters
            val arg = when {
                parameters.any { it.type.isAssignableFrom(ChannelHandlerContext::class.java) } ->
                    modelMapper.map(req.body, parameters[0].type)
                        ?: throw IllegalArgumentException("Parameter cannot be null.")

                else -> ctx
            }
            controller.invoke(bean, arg, ctx, future)
        } catch (e: Exception) {
            future.completeExceptionally(e)
        }

        return future
    }
}
