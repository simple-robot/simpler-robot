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

package love.forte.simbot.core.event

import love.forte.simbot.event.*


/**
 * 事件相关拦截器入口。通过解析拦截器列表提供一个最终的入口。
 */
public sealed class EventInterceptEntrance<C : EventInterceptor.Context<R>, R, EC : EventProcessingContext> {
    @JvmSynthetic
    public abstract suspend fun doIntercept(
        context: EC,
        processing: suspend (EC) -> R
    ): R

    public companion object {
        /**
         * 得到事件流程拦截器入口。
         */
        public fun eventProcessingInterceptEntrance(interceptors: Collection<EventProcessingInterceptor>): EventInterceptEntrance<EventProcessingInterceptor.Context, EventProcessingResult, EventProcessingContext> {
            return if (interceptors.isEmpty()) EventProcessingDirectInterceptEntrance
            else EventProcessingIteratorInterceptEntrance(interceptors.toList())
        }

        /**
         * 得到监听函数拦截器入口。
         */
        public fun eventListenerInterceptEntrance(
            listener: EventListener,
            interceptors: Collection<EventListenerInterceptor>
        ): EventInterceptEntrance<EventListenerInterceptor.Context, EventResult, EventListenerProcessingContext> {
            return if (interceptors.isEmpty()) EventListenerDirectInterceptEntrance
            else EventListenerIteratorInterceptEntrance(listener, interceptors.toList())
        }
    }

    public sealed class IteratorInterceptorContext<C : EventInterceptor.Context<R>, I : EventInterceptor<C, R>, R, EC : EventProcessingContext>(
        private val interceptorsIterator: Iterator<I>,
        private val processing: suspend (EC) -> R
    ) {
        public abstract val eventContext: EC
        protected abstract val context: C

        protected suspend fun doProceed(): R {
            return if (interceptorsIterator.hasNext()) {
                interceptorsIterator.next().intercept(context)
            } else processing(eventContext)
        }


    }
}

//region Event processing intercept entrance
/**
 * 核心所实现的事件拦截器入口。提供拦截器迭代器列表，通过 doIntercept 提供真实逻辑。
 *
 */
internal class EventProcessingIteratorInterceptEntrance(
    internal val interceptorsIterable: Iterable<EventProcessingInterceptor>,
) : EventInterceptEntrance<EventProcessingInterceptor.Context, EventProcessingResult, EventProcessingContext>() {

    override suspend fun doIntercept(
        context: EventProcessingContext,
        processing: suspend (EventProcessingContext) -> EventProcessingResult
    ): EventProcessingResult {
        return IteratorInterceptorContext(context, processing).proceed()
    }

    internal inner class IteratorInterceptorContext(
        override val eventContext: EventProcessingContext,
        processing: suspend (EventProcessingContext) -> EventProcessingResult
    ) : EventInterceptEntrance.IteratorInterceptorContext<
            EventProcessingInterceptor.Context,
            EventProcessingInterceptor,
            EventProcessingResult,
            EventProcessingContext
            >(interceptorsIterable.iterator(), processing),
        EventProcessingInterceptor.Context {
        override val context: EventProcessingInterceptor.Context
            get() = this

        override suspend fun proceed(): EventProcessingResult = doProceed()
    }
}

internal object EventProcessingDirectInterceptEntrance
    : EventInterceptEntrance<EventProcessingInterceptor.Context, EventProcessingResult, EventProcessingContext>() {

    override suspend fun doIntercept(
        context: EventProcessingContext,
        processing: suspend (EventProcessingContext) -> EventProcessingResult
    ): EventProcessingResult = processing(context)
}
//endregion

//region Event Listener intercept entrance
/**
 * 核心所实现的监听函数拦截器入口。提供拦截器迭代器列表，通过 doIntercept 提供真实逻辑。
 *
 */
internal class EventListenerIteratorInterceptEntrance(
    internal val listener: EventListener,
    internal val interceptorsIterable: Iterable<EventListenerInterceptor>,
) : EventInterceptEntrance<EventListenerInterceptor.Context, EventResult, EventListenerProcessingContext>() {

    override suspend fun doIntercept(
        context: EventListenerProcessingContext,
        processing: suspend (EventListenerProcessingContext) -> EventResult
    ): EventResult {
        return IteratorInterceptorContext(context, processing).proceed()
    }

    suspend fun doIntercept(context: EventListenerProcessingContext): EventResult {
        return doIntercept(context, listener::invoke)
    }


    internal inner class IteratorInterceptorContext(
        override val eventContext: EventListenerProcessingContext,
        processing: suspend (EventListenerProcessingContext) -> EventResult
    ) : EventInterceptEntrance.IteratorInterceptorContext<
            EventListenerInterceptor.Context,
            EventListenerInterceptor,
            EventResult,
            EventListenerProcessingContext
            >(interceptorsIterable.iterator(), processing),
        EventListenerInterceptor.Context {

        override val context: EventListenerInterceptor.Context
            get() = this

        override suspend fun proceed(): EventResult = doProceed()
    }
}

internal object EventListenerDirectInterceptEntrance
    : EventInterceptEntrance<EventListenerInterceptor.Context, EventResult, EventListenerProcessingContext>() {

    override suspend fun doIntercept(
        context: EventListenerProcessingContext,
        processing: suspend (EventListenerProcessingContext) -> EventResult
    ): EventResult = processing(context)
}
//endregion

