/*
 * Copyright (c) 2022-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.event

import kotlinx.coroutines.CancellationException
import love.forte.simbot.Api4J
import love.forte.simbot.utils.runWithInterruptible


// region base listener
/**
 *
 * 相当于函数 `EventProcessingContext.(ContinuousSessionProvider<T>) -> Unit`。
 *
 * 对于Java使用者可以考虑使用 [BlockingContinuousSessionSelector]。
 *
 * @see BlockingContinuousSessionSelector
 */
public fun interface ContinuousSessionSelector<T> {
    @JvmSynthetic
    public suspend operator fun EventProcessingContext.invoke(provider: ContinuousSessionProvider<T>)
}

/**
 * 阻塞的 [ContinuousSessionSelector].
 */
@Api4J
public fun interface BlockingContinuousSessionSelector<T> {
    
    /**
     * 执行一个含义等同于 [ContinuousSessionSelector.invoke] 的阻塞函数。
     *
     * @throws CancellationException 执行被终止
     */
    public operator fun invoke(context: EventProcessingContext, provider: ContinuousSessionProvider<T>)
}


@OptIn(Api4J::class)
internal fun <T> BlockingContinuousSessionSelector<T>.parse(): ContinuousSessionSelector<T> =
    ContinuousSessionSelector { provider -> runWithInterruptible { this@parse(this, provider) } }
// endregion

// region event matcher
/**
 * 使用于 [ContinuousSessionContext.waitingForNext] 中的事件匹配器。
 *
 * 提供一个 [Event] 来判断其是否符合条件。
 *
 * 阻塞实现参考 [BlockingContinuousSessionEventMatcher].
 *
 */
public fun interface ContinuousSessionEventMatcher<in E : Event> {
    
    /**
     * 根据条件判断结果。
     */
    @JvmSynthetic
    public suspend operator fun EventProcessingContext.invoke(event: E): Boolean
    
    
    public companion object AlwaysTrue : ContinuousSessionEventMatcher<Event> {
        override suspend fun EventProcessingContext.invoke(event: Event): Boolean {
            return true
        }
    }
    
}

/**
 * 使用于 [ContinuousSessionContext.waitingForNext] 中的事件匹配器。
 *
 * 提供一个 [Event] 来判断其是否符合条件。
 *
 */
@Api4J
public fun interface BlockingContinuousSessionEventMatcher<in E : Event> {
    
    /**
     * 根据条件判断结果。
     */
    public operator fun EventProcessingContext.invoke(event: E): Boolean
    
    
    public companion object AlwaysTrue : BlockingContinuousSessionEventMatcher<Event> {
        override fun EventProcessingContext.invoke(event: Event): Boolean {
            return true
        }
    }
    
}

@OptIn(Api4J::class)
internal fun <E : Event> BlockingContinuousSessionEventMatcher<E>.parse(): ContinuousSessionEventMatcher<E> {
    return if (this === BlockingContinuousSessionEventMatcher.AlwaysTrue) {
        ContinuousSessionEventMatcher.AlwaysTrue
    } else {
        ContinuousSessionEventMatcher { event -> runWithInterruptible { invoke(event) } }
    }
}


// endregion
