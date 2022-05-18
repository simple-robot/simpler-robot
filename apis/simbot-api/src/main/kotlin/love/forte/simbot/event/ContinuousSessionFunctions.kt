package love.forte.simbot.event

import kotlinx.coroutines.CancellationException
import love.forte.simbot.Api4J
import love.forte.simbot.utils.runWithInterruptible


// region base listener
/**
 *
 * 相当于函数 `EventProcessingContext.(ContinuousSessionProvider<T>) -> Unit`。
 *
 * 对于Java使用者可以考虑使用 [BlockingResumeListener]。
 *
 * @see BlockingResumeListener
 * @see ClearTargetResumeListener
 * @see BlockingClearTargetResumeListener
 */
public fun interface ResumeListener<T> {
    public suspend operator fun EventProcessingContext.invoke(provider: ContinuousSessionProvider<T>)
}

/**
 * 阻塞的 [ResumeListener].
 */
@Api4J
public fun interface BlockingResumeListener<T> {
    
    /**
     * 执行一个含义等同于 [ResumeListener.invoke] 的阻塞函数。
     *
     * @throws CancellationException 执行被终止
     */
    public operator fun invoke(context: EventProcessingContext, provider: ContinuousSessionProvider<T>)
}


@OptIn(Api4J::class)
internal fun <T> BlockingResumeListener<T>.parse(): ResumeListener<T> =
    ResumeListener { provider -> runWithInterruptible { this@parse(this, provider) } }
// endregion

// region event matcher
/**
 * 使用于 [ContinuousSessionContext.waitingForNext] 中的事件匹配器。
 *
 * 提供一个 [Event] 来判断其是否符合条件。
 *
 * 阻塞实现参考 [BlockingEventMatcher].
 *
 */
public fun interface EventMatcher<in E : Event> {
    
    /**
     * 根据条件判断结果。
     */
    @JvmSynthetic
    public suspend operator fun EventProcessingContext.invoke(event: E): Boolean
    
    
    public companion object AlwaysTrue : EventMatcher<Event> {
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
public fun interface BlockingEventMatcher<in E : Event> {
    
    /**
     * 根据条件判断结果。
     */
    public operator fun EventProcessingContext.invoke(event: E): Boolean
    
    
    public companion object AlwaysTrue : BlockingEventMatcher<Event> {
        override fun EventProcessingContext.invoke(event: Event): Boolean {
            return true
        }
    }
    
}

@OptIn(Api4J::class)
internal fun <E : Event> BlockingEventMatcher<E>.parse(): EventMatcher<E> {
    return if (this === BlockingEventMatcher.AlwaysTrue) {
        EventMatcher
    } else {
        EventMatcher { event -> runWithInterruptible { invoke(event) } }
    }
}


// endregion


/**
 * 有着明确监听目标的 [ResumeListener]。
 *
 * @see BlockingClearTargetResumeListener
 */
public fun interface ClearTargetResumeListener<E : Event, T> {
    public suspend operator fun EventProcessingContext.invoke(
        event: E,
        provider: ContinuousSessionProvider<T>,
    )
}


/**
 * 有着明确监听目标的 [ResumeListener]。需要考虑重写 [invoke] 来实现事件类型的准确转化。
 *
 * @see ClearTargetResumeListener
 * @see ResumeListener
 */
@Api4J
public fun interface BlockingClearTargetResumeListener<E : Event, T> {
    public operator fun invoke(event: E, context: EventProcessingContext, provider: ContinuousSessionProvider<T>)
    
}


@OptIn(Api4J::class)
internal fun <E : Event, T> BlockingClearTargetResumeListener<E, T>.parse(): ClearTargetResumeListener<E, T> =
    ClearTargetResumeListener { event, provider -> runWithInterruptible { this@parse(event, this, provider) } }


