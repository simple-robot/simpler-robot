/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x ) 的一部分。
 *
 *  simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *
 */

package love.forte.simbot.event.internal

import love.forte.simbot.Bot
import love.forte.simbot.CharSequenceID
import love.forte.simbot.ID
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
    abstract override val bot: Bot
    abstract override val metadata: Event.Metadata
    abstract override val key: Key<out InternalEvent>

    /**
     * 内部事件是当前应用程序所推送的伪事件，与实际业务无关，理论上只有bot自己能够收到此事件。
     * 因此默认情况下，[InternalEvent] 的访问级别属于 [Event.VisibleScope.PRIVATE].
     */
    override val visibleScope: Event.VisibleScope get() = Event.VisibleScope.PRIVATE

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
