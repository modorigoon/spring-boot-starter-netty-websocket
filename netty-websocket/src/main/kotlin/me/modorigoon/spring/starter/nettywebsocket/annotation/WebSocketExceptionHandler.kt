package me.modorigoon.spring.starter.nettywebsocket.annotation

import kotlin.reflect.KClass


@MustBeDocumented
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class WebSocketExceptionHandler(val throwables: Array<KClass<out Throwable>> = [])
