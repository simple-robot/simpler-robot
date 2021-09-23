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
import java.util.concurrent.ConcurrentHashMap


/**
 *
 * [SimpleEventLocator] 的基础实现。
 *
 * @author ForteScarlet
 */
public class SimpleEventLocator : EventLocator {

    private val eventCoordinateMap = ConcurrentHashMap<EventSerializerCoordinate, KSerializer<*>>()


    override fun locateAsEvent(type: Event.Type, extraType: String): KSerializer<out Event<*>>? {
        val coordinate = EventSerializerCoordinate(type, extraType)

        @Suppress("UNCHECKED_CAST")
        return eventCoordinateMap[coordinate] as? KSerializer<out Event<*>>
    }

    override fun locateAsMsgGet(type: Event.Type, extraType: String): KSerializer<out MsgGet>? {
        val coordinate = EventSerializerCoordinate(type, extraType)

        @Suppress("UNCHECKED_CAST")
        return eventCoordinateMap[coordinate] as? KSerializer<MsgGet>
    }


    override fun <T> registerCoordinate(
        type: Event.Type,
        extraType: String,
        serializer: KSerializer<out T>,
    ): KSerializer<*> where T : Event<*>, T : MsgGet {
        val coordinate = EventSerializerCoordinate(type, extraType)

        return eventCoordinateMap.computeIfAbsent(coordinate) { serializer }
    }


    /**
     * 事件的定位坐标。
     */
    private data class EventSerializerCoordinate(
        val type: Event.Type,
        val extraType: String,
    )
}