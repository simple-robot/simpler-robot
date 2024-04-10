/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library (Alias: simple-robot, simbot, etc.).
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     Lesser GNU General Public License for more details.
 *
 *     You should have received a copy of the Lesser GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package love.forte.simbot.event

import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import love.forte.simbot.common.function.ConfigurerFunction
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext


/**
 * 事件调度器。
 * [EventDispatcher] 拥有 [EventProcessor] 和 [EventListenerRegistrar] 的职责，
 * 是对事件调度、事件监听器管理的统一单元。
 *
 * @author ForteScarlet
 */
public interface EventDispatcher : EventProcessor, EventListenerRegistrar, EventListenerContainer {
    // ..?
}

/**
 * DSL marker for [EventDispatcherConfiguration]
 */
@Retention(AnnotationRetention.BINARY)
@DslMarker
public annotation class EventDispatcherConfigurationDSL

/**
 * 针对 [EventDispatcher] 的基础配置类信息。
 * 不同的 [EventDispatcher] 可以对 [EventDispatcherConfiguration] 进行扩展。
 *
 * [EventDispatcherConfiguration] 最少也要满足一些所需的配置内容。
 * 最少也要在不支持的情况下给出警告日志或异常。
 *
 */
public interface EventDispatcherConfiguration {

    /**
     * 用于 [EventDispatcher] 中进行事件调度的协程上下文。每一次事件处理都会被切换至此上下文中。
     * [coroutineContext] 会作为调度事件时候使用的调度器，
     * 事件在处理的时候会被切换至此上下文中（例如使用其中的调度器等）。
     *
     * 如果 [coroutineContext] 中存在 [Job]，则此 [Job] 会**被剔除**。
     * [EventDispatcher] 的调度结果最终以流 [Flow] 的形式提供，
     * 因此每次调度任务的生命周期由接收者决定，[Job] 不起作用（[Flow] 的调度上下文中也不允许 [Job] 存在）。
     *
     * 当同时使用 [coroutineContext] 和 [Flow.flowOn] 对时间调度流指定调度器时，
     * [coroutineContext] 会优先生效于事件调度逻辑本身。
     *
     * 注：当配置了 [coroutineContext]，那么每次进行事件调度的时候都会产生大量的上下文切换行为。
     * 切换的次数取决于事件处理器的数量和事件调度链结果流最终被收集的数量。
     *
     */
    public var coroutineContext: CoroutineContext

    /**
     * 添加一个拦截器与它可能存在的配置信息。
     */
    @EventDispatcherConfigurationDSL
    public fun addInterceptor(
        propertiesConsumer: ConfigurerFunction<EventInterceptorRegistrationProperties>?,
        interceptor: EventInterceptor
    )

    /**
     * 添加一个拦截器与它可能存在的配置信息。
     */
    @EventDispatcherConfigurationDSL
    public fun addInterceptor(interceptor: EventInterceptor) {
        addInterceptor(null, interceptor)
    }


    /**
     * 添加一个事件调度拦截器与它可能存在的配置信息。
     */
    @EventDispatcherConfigurationDSL
    public fun addDispatchInterceptor(
        propertiesConsumer: ConfigurerFunction<EventDispatchInterceptorRegistrationProperties>?,
        interceptor: EventDispatchInterceptor
    )

    /**
     * 添加一个事件调度拦截器与它可能存在的配置信息。
     */
    @EventDispatcherConfigurationDSL
    public fun addDispatchInterceptor(
        interceptor: EventDispatchInterceptor
    ) {
        addDispatchInterceptor(null, interceptor)
    }
}


/**
 * [EventDispatcherConfiguration] 的基础抽象类，提供 [EventDispatcherConfiguration] 中基本能力的部分实现或抽象。
 *
 * @see EventDispatcherConfiguration
 */
public abstract class AbstractEventDispatcherConfiguration : EventDispatcherConfiguration {
    override var coroutineContext: CoroutineContext = EmptyCoroutineContext

    //region interceptors
    protected open val interceptors:
        MutableList<Pair<EventInterceptor, ConfigurerFunction<EventInterceptorRegistrationProperties>?>> =
        mutableListOf()

    protected open val dispatchInterceptors:
        MutableList<
            Pair<
                EventDispatchInterceptor,
                ConfigurerFunction<EventDispatchInterceptorRegistrationProperties>?
                >
            > = mutableListOf()

    override fun addInterceptor(
        propertiesConsumer: ConfigurerFunction<EventInterceptorRegistrationProperties>?,
        interceptor: EventInterceptor
    ) {
        interceptors.add(interceptor to propertiesConsumer)
    }

    override fun addDispatchInterceptor(
        propertiesConsumer: ConfigurerFunction<EventDispatchInterceptorRegistrationProperties>?,
        interceptor: EventDispatchInterceptor
    ) {
        dispatchInterceptors.add(interceptor to propertiesConsumer)
    }
    //endregion

}


