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

package love.forte.simbot.event.internal

import love.forte.simbot.CharSequenceID
import love.forte.simbot.ID
import love.forte.simbot.bot.Bot
import love.forte.simbot.event.Event
import love.forte.simbot.message.doSafeCast

/**
 *
 * **内部** 事件。
 *
 * 一个 [内部事件][InternalEvent] 代表此事件仅由当前程序内部进行推送并使用，不涉及真正的对外事件。
 *
 * 内部事件应当是一个独立的事件体系，不能继承任何非内部事件的其他事件。
 *
 *
 * 内部事件的实现由组件决定，无法保证每个组件都会对所有的内部事件提供相应的推送。
 *
 * @author ForteScarlet
 */
public abstract class InternalEvent : Event {
    abstract override val id: ID
    abstract override val bot: Bot
    abstract override val key: Key<out InternalEvent>

    public companion object Root : Key<InternalEvent> {
        override val id: CharSequenceID = "api.internal".ID
        override fun safeCast(value: Any): InternalEvent? = doSafeCast(value)
        override val parents: Set<Event.Key<*>>
            get() = emptySet()
    }

    /**
     * 内部事件的类型标识。所有 [InternalEvent] 只能继承与内部事件相关的事件。
     *
     */
    public interface Key<E : InternalEvent> : Event.Key<E> {
        override val parents: Set<Event.Key<*>>
    }
}

public abstract class BaseInternalKey<E : InternalEvent>(
    idValue: String,
    override val parents: Set<Event.Key<*>> = emptySet()
) : InternalEvent.Key<E> {
    public constructor(idValue: String, vararg parents: Event.Key<*>): this(idValue, parents.toSet())
    override val id: CharSequenceID = idValue.ID

    override fun toString(): String = "InternalEventKey(id=$id)"
    override fun equals(other: Any?): Boolean {

        if (this === other) return true
        if (other !is Event.Key<*>) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}
