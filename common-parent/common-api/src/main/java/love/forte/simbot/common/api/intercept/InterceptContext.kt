/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     InterceptContext.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.common.api.intercept



/**
 * 基础上下文接口，一般来讲，一个拦截器都会有一个 **上下文**。
 */
public interface Context<T> {
    /**
     * 针对上下文中的值可以被获取。
     */
    val value: T

    /**
     * 可以从上下文对象中获取储存的值。
     * 这些值会在当前一次拦截流程中被传递。
     *
     * @return value if exists.
     */
    operator fun get(key: String) : Any?

    /**
     * 可以向上下文对象中储存一些值。
     * 这些值会在当前一次拦截流程中被传递。
     *
     * @return old value if exists.
     */
    operator fun set(key: String, value: Any?) : Any?

    /**
     * 清空当前保存的值。
     */
    fun clear()
}


/**
 * 针对 [Context] 接口的基础实现父类。
 * 使用一个 [MutableMap] 最为额外值载体。
 */
@Suppress("RedundantVisibilityModifier", "unused")
public abstract class BaseContext<T>
@JvmOverloads
constructor(
    override val value: T,
    private val contextMap: MutableMap<String, Any?> = mutableMapOf()
) : _root_ide_package_.love.forte.simbot.common.api.intercept.Context<T> {
    /**
     * 可以从上下文对象中获取储存的值。
     * 这些值会在当前一次拦截流程中被传递。
     *
     * @return value if exists.
     */
    override fun get(key: String): Any? = contextMap[key]

    /**
     * 可以向上下文对象中储存一些值。
     * 这些值会在当前一次拦截流程中被传递。
     *
     * @return old value if exists.
     */
    override fun set(key: String, value: Any?): Any? = contextMap.put(key, value)


    /**
     * 清空当前保存的值。
     */
    override fun clear() {
        contextMap.clear()
    }
}






