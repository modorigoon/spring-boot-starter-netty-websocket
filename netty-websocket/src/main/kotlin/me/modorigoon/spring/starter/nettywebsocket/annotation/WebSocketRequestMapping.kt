package me.modorigoon.spring.starter.nettywebsocket.annotation


@MustBeDocumented
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class WebSocketRequestMapping(val value: String)
