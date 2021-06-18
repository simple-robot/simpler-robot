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

@file:JvmName("ListenerContexts")
package love.forte.simbot.listener

import love.forte.simbot.SimbotRuntimeException
import love.forte.simbot.api.SimbotExperimentalApi
import love.forte.simbot.api.message.events.MsgGet
import love.forte.simbot.mark.Since


/**
 *
 * 监听函数上下文。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
@Since("2.1.0", desc = ["上下文整体结构重构"])
@SimbotExperimentalApi
@Suppress("MemberVisibilityCanBePrivate")
public interface ListenerContext : ContextMap {

    /**
     * 获取指定作用域的上下文
     */
    override fun getContext(scope: Scope): ScopeContext

    /**
     * 从 `global` 中获取信息。
     */
    fun global(key: String): Any? = getContext(Scope.GLOBAL)[key]

    /**
     * 从 `instant` 中获取信息。
     */
    fun instant(key: String): Any? = getContext(Scope.EVENT_INSTANT)[key]

    /**
     * 向 `global` 中设置信息。
     */
    fun global(key: String, value: Any) {
        getContext(Scope.GLOBAL)[key] = value
    }

    /**
     * 向 `instant` 中设置信息。
     */
    fun instant(key: String, value: Any) {
        getContext(Scope.EVENT_INSTANT)[key] = value
    }


    /**
     * api中提供了定义的作用域。
     */
    public enum class Scope(val key: String) {
        /** 瞬时的，即每次监听事件触发 */
        EVENT_INSTANT("instant"),

        /** 全局的 */
        GLOBAL("global")
        ;
    }
}

@SimbotExperimentalApi
public fun ListenerContext.instantOrGlobal(key: String): Any? = instant(key) ?: global(key)

@SimbotExperimentalApi
public fun ListenerContext.globalOrInstant(key: String): Any? = global(key) ?: instant(key)






@SimbotExperimentalApi
public operator fun ListenerContext.get(scope: ListenerContext.Scope): ScopeContext = this.getContext(scope)


@SimbotExperimentalApi
public inline fun findScope(block: () -> String): ListenerContext.Scope? {
    return ListenerContext.Scope.values().find { it.key == block() }
}


/**
 * 监听函数上下文构建工厂。
 */
@SimbotExperimentalApi
public interface ListenerContextFactory {
    /**
     * 通过 [当前监听事件实例][msgGet] 构建一个本次监听所需的上下文实例。
     */
    fun getListenerContext(msgGet: MsgGet): ListenerContext
}



public open class ContextValueNotFoundException : SimbotRuntimeException {
    constructor() : super()
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
    constructor(message: String?, cause: Throwable?, enableSuppression: Boolean, writableStackTrace: Boolean) : super(
        message,
        cause,
        enableSuppression,
        writableStackTrace)
}