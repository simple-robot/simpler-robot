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

package love.forte.simbot.event

import love.forte.simbot.*
import java.util.stream.Stream
import kotlin.streams.asStream


/**
 * 监听函数注册器
 */
public interface EventListenerRegistrar {
    /**
     * 注册一个监听函数。对于注册的其他附加属性均采用默认值.
     *
     * @return 被注册的 [listener] 在当前容器中的句柄.
     */
    @ExperimentalSimbotApi
    public fun register(listener: EventListener): EventListenerHandle
    
    /**
     * 注册一个监听函数。
     *
     * @return 被注册的 [listener][EventListenerRegistrationDescription.listener] 在当前容器中的句柄.
     */
    @ExperimentalSimbotApi
    public fun register(registrationDescription: EventListenerRegistrationDescription): EventListenerHandle
    
}


/**
 * 用于向 [EventListenerRegistrar] 中注册监听函数的信息描述. 可以在 [EventListener]
 * 之外提供更多可能有效的附加信息.
 *
 * 除了 [listener] 以外, 其他的所有属性信息均**不保证**一定会被 [EventListenerRegistrar] 的具体实现所处理,
 * 对于它们是否能够被应用, 需要查阅具体实现的说明.
 *
 */
public abstract class EventListenerRegistrationDescription {
    
    /**
     * 注册信息中的监听函数.
     */
    public abstract val listener: EventListener
    
    /**
     * 此监听函数的优先级. 默认为 [DEFAULT_PRIORITY].
     *
     * @see PriorityConstant
     */
    public open var priority: Int = DEFAULT_PRIORITY
    
    /**
     * 当前的监听函数是否要在异步环境中使用. 默认为 [DEFAULT_ASYNC].
     */
    public open var isAsync: Boolean = DEFAULT_ASYNC
    
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        
        other as EventListenerRegistrationDescription
        
        if (listener != other.listener) return false
        if (priority != other.priority) return false
        if (isAsync != other.isAsync) return false
        
        return true
    }
    
    override fun hashCode(): Int {
        var result = listener.hashCode()
        result = 31 * result + priority
        result = 31 * result + isAsync.hashCode()
        return result
    }
    
    
    override fun toString(): String {
        return "EventListenerRegistrationDescription(listener=$listener, priority=$priority, isAsync=$isAsync)"
    }
    
    public companion object {
        public const val DEFAULT_PRIORITY: Int = PriorityConstant.NORMAL
        public const val DEFAULT_ASYNC: Boolean = false
        
        /**
         * 通过 [EventListener] 构建一个 [EventListenerRegistrationDescription].
         * ```kotlin
         * listener.toRegistrationDescription {
         *     priority = PriorityConstant.FIRST
         *     isAsync = true
         * }
         * ```
         */
        @JvmSynthetic
        public fun EventListener.toRegistrationDescription(block: EventListenerRegistrationDescription.() -> Unit): EventListenerRegistrationDescription {
            return SimpleEventListenerRegistrationDescription(this).also(block)
        }
        
        /**
         * 通过 [EventListener] 构建一个 [EventListenerRegistrationDescription].
         * ```kotlin
         * listener.toRegistrationDescription(priority = priority, isAsync = isAsync)
         * ```
         */
        @JvmName("of")
        @JvmOverloads
        @JvmStatic
        public fun EventListener.toRegistrationDescription(
            priority: Int = DEFAULT_PRIORITY,
            isAsync: Boolean = DEFAULT_ASYNC,
        ): EventListenerRegistrationDescription {
            return SimpleEventListenerRegistrationDescription(this).apply {
                this.priority = priority
                this.isAsync = isAsync
            }
        }
    }
    
}


private data class SimpleEventListenerRegistrationDescription(
    override val listener: EventListener,
    override var priority: Int = DEFAULT_PRIORITY,
    override var isAsync: Boolean = DEFAULT_ASYNC,
) : EventListenerRegistrationDescription()


/**
 *
 * 监听函数容器。
 *
 * @author ForteScarlet
 */
public interface EventListenerContainer : EventListenerRegistrar {
    
    /**
     * 得到当前容器中的所有监听函数序列.
     *
     * _Java see [getListeners]._
     *
     * **※ 实验性: 未来可能会产生任何不兼容变更或被移除, 请谨慎使用.**
     *
     */
    @ExperimentalSimbotApi
    @get:JvmSynthetic
    public val listeners: Sequence<EventListener>
    
    
    /**
     * 得到当前容器中的所有监听函数序列.
     *
     * **※ 实验性: 未来可能会产生任何不兼容变更或被移除, 请谨慎使用.**
     *
     */
    @ExperimentalSimbotApi
    @Api4J
    public fun getListeners(): Stream<EventListener> = listeners.asStream()
}


/**
 * 事件监听器管理器标准接口。
 *
 */
public interface EventListenerManager : EventProcessor, EventListenerContainer


/**
 * 无匹配监听函数异常.
 *
 * @param appellation 此监听函数的称呼
 */
public open class NoSuchEventListenerException(appellation: String) : SimbotError,
    NoSuchElementException(appellation)
