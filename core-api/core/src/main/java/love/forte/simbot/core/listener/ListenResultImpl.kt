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
@file:JvmName("ListenResultImpls")

package love.forte.simbot.core.listener

import love.forte.simbot.annotation.ListenBreak
import love.forte.simbot.listener.ListenResult
import love.forte.simbot.listener.ListenerFunction
import love.forte.simbot.listener.ListenerResultFactory

@Deprecated("Use ListenResult.Default", ReplaceWith("ListenResult"), DeprecationLevel.ERROR)
object NothingResult : ListenResult<Nothing> {
    override fun isSuccess(): Boolean = false
    override fun isBreak(): Boolean = false
    override val result: Nothing? = null
    override val cause: Throwable? = null
}


internal class ListenResultBuilder {
    var result: Any? = null
    var success: Boolean = true
    var isBreak: Boolean = false
    var throwable: Throwable? = null
    fun build(): ListenResult<*> = ListenResultImpl(result, success, isBreak, throwable)
}

internal fun listenResult(build: ListenResultBuilder.() -> Unit): ListenResult<*> {
    return ListenResultBuilder().also(build).build()
}


/**
 * [ListenResult] 默认实现。
 */
open class ListenResultImpl<T>(
    override val result: T?,
    private val success: Boolean,
    private val isBreak: Boolean,
    override val cause: Throwable?,
) : ListenResult<T> {
    override fun isSuccess(): Boolean = success
    override fun isBreak(): Boolean = isBreak

    override fun toString(): String {
        return "ListenResult(result=$result, success=$success, isBreak=$isBreak, cause=$cause)"
    }


    companion object {

        /**
         * 构建一个成功的result。
         */
        @JvmStatic
        @JvmOverloads
        @Suppress("UNCHECKED_CAST")
        fun <T> success(result: T? = null, isBreak: Boolean = false): ListenResult<T> {
            return result?.let { ListenResultImpl(it, true, isBreak, null) }
                ?: run {
                    if (isBreak) {
                        EmptySuccessBreakResult as ListenResult<T>
                    } else {
                        EmptySuccessNoBreakResult as ListenResult<T>
                    }
                }
        }

        /**
         * 构建一个失败的result。
         */
        @JvmStatic
        @JvmOverloads
        @Suppress("UNCHECKED_CAST")
        fun <T> failed(result: T? = null, cause: Throwable? = null, isBreak: Boolean = false): ListenResult<T> {
            return result?.let { ListenResultImpl(it, false, isBreak, cause) }
                ?: run {
                    val canObj: Boolean = isBreak && cause == null
                    if (canObj) {
                        if (isBreak) {
                            EmptyFailedBreakResult as ListenResult<T>
                        } else {
                            EmptyFailedNoBreakResult as ListenResult<T>
                        }
                    } else {
                        ListenResultImpl(null, false, isBreak, cause)
                    }
                }
        }
    }
}


/**
 * 空的成功结果。
 */
internal object EmptySuccessNoBreakResult : ListenResult<Nothing>,
    ListenResultImpl<Nothing>(null, true, false, null)

/**
 * 空的失败结果。
 */
internal object EmptyFailedNoBreakResult : ListenResult<Nothing>,
    ListenResultImpl<Nothing>(null, false, false, null)

/**
 * 空的成功结果。break为true
 */
internal object EmptySuccessBreakResult : ListenResult<Nothing>,
    ListenResultImpl<Nothing>(null, true, true, null)

/**
 * 空的失败结果。break为true
 */
internal object EmptyFailedBreakResult : ListenResult<Nothing>,
    ListenResultImpl<Nothing>(null, false, true, null)


/**
 * 监听响应值工厂。
 * 单例。
 */
public object CoreListenerResultFactory : ListenerResultFactory {
    override fun getResult(
        result: Any?,
        listenerFunction: ListenerFunction,
        throwable: Throwable?,
    ): ListenResult<*> {
        return if (result is ListenResult<*>) {
            result
        } else {
            listenResult {
                this.result = result
                if (throwable != null) {
                    this.success = false
                } else {
                    this.success = when (result) {
                        is Boolean -> result
                        else -> result != null
                    }
                }

                this.throwable = throwable
                // listen break.

                this.isBreak = success && listenerFunction.getAnnotation(ListenBreak::class.java) != null
            }
        }
    }
}








