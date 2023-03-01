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

package love.forte.simboot.core.listener

import love.forte.simboot.core.application.Boot
import love.forte.simboot.listener.ParameterBinder
import love.forte.simbot.Attribute
import love.forte.simbot.attribute
import love.forte.simbot.event.Event
import love.forte.simbot.event.EventListener
import kotlin.reflect.KFunction

/**
 * 提供部分在 boot 相关模块（包括SpringBoot相关）下所可能提供的监听函数属性。
 */
@Suppress("MemberVisibilityCanBePrivate")
public object BootListenerAttributes {
    
    // region raw function
    /**
     * [RawFunction] 的属性名。
     */
    public const val RAW_FUNCTION_NAME: String = "\$boot.raw_function$"
    
    /**
     * 由 [Boot] 启动器 （中使用的 [KFunctionListenerProcessor] 解析器） 所提供，
     * 为其解析的监听函数提供此监听函数中所使用的原始的 [KFunction] 对象。
     *
     * [RawFunction] 属性值会被保存在 [EventListener] 中，
     * 可以通过 [EventListener.getAttribute] 获取。
     *
     * **Kotlin**
     * ```kotlin
     * val function: KFunction<*>? = listener.getAttribute(RAW_FUNCTION)
     * function?.also {
     *     // ...
     * }
     * ```
     *
     * **Java**
     * ```java
     * final KFunction<?> function = listener.getAttribute(BootListenerAttributes.RAW_FUNCTION);
     * if (function != null) {
     *     // ...
     * }
     * ```
     *
     * @see EventListener.rawFunction
     * @see EventListener.rawFunctionOrNull
     *
     */
    @JvmField
    public val RawFunction: Attribute<KFunction<*>> = attribute(RAW_FUNCTION_NAME)
    
    /**
     * 尝试从一个 [EventListener] 中获取 [RawFunction] 属性。
     * 如果使用的是simbot标准库中提供的boot相关实现（包括SpringBoot相关）则能够获取到此值，否则可能会无法得到。
     *
     * 当无法得到的时候会抛出 [NoSuchElementException] 异常。
     *
     * @throws NoSuchElementException 当无法得到时
     *
     */
    @JvmStatic
    public val EventListener.rawFunction: KFunction<*>
        get() = rawFunctionOrNull
            ?: throw NoSuchElementException("""BootListenerAttributes.RawFunction("$RAW_FUNCTION_NAME")""")
    
    
    /**
     * 尝试从一个 [EventListener] 中获取 [RawFunction] 属性。
     * 如果使用的是simbot标准库中提供的boot相关实现（包括SpringBoot相关）则能够获取到此值，否则可能会无法得到。
     *
     * 当无法得到的时候会得到 null。
     *
     */
    @JvmStatic
    public val EventListener.rawFunctionOrNull: KFunction<*>? get() = getAttribute(RawFunction)
    // endregion
    
    // region raw binders
    /**
     * [RawBinders] 的属性名。
     */
    public const val RAW_BINDERS_NAME: String = "\$boot.raw_binders$"
    
    /**
     * 由 [Boot] 启动器 （中使用的 [KFunctionListenerProcessor] 解析器） 所提供，
     * 为其解析的监听函数提供此监听函数中所使用的原始的 [参数绑定器][ParameterBinder] 集合。
     *
     * [RawBinders] 属性值会被保存在 [EventListener] 中，
     * 可以通过 [EventListener.getAttribute] 获取。
     *
     * **Kotlin**
     * ```kotlin
     * val binders: Collection<ParameterBinder>? = listener.getAttribute(RAW_BINDERS)
     * binders?.forEach {
     *     // ...
     * }
     * ```
     *
     * **Java**
     * ```java
     * final Collection<ParameterBinder> binders = listener.getAttribute(BootListenerAttributes.RAW_BINDERS);
     * if (binders != null) {
     *     // ...
     * }
     * ```
     *
     *
     *
     * _Note: 此属性所得列表仅为初始化目标监听函数时候所使用的 [binders][ParameterBinder] 列表的 **副本**，其变化不会影响到监听函数的内部，内部假若发生变化也不会影响到此属性得到的集合。_
     *
     */
    @JvmField
    public val RawBinders: Attribute<Collection<ParameterBinder>> = attribute(RAW_BINDERS_NAME)
    
