package me.modorigoon.spring.starter.nettywebsocket.invoker

import me.modorigoon.spring.starter.nettywebsocket.annotation.WebSocketControllerAdvice
import me.modorigoon.spring.starter.nettywebsocket.annotation.WebSocketExceptionHandler
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component
import java.lang.reflect.Method
import java.util.concurrent.CopyOnWriteArrayList


@Component
class WebSocketAdviceInvoker(val context: ApplicationContext) {

    private val adviceBeans: List<Any> =
        CopyOnWriteArrayList(context.getBeansWithAnnotation(WebSocketControllerAdvice::class.java).values)

    private fun getAdvisor(bean: Any?, throwable: Throwable?): Method? =
        bean?.javaClass?.methods?.firstOrNull { method ->
            method.getAnnotation(WebSocketExceptionHandler::class.java)
                ?.throwables?.any { it.java == throwable?.javaClass } == true
        }

    fun invoke(throwable: Throwable?): Any? =
        throwable?.let {
            adviceBeans.firstOrNull { bean ->
                getAdvisor(bean, throwable)?.invoke(bean, throwable) != null
            }
        }
}
