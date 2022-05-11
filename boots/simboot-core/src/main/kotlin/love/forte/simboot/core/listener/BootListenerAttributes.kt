@file:JvmName("BootListenerAttributes")

package love.forte.simboot.core.listener

import love.forte.simboot.core.application.Boot
import love.forte.simboot.listener.ParameterBinder
import love.forte.simbot.Attribute
import love.forte.simbot.attribute
import love.forte.simbot.event.Event
import love.forte.simbot.event.EventListener
import kotlin.reflect.KFunction

fun a(listener: EventListener) {
    val targets: Collection<Event.Key<*>>? = listener.getAttribute(RAW_LISTEN_TARGETS)
    targets?.forEach {
        // ...
    }
}

/**
 * 由 [Boot] 启动器 （中使用的 [KFunctionListenerProcessor] 解析器） 所提供，
 * 为其解析的监听函数提供此监听函数中所使用的原始的 [KFunction] 对象。
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
 */
@JvmField
public val RAW_FUNCTION: Attribute<KFunction<*>> = attribute("\$BOOT\$RAW_FUNCTION")


/**
 * 由 [Boot] 启动器 （中使用的 [KFunctionListenerProcessor] 解析器） 所提供，
 * 为其解析的监听函数提供此监听函数中所使用的原始的 [参数绑定器][ParameterBinder] 集合。
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
public val RAW_BINDERS: Attribute<Collection<ParameterBinder>> = attribute("\$BOOT\$RAW_BINDER")


/**
 * 由 [Boot] 启动器 （中使用的 [KFunctionListenerProcessor] 解析器） 所提供，
 * 为其解析的监听函数提供此监听函数中所使用的原始的 [监听事件类型][Event.Key] 集合。
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
public val RAW_LISTEN_TARGETS: Attribute<Collection<Event.Key<*>>> = attribute("\$BOOT\$RAW_LISTEN_TARGETS")



