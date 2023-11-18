package me.modorigoon.spring.starter.nettywebsocket

import java.time.LocalDateTime


data class ResponseEntity(
    val status: ResponseStatus,
    val identifier: String?,
    val message: String?,
    val body: Any?,
    val time: LocalDateTime
) {
    constructor(status: ResponseStatus, identifier: String?, message: String?, body: Any?) : this(
        status = status,
        identifier = identifier,
        message = message,
        body = body,
        time = LocalDateTime.now()
    )

    constructor(identifier: String?, message: String?, body: Any?) : this(
        status = ResponseStatus.OK,
        identifier = identifier,
        message = message,
        body = body
    )

    constructor(status: ResponseStatus, message: String?) : this(
        status = status,
        identifier = null,
        message = message,
        body = null
    )
}

enum class ResponseStatus {
    OK, ERROR, BAD_REQUEST
}