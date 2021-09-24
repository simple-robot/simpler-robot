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

@file:JvmName("ListenerContexts")
@file:Suppress("unused")

package love.forte.simbot.listener

import love.forte.simbot.SimbotRuntimeException
import love.forte.simbot.api.message.events.MsgGet
import love.forte.simbot.mark.Since


/**
 *
 * 监听函数上下文。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
@Since("2.1.0", desc = ["上下文整体结构重构"])
@Suppress("MemberVisibilityCanBePrivate")
public interface ListenerContext : ContextMap {

    /**
     * 获取指定作用域的上下文
     */
    override fun getContext(scope: Scope): ScopeContext?

    /**
     * 从 `global` 中获取信息。
     */
    fun global(key: String): Any? = getContext(Scope.GLOBAL)?.get(key)

    /**
     * 从 `instant` 中获取信息。
     */
    fun instant(key: String): Any? = getContext(Scope.EVENT_INSTANT)?.get(key)

    /**
     * 向 `global` 中设置信息。
     */
    fun global(key: String, value: Any) {
        getContext(Scope.GLOBAL)?.set(key, value) //[key] = value
    }

    /**
     * 向 `instant` 中设置信息。
     */
    fun instant(key: String, value: Any) {
        getContext(Scope.EVENT_INSTANT)?.set(key, value) //[key] = value
    }


    /**
     * api中提供了定义的作用域。
     */
    public enum class Scope(val key: String) {
        /** 瞬时的，即每次监听事件触发 */
        EVENT_INSTANT("instant"),

        /** 全局的 */
        GLOBAL("global"),

        /**
         * 持续会话
         */
        CONTINUOUS_SESSION("continuous-session")
        ;
    }
}

public fun ListenerContext.instantOrGlobal(key: String): Any? = instant(key) ?: global(key)

public fun ListenerContext.globalOrInstant(key: String): Any? = global(key) ?: instant(key)


public operator fun ListenerContext.get(scope: ListenerContext.Scope): ScopeContext? = this.getContext(scope)

/**
 *
 * 通过 [ListenerContext.Scope.CONTINUOUS_SESSION] 作用域得到 [持续会话上下文][ContinuousSessionScopeContext] 实例。
 *
 * @throws ClassCastException 如果具体的实现中, [ListenerContext.Scope.CONTINUOUS_SESSION] 的类型并不是 [ContinuousSessionScopeContext]
 */
public val ListenerContext.continuousSessionContext: ContinuousSessionScopeContext? get() = this[ListenerContext.Scope.CONTINUOUS_SESSION] as? ContinuousSessionScopeContext


public inline fun findScope(block: () -> String): ListenerContext.Scope? {
    return ListenerContext.Scope.values().find { it.key == block() }
}


/**
 * 监听函数上下文构建工厂。
 */
public interface ListenerContextFactory {
    /**
     * 通过 [当前监听事件实例][msgGet] 构建一个本次监听所需的上下文实例。
     */
    fun getListenerContext(msgGet: MsgGet): ListenerContext

    /**
     * 直接获取某个作用域下的上下文。
     * @since 2.3.0
     */
    fun getScopeContext(msgGet: MsgGet, scope: ListenerContext.Scope): ScopeContext
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