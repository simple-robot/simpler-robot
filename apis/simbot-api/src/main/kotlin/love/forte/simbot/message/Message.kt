/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  根据 GNU LESSER GENERAL PUBLIC LICENSE 3 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

@file:Suppress("unused")

package love.forte.simbot.message

import kotlinx.serialization.Serializable
import love.forte.simbot.Component
import love.forte.simbot.ComponentContainer
import love.forte.simbot.ID
import love.forte.simbot.SimbotComponent
import love.forte.simbot.event.Event
import love.forte.simbot.message.Message.Element
import kotlin.reflect.KClass
import kotlin.reflect.safeCast


/**
 * 消息。
 *
 * @see Element
 * @see Messages
 * @see SingleOnlyMessage
 */
public sealed interface Message {

    /**
     * 一个 [消息][Message] 的 [元素][Element], 元素本身也是一种消息。
     *
     * 需尽量保证实现类是可序列化的。
     *
     * @see SingleOnlyMessage 约束一个消息列表中仅只能存在此一种消息元素的消息。
     */
    public interface Element<E : Element<E>> : Message, ComponentContainer {
        public val key: Key<E>

        /**
         * 每个消息，都有一个所属的组件。组件之间不应出现消息交叉。
         *
         * @see SimbotComponent
         */
        override val component: Component get() = key.component
        override fun toString(): String
        override fun equals(other: Any?): Boolean
    }

    /**
     * 消息元素类型的唯一表示标识。
     *
     * 一般由伴生对象或对象实现。
     *
     */
    public interface Key<E : Element<E>> : ComponentContainer {
        /**
         * 任何消息都应由某个组件所提供。
         * 在检测冲突的前提是组件应当一致。
         */
        override val component: Component

        /**
         * 得到此元素的 [KClass].
         */
        public val elementType: KClass<E>

        /**
         * 将一个实例转化为 [E] 实例。 无法转化得到null。
         *
         * *Just like JVM KClass::safeCast.*
         */
        public fun safeCast(instance: Any?): E? = elementType.safeCast(instance)

    }

    /**
     * 一个消息的 [元数据][Metadata].
     * 元数据中，可能存在很多这个消息的原始基础信息，比如唯一标识、接收时间等等。
     *
     * [Metadata] 应当能够用于对一个消息进行唯一定位。
     *
     * 具体内容由实现者决定，但是 [Metadata] 至少要能提供出一个唯一[id].
     *
     * [id] 一般用于判断两个的 [Metadata] 是否不同，来代表 [Metadata] 的唯一性。
     * 而其他非唯一数据则不需要保存至 [id] 中。
     *
     *
     * @see ID
     * @see MessageContent
     */
    @Serializable
    public abstract class Metadata {
        public abstract val id: ID
        override fun equals(other: Any?): Boolean {
            if (other === this) return true
            if (other !is Event.Metadata) return false

            return id == other.id
        }

        override fun hashCode(): Int = id.hashCode()
    }


}


public inline fun <reified E : Element<E>> Message.Key<E>.cast(value: Any?): E {
    if (value == null) throw NullPointerException("cast value")
    if (value !is E) throw ClassCastException("Value cannot be cast to ${E::class.simpleName}")
    return value
}


public inline fun <reified E> doSafeCast(value: Any): E? = if (value is E) value else null
public inline fun <reified E> doCast(value: Any): E =
    doSafeCast<E>(value) ?: throw ClassCastException("${value::class} cannot cast to type ${E::class}")







