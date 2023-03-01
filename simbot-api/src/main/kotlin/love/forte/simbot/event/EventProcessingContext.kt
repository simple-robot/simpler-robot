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

import kotlinx.serialization.modules.SerializersModule
import love.forte.simbot.Attribute
import love.forte.simbot.MutableAttributeMap
import love.forte.simbot.event.EventProcessingResult.Empty.resultsView
import love.forte.simbot.utils.view.IndexAccessView
import org.jetbrains.annotations.UnmodifiableView
import kotlin.coroutines.CoroutineContext


/**
 *
 * 整个事件流程中进行传递的上下文。
 *
 * 此流程上下文由事件被触发开始，从头至尾完成参与完成流程下各个节点的信息传递。
 *
 * 事件流程中进行流转的上下文也是一个协程上下文.
 * @author ForteScarlet
 */
public interface EventProcessingContext : CoroutineContext.Element, InstantScopeContext {
    public companion object Key : CoroutineContext.Key<EventProcessingContext>
    
    override val key: CoroutineContext.Key<*> get() = Key
    
    
    /**
     * 本次监听流程中的事件主体。
     */
    public val event: Event
    
    /**
     * 已经执行过的所有监听函数的结果视图的二次列表收集。
     * 此列表仅由事件处理器内部操作，是一个对外不可变视图。
     *
     * 请使用 [resultsView], 此api会在适当时机被删除。
     * @see resultsView
     */
    @Deprecated("Use 'resultsView'", replaceWith = ReplaceWith("resultsView"), level = DeprecationLevel.ERROR)
    public val results: @UnmodifiableView List<EventResult> get() = resultsView.toList()
    
    /**
     * 本次流程下执行后得到的所有响应结果的视图。按照顺序计入。
     */
    public val resultsView: IndexAccessView<EventResult>
    
    /**
     * 当前事件所处环境中所能够提供的消息序列化模块信息。
     */
    public val messagesSerializersModule: SerializersModule
    
    
    /**
     * 根据一个 [Attribute] 得到一个属性。
     *
     * 其中，除了具有特殊规则的属性
     * （ 例如 [SimpleScope][love.forte.simbot.core.scope.SimpleScope] 中的部分作用域属性 ）
     * 以外的所有内容都是**瞬时的**, 只会存在于当前上下文。
     *
     */
    override fun <T : Any> getAttribute(attribute: Attribute<T>): T?
    
}


/**
 * 作用域上下文，提供部分贯穿事件的作用域参数信息。
 */
public interface ScopeContext : MutableAttributeMap


/**
 * 全局作用域上下文，代表以事件处理器为作用域唯一实例的上下文类型。
 */
public interface GlobalScopeContext : ScopeContext


/**
 * 瞬时作用域上下文，由 [EventProcessingContext] 直接实现。
 */
public interface InstantScopeContext : ScopeContext


/**
 *
 * 每一个 [EventListener] 在事件处理流程中所对应的上下文类型。
 *
 * 相比较于 [EventProcessingContext],
 * [EventListenerProcessingContext] 允许监听函数在执行流程中获取当前（将要）被执行的监听函数自身 [listener]。
 *
 *
 */
public interface EventListenerProcessingContext : EventProcessingContext {
    override val event: Event
    override fun <T : Any> getAttribute(attribute: Attribute<T>): T?
    
    /**
     * 当前（将要）被执行的监听函数。
     */
    public val listener: EventListener
    
    /**
     * 当前 [listener] 在事件调度容器中的句柄.
     */
    public val listenerHandle: EventListenerHandle
    
    /**
     * 当前监听函数的主要文本内容，一般可用于在拦截器、过滤器、监听函数相互组合时进行一些过滤内容匹配。
     *
     * 正常来讲，[textContent] 在 [event] 为 [MessageEvent] 类型的时候，默认为 [MessageEvent.plainText][love.forte.simbot.message.MessageContent.plainText],
     * 其他情况下默认为null。
     *
     */
    public var textContent: String?
    
}
