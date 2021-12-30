/*
 *  Copyright (c) 2021-2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simbot.event

import love.forte.simbot.Bot
import love.forte.simbot.CharSequenceID
import love.forte.simbot.ID
import love.forte.simbot.message.doSafeCast

/**
 *
 * **内部** 事件。
 *
 * 一个 [内部事件][InternalEvent] 代表此事件仅由当前程序内部进行推送并使用，不涉及真正的对外事件。
 *
 * 内部事件应当是一个独立的事件体系，不能继承任何非内部事件的其他事件。
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
    override val id: CharSequenceID = idValue.ID

    override fun toString(): String = "InternalEventKey(id=$id)"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Event.Key<*>) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}
