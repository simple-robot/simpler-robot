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

package love.forte.simbot.core.event

import love.forte.simbot.event.*


/**
 * 事件相关拦截器入口。通过解析拦截器列表提供一个最终的入口。
 */
public abstract class EventInterceptEntrance<C : EventInterceptor.Context<R>, R, EC : EventProcessingContext> {
    @JvmSynthetic
    public abstract suspend fun doIntercept(
        context: EC,
        processing: suspend (EC) -> R,
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
         * 得到事件流程拦截器入口。
         */
        public fun eventProcessingInterceptEntrance(): EventInterceptEntrance<EventProcessingInterceptor.Context, EventProcessingResult, EventProcessingContext> =
            EventProcessingDirectInterceptEntrance
        
        /**
         * 得到监听函数拦截器入口。
         */
        public fun eventListenerInterceptEntrance(
            interceptors: Collection<EventListenerInterceptor>,
        ): EventInterceptEntrance<EventListenerInterceptor.Context, EventResult, EventListenerProcessingContext> {
            return if (interceptors.isEmpty()) EventListenerDirectInterceptEntrance
            else EventListenerIteratorInterceptEntrance(interceptors.toList())
        }
        
        /**
         * 得到监听函数拦截器入口。
         */
        public fun eventListenerInterceptEntrance(): EventInterceptEntrance<EventListenerInterceptor.Context, EventResult, EventListenerProcessingContext> =
            EventListenerDirectInterceptEntrance
    }
    
    public abstract class IteratorInterceptorContext<C : EventInterceptor.Context<R>, I : EventInterceptor<C, R>, R, EC : EventProcessingContext>(
        private val interceptorsIterator: Iterator<I>,
        private val processing: suspend (EC) -> R,
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

// region Event processing intercept entrance
/**
 * 核心所实现的事件拦截器入口。提供拦截器迭代器列表，通过 doIntercept 提供真实逻辑。
 *
 */
internal class EventProcessingIteratorInterceptEntrance(
    internal val interceptorsIterable: Iterable<EventProcessingInterceptor>,
) : EventInterceptEntrance<EventProcessingInterceptor.Context, EventProcessingResult, EventProcessingContext>() {
    
    override suspend fun doIntercept(
        context: EventProcessingContext,
        processing: suspend (EventProcessingContext) -> EventProcessingResult,
    ): EventProcessingResult {
        return IteratorInterceptorContext(context, processing).proceed()
    }
    
    internal inner class IteratorInterceptorContext(
        override val eventContext: EventProcessingContext,
        processing: suspend (EventProcessingContext) -> EventProcessingResult,
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
        processing: suspend (EventProcessingContext) -> EventProcessingResult,
    ): EventProcessingResult = processing(context)
}
// endregion

// region Event Listener intercept entrance
/**
 * 核心所实现的监听函数拦截器入口。提供拦截器迭代器列表，通过 doIntercept 提供真实逻辑。
 *
 */
internal class EventListenerIteratorInterceptEntrance(
    internal val interceptorsIterable: Iterable<EventListenerInterceptor>,
) : EventInterceptEntrance<EventListenerInterceptor.Context, EventResult, EventListenerProcessingContext>() {
    
    override suspend fun doIntercept(
        context: EventListenerProcessingContext,
        processing: suspend (EventListenerProcessingContext) -> EventResult,
    ): EventResult {
        return IteratorInterceptorContext(context, processing).proceed()
    }
    
    // suspend fun doIntercept(context: EventListenerProcessingContext): EventResult {
    //     return doIntercept(context, listener::invoke)
    // }
    
    
    internal inner class IteratorInterceptorContext(
        override val eventContext: EventListenerProcessingContext,
        processing: suspend (EventListenerProcessingContext) -> EventResult,
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
        processing: suspend (EventListenerProcessingContext) -> EventResult,
    ): EventResult = processing(context)
}
// endregion

