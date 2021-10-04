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

package love.forte.simbot.component.kaiheila.event

import kotlinx.serialization.KSerializer
import love.forte.simbot.api.message.events.MsgGet
import love.forte.simbot.component.kaiheila.objects.Channel
import java.util.concurrent.ConcurrentHashMap


/**
 *
 * [KhlEventLocator] 全局性实现。
 *
 * @author ForteScarlet
 */
public object KhlEventLocator : EventLocator {

    // Event.Type -> ChannelType -> extraType
    private val eventCoordinateMap =
        ConcurrentHashMap<Event.Type, ConcurrentHashMap<Any, ConcurrentHashMap<String, KSerializer<*>>>>()

    // private val eventCoordinateMap = ConcurrentHashMap<EventSerializerCoordinate, KSerializer<*>>()


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
        eventCoordinateMap[type]?.get(channelType ?: NullType)?.get(extraType)


    override fun <T> registerCoordinate(
        type: Event.Type,
        channelType: Channel.Type?,
        extraType: String,
        serializer: KSerializer<out T>,
    ): KSerializer<*> where T : Event<*>, T : MsgGet {
        // val coordinate = EventSerializerCoordinate(type, extraType, channelType)
        //return eventCoordinateMap.computeIfAbsent(coordinate) { serializer }


        val channelType0 = channelType ?: NullType


        return eventCoordinateMap
            .computeIfAbsent(type, ::newConcurrentHashMap)
            .computeIfAbsent(channelType0, ::newConcurrentHashMap)
            .put(extraType, serializer) ?: serializer
    }

    private object NullType
}



@Suppress("NOTHING_TO_INLINE", "UNUSED_PARAMETER")
private inline fun <K, V> newConcurrentHashMap(key: Any): ConcurrentHashMap<K, V> = ConcurrentHashMap()