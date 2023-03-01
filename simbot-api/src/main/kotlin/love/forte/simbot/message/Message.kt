/*
 * Copyright (c) 2021-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

@file:Suppress("unused")

package love.forte.simbot.message

import love.forte.simbot.action.SendSupport
import love.forte.simbot.message.Message.Element


/**
 * 消息。
 *
 * @see Element
 * @see Messages
 */
public sealed interface Message {
    
    /**
     * 一个 [消息][Message] 的 [元素][Element], 元素本身也是一种消息。
     *
     * 需尽量保证实现类是可序列化的。
     */
    public interface Element<out E : Element<E>> : Message {
        public val key: Key<E>
        
        override fun toString(): String
        override fun equals(other: Any?): Boolean
    }
    
    /**
     * 消息元素类型的唯一表示标识。
     *
     * 一般由伴生对象或对象实现。
     *
     */
    public interface Key<out E : Element<E>> {
        
        /**
         * 将一个实例转化为 [E] 实例。 无法转化得到null。
         *
         * *Just like JVM KClass::safeCast.*
         */
        public fun safeCast(value: Any): E?
        
    }
    
    
}

/**
 * @suppress 没什么实际用处的扩展函数，未来将考虑移除
 */
@Deprecated("Unused", level = DeprecationLevel.ERROR)
public inline fun <reified E : Element<E>> Message.Key<E>.cast(value: Any?): E {
    if (value == null) throw NullPointerException("cast value")
    if (value !is E) throw ClassCastException("Value cannot be cast to ${E::class.simpleName}")
    return value
}

/**
 * 将 [value] 安全的转化为类型 [E]，或者得到null。
 * 相当于 `value as? E`
 */
public inline fun <reified E> doSafeCast(value: Any): E? = value as? E // if (value is E) value else null

/**
 * 将 [value] 转化为类型 [E]，如果类型不匹配则抛出 [ClassCastException] 异常。
 *
 * @throws ClassCastException 类型不匹配时
 */
public inline fun <reified E> doCast(value: Any): E =
    doSafeCast<E>(value) ?: throw ClassCastException("${value::class} cannot cast to type ${E::class}")


/**
 * [SendSupport.send] 的反转形式，可以使用中缀函数写法。
 *
 * e.g.
 * ```kotlin
 * val message: Message = ...
 * val group: Group = ...
 *
 * val receipt = message sendTo group
 * ```
 *
 */
public suspend inline infix fun Message.sendTo(support: SendSupport): MessageReceipt = support.send(this)




