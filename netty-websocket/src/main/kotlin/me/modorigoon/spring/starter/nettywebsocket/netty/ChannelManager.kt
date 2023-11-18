package me.modorigoon.spring.starter.nettywebsocket.netty

import io.netty.channel.Channel
import io.netty.util.AttributeKey
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap


@Component
class ChannelManager {

    companion object {
        private const val CHANNEL_ID_ATTRIBUTE_KEY: String = "CHANNEL_ID:"
        private val channelAttributeKey = AttributeKey.newInstance<String>(CHANNEL_ID_ATTRIBUTE_KEY)
    }

    private val channels = ConcurrentHashMap<String, Channel>()

    fun addChannel(channel: Channel, id: String): Channel =
        channel.apply { attr(channelAttributeKey).set(id) }.apply { channels[id] = this }

    fun getChannel(id: String): Channel? = channels[id]

    fun removeChannel(id: String): Channel? = channels.remove(id)

    fun removeChannel(channel: Channel): Channel? = channels.remove(getChannelIdAttributeKey(channel))

    // TODO: channel attribute key null safe
    fun getChannelIdAttributeKey(channel: Channel): String = channel.attr(channelAttributeKey).get()
}
