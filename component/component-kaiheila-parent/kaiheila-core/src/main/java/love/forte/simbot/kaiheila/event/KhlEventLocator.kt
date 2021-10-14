/*
 *
 *  * Copyright (c) 2021. ForteScarlet All rights reserved.
 *  * Project  simple-robot
 *  * File     MiraiAvatar.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *
 */

package love.forte.simbot.kaiheila.event

import kotlinx.serialization.KSerializer
import love.forte.simbot.api.message.events.MsgGet
import love.forte.simbot.kaiheila.event.message.registerMessageEventCoordinates
import love.forte.simbot.kaiheila.objects.Channel
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap


/**
 *
 * [KhlEventLocator] 全局性实现。
 *
 * @author ForteScarlet
 */
public object KhlEventLocator : EventLocator {

    private val logger = LoggerFactory.getLogger(KhlEventLocator::class.java)

    // Event.Type -> ChannelType -> extraType
    // private val eventCoordinateMap =
    //     ConcurrentHashMap<Event.Type, ConcurrentHashMap<Any, ConcurrentHashMap<String, KSerializer<*>>>>()

    private val eventCoordinateMap = ConcurrentHashMap<Coordinate, KSerializer<*>>()

    init {
        init()
    }

    override fun locateAsEvent(
        type: Event.Type,
        channelType: Channel.Type?,
        extraType: String,
    ): KSerializer<out Event<*>>? {
        @Suppress("UNCHECKED_CAST")
        return this[type, channelType, extraType] as? KSerializer<out Event<*>>
    }

    override fun locateAsMsgGet(
        type: Event.Type,
        channelType: Channel.Type?,
        extraType: String,
    ): KSerializer<out MsgGet>? {
        @Suppress("UNCHECKED_CAST")
        return this[type, channelType, extraType] as? KSerializer<MsgGet>
    }

    private operator fun get(type: Event.Type, channelType: Channel.Type?, extraType: String): KSerializer<*>? =
        eventCoordinateMap[Coordinate(type, channelType, extraType)]


    override fun <T> registerCoordinate(
        type: Event.Type,
        channelType: Channel.Type?,
        extraType: String,
        serializer: KSerializer<out T>,
    ): KSerializer<*> where T : Event<*>, T : MsgGet {
        val key = Coordinate(type, channelType, extraType)
        logger.debug("Registration event coordinate {}", key)
        val put = eventCoordinateMap.put(key, serializer)
        if (put != null) {
            logger.debug("The old coordinate was overwritten during registration: {}", put)
        }
        return put ?: serializer
    }

    private data class Coordinate(val type: Event.Type, val channelType: Channel.Type?, val extraType: String)
}


@Suppress("NOTHING_TO_INLINE", "UNUSED_PARAMETER")
private inline fun <K, V> newConcurrentHashMap(key: Any): ConcurrentHashMap<K, V> = ConcurrentHashMap()


internal fun EventLocator.init() {
    // Message Events.
    registerMessageEventCoordinates()
    // TODO
}
