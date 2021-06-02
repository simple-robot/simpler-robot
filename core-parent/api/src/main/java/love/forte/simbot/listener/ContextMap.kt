/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot
 *  * File     MiraiAvatar.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *
 */

package love.forte.simbot.listener

/**
 * 作为一个上下文映射表。
 * 其中包含了多个不同作用域的上下文。
 */
public interface ContextMap {

    /**
     * 获取当前上下文映射中的所有作用域内容。
     * @see ListenerContext.Scope
     */
    val scopes: Array<ListenerContext.Scope>

    /**
     * 根据某个作用域获取到对应的上下文实例。
     */
    fun getContext(scope: ListenerContext.Scope): ScopeContext

}


/**
 * [ContextMap] 工厂。获取一个contextMap。
 */
public interface ContextMapFactory {

    /**
     * 为当前监听事件获取一个[上下文映射表][ContextMap]实例。
     *
     * 每当监听函数可以成功进行执行的时候，都会调用此函数以获取一个对应的监听函数。
     *
     */
    val contextMap: ContextMap



}


/**
 *
 */
@Suppress("UNCHECKED_CAST", "MemberVisibilityCanBePrivate")
public class ContextMapImpl
constructor(
    // override val instant: MutableMap<String, Any>,
    // override val global: MutableMap<String, Any>,
) : ContextMap {
    override val scopes: Array<ListenerContext.Scope>
        get() = TODO("Not yet implemented")

    override fun getContext(scope: ListenerContext.Scope): ScopeContext {
        TODO("Not yet implemented")
    }

    // /**
    //  * 优先从当前instant中获取，否则从全局global中获取。
    //  */
    // override fun instantOrGlobal(key: String): Any? = instant[key] ?: global[key]
    //
    // /**
    //  * 优先从全局global中获取，否则从当前instant中获取。
    //  */
    // override fun globalOrInstant(key: String): Any? = global[key] ?: instant[key]
    //
    // /**
    //  * 优先从当前instant中获取，否则从全局global中获取，如果还没有则使用默认值。
    //  * @param def 都没有的时候使用的默认值。不可为null。
    //  */
    // override fun <V> instantOrGlobalOrDefault(key: String, def: V): V = (instant[key] ?: global[key] ?: def) as V
    //
    // /**
    //  * 优先从全局global中获取，否则从当前instant中获取。
    //  * @param def 都没有的时候使用的默认值。不可为null。
    //  */
    // override fun <V> globalOrInstantOrDefault(key: String, def: V): V = (global[key] ?: instant[key] ?: def) as V
//
//     /**
//      * 优先从当前instant中获取，否则从全局global中获取，如果还没有则使用默认值。
//      * @param compute 都没有的时候使用key计算得到一个value。得到的值不可为null。
//      */
//     override fun <V> instantOrGlobalOrCompute(key: String, compute: (String) -> V): V =
//         (instant[key] ?: global[key] ?: compute(key)) as V
//
//     /**
//      * 优先从全局global中获取，否则从当前instant中获取。
//      * @param compute 都没有的时候使用key计算得到一个value。得到的值不可为null。
//      */
//     override fun <V> globalOrInstantOrCompute(key: String, compute: (String) -> V): V =
//         (global[key] ?: instant[key] ?: compute(key)) as V
}