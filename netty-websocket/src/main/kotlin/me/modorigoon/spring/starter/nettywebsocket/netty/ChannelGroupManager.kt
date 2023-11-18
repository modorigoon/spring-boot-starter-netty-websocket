package me.modorigoon.spring.starter.nettywebsocket.netty

import io.netty.channel.Channel
import io.netty.channel.group.ChannelGroup
import io.netty.channel.group.DefaultChannelGroup
import io.netty.util.concurrent.ImmediateEventExecutor
import org.springframework.stereotype.Component
import java.lang.Exception
import java.util.concurrent.ConcurrentHashMap


@Component
class ChannelGroupManager {

    private val channelGroups = ConcurrentHashMap<String, ChannelGroup>()

    fun getChannelGroup(name: String): ChannelGroup? = channelGroups[name]

    fun createChannelGroup(name: String): ChannelGroup =
        getChannelGroup(name)?.let {
            throw Exception("Channel group already exists.")
        } ?: DefaultChannelGroup(ImmediateEventExecutor.INSTANCE).apply {
            channelGroups[name] = this
        }

    fun getOrCreate(name: String): ChannelGroup = getChannelGroup(name) ?: createChannelGroup(name)

    fun removeChannelInGroup(name: String, channel: Channel): Boolean =
        getChannelGroup(name)?.remove(channel) ?: false

    fun removeChannelInAllGroup(channel: Channel) {
        channelGroups.values.forEach { it.remove(channel) }
    }
}
