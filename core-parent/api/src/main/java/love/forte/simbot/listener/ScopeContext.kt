/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simpler-robot
 *  * File     ScopeContext.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *  *
 *  *
 *
 */

@file:JvmName("ScopeContexts")

package love.forte.simbot.listener

import love.forte.simbot.Context
import love.forte.simbot.api.SimbotExperimentalApi


/**
 * 监听事件流程中某作用域下的上下文。
 *
 * 监听事件流程中的 [作用域上下文][ScopeContext] 实现标准的 [上下文规范][Context], 并定义 [mainValue] 为当前上下文的 [作用域][scope]。
 *
 * 但是请优先使用 [scope], 因为 [mainValue] 的指向未来 **有可能** 会发生变更。
 *
 */
@SimbotExperimentalApi
public interface ScopeContext : Context<ListenerContext.Scope> {

    @Deprecated("mainValue的指向未来可能会变更，请使用 'scope'", ReplaceWith("scope"))
    override val mainValue: ListenerContext.Scope
        get() = scope

    /**
     * 当前上下文所属的作用域。
     */
    val scope: ListenerContext.Scope


    /**
     * 从当前上下文中获取对应 [键][key] 所得的值。
     */
    operator fun get(key: String): Any?


    /**
     * 向当前上下文中置入一个值。需要注意，[置入的值][value] 不允许为null。
     */
    operator fun set(key: String, value: Any)


    /**
     * 移除当前上下文中指定 [键][key] 所对应的值。如果能够拿到移除元素且移除成功，得到移除元素。
     */
    fun remove(key: String): Any?


    /**
     * 得到当前作用域下的全部元素。
     */
    fun size(): Int


    /**
     * 得到当前作用域下的所有键。
     */
    val keys: Set<String>
}

/** 习惯用法 */
@SimbotExperimentalApi
public inline val ScopeContext.size: Int get() = this.size()


/**
 *
 * 基于 [MutableMap] 的 [ScopeContext] 默认实现。
 *
 */
@SimbotExperimentalApi
public class MapScopeContext(
    override val scope: ListenerContext.Scope,
    private val delegate: MutableMap<String, Any> = mutableMapOf(),
) : ScopeContext {

    override fun get(key: String): Any? = delegate[key]

    override fun set(key: String, value: Any) {
        delegate[key] = value
    }

    override fun remove(key: String): Any? = delegate.remove(key)

    override fun size(): Int = delegate.size

    override val keys: Set<String>
        get() = delegate.keys
}

