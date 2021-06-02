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
     * 根据某个作用域获取到对应的上下文实例。
     */
    fun getContext(scope: ListenerContext.Scope): ScopeContext

}


/**
 * [ContextMap] 工厂。获取一个contextMap。
 */
@Deprecated("Unused")
public interface ContextMapFactory {

    /**
     * 为当前监听事件获取一个[上下文映射表][ContextMap]实例。
     *
     * 每当监听函数可以成功进行执行的时候，都会调用此函数以获取一个对应的监听函数。
     *
     */
    val contextMap: ContextMap



}
