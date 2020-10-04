/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     ListenerContext.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.listener

import love.forte.simbot.core.intercept.Context
import java.util.concurrent.ConcurrentHashMap


/**
 * [ListenerContext] 中所使用的上下文Map。
 *
 * @property instant 瞬时map，每次的监听函数都会是一个新的map。
 * @property global 全局map，不会因一次监听结局而消失。
 */
@Suppress("UNCHECKED_CAST", "MemberVisibilityCanBePrivate")
public class ContextMap
constructor(val instant : MutableMap<String, Any?>, val global: MutableMap<String, Any?> = ConcurrentHashMap()) {
    /**
     * 优先从当前instant中获取，否则从全局global中获取。
     */
    fun instantOrGlobal(key: String): Any? = instant[key] ?: global[key]

    /**
     * 优先从全局global中获取，否则从当前instant中获取。
     */
    fun globalOrInstant(key: String): Any? = global[key] ?: instant[key]

    /**
     * 优先从当前instant中获取，否则从全局global中获取，如果还没有则使用默认值。
     * @param def 都没有的时候使用的默认值。不可为null。
     */
    fun <V> instantOrGlobalOrDefault(key: String, def: V): V = (instant[key] ?: global[key] ?: def) as V

    /**
     * 优先从全局global中获取，否则从当前instant中获取。
     * @param def 都没有的时候使用的默认值。不可为null。
     */
    fun <V> globalOrInstantOrDefault(key: String, def: V): V = (global[key] ?: instant[key] ?: def) as V

    /**
     * 优先从当前instant中获取，否则从全局global中获取，如果还没有则使用默认值。
     * @param compute 都没有的时候使用key计算得到一个value。得到的值不可为null。
     */
    fun <V> instantOrGlobalOrCompute(key: String, compute: (String) -> V): V = (instant[key] ?: global[key] ?: compute(key)) as V

    /**
     * 优先从全局global中获取，否则从当前instant中获取。
     * @param compute 都没有的时候使用key计算得到一个value。得到的值不可为null。
     */
    fun <V> globalOrInstantOrCompute(key: String, compute: (String) -> V): V = (global[key] ?: instant[key] ?: compute(key)) as V



}


/**
 *
 * 监听函数上下文。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
@Suppress("MemberVisibilityCanBePrivate")
class ListenerContext(val contextMap: ContextMap) : Context<ContextMap> {

    /**
     * 主体为一个 [ContextMap], 以提供监听函数间的信息传递。
     */
    override val mainValue: ContextMap get() = contextMap
}