/*
 *
 *  * Copyright (c) 2021. ForteScarlet All rights reserved.
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
     * 有些情况下（例如当前环境不支持的时候）可能无法获取某作用域。
     *
     */
    fun getContext(scope: ListenerContext.Scope): ScopeContext?

}

