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

package love.forte.simbot.core.event

import love.forte.simbot.event.EventFilter
import love.forte.simbot.event.EventListener
import love.forte.simbot.event.EventProcessingContext
import love.forte.simbot.event.EventResult


/**
 * 向一个 [EventListener] 中拼接一个 [EventFilter].
 */
public operator fun EventListener.plus(filter: EventFilter): EventListener {
    return if (this is EventListenerWithFilter) {
        EventListenerWithFilter(filters + filter, delegate)
    } else {
        EventListenerWithFilter(listOf(filter), this)
    }
}

/**
 * 向一个 [EventListener] 中拼接多个 [EventFilter].
 */
public operator fun EventListener.plus(filters: Iterable<EventFilter>): EventListener {
    return if (this is EventListenerWithFilter) {
        EventListenerWithFilter((this.filters + filters).sortedBy { it.priority }, delegate)
    } else {
        EventListenerWithFilter(filters.sortedBy { it.priority }, this)
    }
}


/**
 * 通过提供一组过滤器来得到一个 [Listener][EventListener].
 */
internal class EventListenerWithFilter(
    internal val filters: List<EventFilter>,
    internal val delegate: EventListener
) : EventListener by delegate {
    override suspend fun invoke(context: EventProcessingContext): EventResult {
        for (filter in filters) {
            if (!filter.test(context)) return filter.defaultResult(context)
        }

        return delegate.invoke(context)
    }

    override fun toString(): String {
        return "$delegate with filter(s)(${filters.size})"
    }
}



