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


/**
 *
 * 事件定位器，根据提供的数据参数得到一个 [Event] 所对应的序列器。
 *
 *
 * @author ForteScarlet
 */
public interface EventLocator {

    /**
     * 根据事件json中的部分数据来定位一个序列化器。
     *
     * @param type 外层类型，即[事件信令][Signal.Event] 中的 [d][Signal.Event.d] 数据中的 `type` 字段。
     * @param extraType extra中的类型，即 [Event.Extra.type]。
     *
     * @throws TypeCastException 在[注册定位][registerCoordinate] 时不规范导致的类型转化异常
     */
    fun locateAsEvent(type: Event.Type, extraType: String): KSerializer<out Event<*>>?

    /**
     * 根据事件json中的部分数据来定位一个序列化器。
     *
     * @param type 外层类型，即[事件信令][Signal.Event] 中的 [d][Signal.Event.d] 数据中的 `type` 字段。
     * @param extraType extra中的类型，即 [Event.Extra.type]。
     *
     * @throws TypeCastException 在[注册定位][registerCoordinate] 时不规范导致的类型转化异常
     */
    fun locateAsMsgGet(type: Event.Type, extraType: String): KSerializer<out MsgGet>?


    /**
     * 通过两个type属性注册一个序列化器。
     *
     * @return 如果此定位已经存在，返回被替代的那个元素。
     */
    fun <T> registerCoordinate(type: Event.Type, extraType: String, serializer: KSerializer<out T>): KSerializer<*>?
            where T : Event<*>, T : MsgGet


}


public fun EventLocator.locateAsEvent(type: Int, extraType: String): KSerializer<out Event<*>>? =
    locateAsEvent(Event.Type.byType(type), extraType)

public fun EventLocator.locateAsMsgGet(type: Int, extraType: String): KSerializer<out MsgGet>? =
    locateAsMsgGet(Event.Type.byType(type), extraType)


public fun <T> EventLocator.registerCoordinate(coordinate: EventLocatorRegistrarCoordinate<T>): KSerializer<*>? where T : Event<*>, T : MsgGet =
    registerCoordinate(coordinate.type, coordinate.extraType, coordinate.coordinateSerializer())


/**
 * 事件坐标注册器。
 */
public interface EventLocatorRegistrarCoordinate<T> where T : Event<*>, T : MsgGet {
    val type: Event.Type
    val extraType: String
    fun coordinateSerializer(): KSerializer<out T>
}


