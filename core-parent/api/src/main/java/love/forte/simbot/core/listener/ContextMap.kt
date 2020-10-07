/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     ContextMap.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.listener

import java.util.concurrent.ConcurrentHashMap

public interface ContextMap {

    /**
     * 当前context map.
     */
    val instant: MutableMap<String, Any?>

    /**
     * 全局global map.
     */
    val global: MutableMap<String, Any?>

    /**
     * 优先从当前instant中获取，否则从全局global中获取。
     */
    fun instantOrGlobal(key: String): Any?

    /**
     * 优先从全局global中获取，否则从当前instant中获取。
     */
    fun globalOrInstant(key: String): Any?

    /**
     * 优先从当前instant中获取，否则从全局global中获取，如果还没有则使用默认值。
     * @param def 都没有的时候使用的默认值。不可为null。
     */
    fun <V> instantOrGlobalOrDefault(key: String, def: V): V

    /**
     * 优先从全局global中获取，否则从当前instant中获取。
     * @param def 都没有的时候使用的默认值。不可为null。
     */
    fun <V> globalOrInstantOrDefault(key: String, def: V): V

    /**
     * 优先从当前instant中获取，否则从全局global中获取，如果还没有则使用默认值。
     * @param compute 都没有的时候使用key计算得到一个value。得到的值不可为null。
     */
    fun <V> instantOrGlobalOrCompute(key: String, compute: (String) -> V): V

    /**
     * 优先从全局global中获取，否则从当前instant中获取。
     * @param compute 都没有的时候使用key计算得到一个value。得到的值不可为null。
     */
    fun <V> globalOrInstantOrCompute(key: String, compute: (String) -> V): V
}



/**
 * [ContextMap] 工厂。获取一个contextMap。
 */
public interface ContextMapFactory {
    val contextMap: ContextMap
}






/**
 * [ListenerContext] 中所使用的上下文Map。
 *
 * @property instant 瞬时map，每次的监听函数都会是一个新的map。
 * @property global 全局map，不会因一次监听结局而消失。
 */
@Suppress("UNCHECKED_CAST", "MemberVisibilityCanBePrivate")
public class ContextMapImpl
constructor(override val instant: MutableMap<String, Any?>, override val global: MutableMap<String, Any?>) :
    ContextMap {
    /**
     * 优先从当前instant中获取，否则从全局global中获取。
     */
    override fun instantOrGlobal(key: String): Any? = instant[key] ?: global[key]

    /**
     * 优先从全局global中获取，否则从当前instant中获取。
     */
    override fun globalOrInstant(key: String): Any? = global[key] ?: instant[key]

    /**
     * 优先从当前instant中获取，否则从全局global中获取，如果还没有则使用默认值。
     * @param def 都没有的时候使用的默认值。不可为null。
     */
    override fun <V> instantOrGlobalOrDefault(key: String, def: V): V = (instant[key] ?: global[key] ?: def) as V

    /**
     * 优先从全局global中获取，否则从当前instant中获取。
     * @param def 都没有的时候使用的默认值。不可为null。
     */
    override fun <V> globalOrInstantOrDefault(key: String, def: V): V = (global[key] ?: instant[key] ?: def) as V

    /**
     * 优先从当前instant中获取，否则从全局global中获取，如果还没有则使用默认值。
     * @param compute 都没有的时候使用key计算得到一个value。得到的值不可为null。
     */
    override fun <V> instantOrGlobalOrCompute(key: String, compute: (String) -> V): V =
        (instant[key] ?: global[key] ?: compute(key)) as V

    /**
     * 优先从全局global中获取，否则从当前instant中获取。
     * @param compute 都没有的时候使用key计算得到一个value。得到的值不可为null。
     */
    override fun <V> globalOrInstantOrCompute(key: String, compute: (String) -> V): V =
        (global[key] ?: instant[key] ?: compute(key)) as V
}