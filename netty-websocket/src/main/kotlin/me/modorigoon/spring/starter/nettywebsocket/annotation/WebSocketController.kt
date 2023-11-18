package me.modorigoon.spring.starter.nettywebsocket.annotation

import org.springframework.stereotype.Component


@Component
@Target(AnnotationTarget.TYPE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class WebSocketController