    /**
     * 尝试从一个 [EventListener] 中获取 [RawBinders] 属性。
     * 如果使用的是simbot标准库中提供的boot相关实现（包括SpringBoot相关）则能够获取到此值，否则可能会无法得到。
     *
     * 当无法得到的时候会抛出 [NoSuchElementException] 异常。
     *
     * @throws NoSuchElementException 当无法得到时
     *
     */
    @JvmStatic
    public val EventListener.rawBinders: Collection<ParameterBinder>
        get() = rawBindersOrNull
            ?: throw NoSuchElementException("""BootListenerAttributes.RawBinders("$RAW_BINDERS_NAME")""")
    
    
    /**
     * 尝试从一个 [EventListener] 中获取 [RawBinders] 属性。
     * 如果使用的是simbot标准库中提供的boot相关实现（包括SpringBoot相关）则能够获取到此值，否则可能会无法得到。
     *
     * 当无法得到的时候会得到 null。
     */
    @JvmStatic
    public val EventListener.rawBindersOrNull: Collection<ParameterBinder>?
        get() = getAttribute(RawBinders)
    // endregion
    
    // region raw listen targets
    /**
     * [RawListenTargets] 的属性名。
     */
    public const val RAW_LISTEN_TARGETS_NAME: String = "\$BOOT.raw_listen_targets$"
    
    /**
     * 由 [Boot] 启动器 （中使用的 [KFunctionListenerProcessor] 解析器） 所提供，
     * 为其解析的监听函数提供此监听函数中所使用的原始的 [监听事件类型][Event.Key] 集合。
     *
     * [RawListenTargets] 属性值会被保存在 [EventListener] 中，
     * 可以通过 [EventListener.getAttribute] 获取。
     *
     * **Kotlin**
     * ```kotlin
     * val targets: Collection<Event.Key<*>>? = listener.getAttribute(RAW_LISTEN_TARGETS)
     * targets?.forEach {
     *     // ...
     * }
     * ```
     *
     * **Java**
     * ```java
     * final Collection<Event.Key<?>> targets = listener.getAttribute(BootListenerAttributes.RAW_LISTEN_TARGETS);
     * if (targets != null) {
     *     // ...
     * }
     * ```
     *
     *
     * _Note: 此属性所得列表仅为初始化目标监听函数时候所使用的 [targets][Event.Key] 集合的 **副本**，其变化不会影响到监听函数的内部，内部假若发生变化也不会影响到此属性得到的集合。_
     *
     */
    @JvmField
    public val RawListenTargets: Attribute<Collection<Event.Key<*>>> = attribute(RAW_LISTEN_TARGETS_NAME)
    
    /**
     * 尝试从一个 [EventListener] 中获取 [RawListenTargets] 属性。
     * 如果使用的是simbot标准库中提供的boot相关实现（包括SpringBoot相关）则能够获取到此值，否则可能会无法得到。
     *
     * 当无法得到的时候会抛出 [NoSuchElementException] 异常。
     *
     * @throws NoSuchElementException 当无法得到时
     *
     */
    @JvmStatic
    public val EventListener.rawListenTargets: Collection<Event.Key<*>>
        get() = rawListenTargetsOrNull
            ?: throw NoSuchElementException("""BootListenerAttributes.RawListenTargets("$RAW_LISTEN_TARGETS_NAME")""")
    
    
    /**
     * 尝试从一个 [EventListener] 中获取 [RawListenTargets] 属性。
     * 如果使用的是simbot标准库中提供的boot相关实现（包括SpringBoot相关）则能够获取到此值，否则可能会无法得到。
     *
     * 当无法得到的时候会得到 null。
     */
    @JvmStatic
    public val EventListener.rawListenTargetsOrNull: Collection<Event.Key<*>>?
        get() = getAttribute(RawListenTargets)
    // endregion
}
