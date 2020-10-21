/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     ListenResultImpl.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.listener

import love.forte.simbot.listener.ListenResult
import love.forte.simbot.listener.ListenerFunction
import love.forte.simbot.listener.ListenerResultFactory

/**
 * 无内容的result。一般用在没有监听函数执行被执行的时候。
 */
object NothingResult : ListenResult<Nothing> {
    override fun isSuccess(): Boolean = false
    override fun isBreak(): Boolean = false
    override val result: Nothing? = null
    override val throwable: Throwable? = null
}


internal class ListenResultBuilder {
    var result: Any? = null
    var success: Boolean = true
    var isBreak: Boolean = false
    var throwable: Throwable? = null
    fun build(): ListenResult<*> = ListenResultImpl(this, success, isBreak, throwable)
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
    override val throwable: Throwable?
) : ListenResult<T> {
    override fun isSuccess(): Boolean = success
    override fun isBreak(): Boolean = isBreak


    companion object {

        /**
         * 构建一个成功的result。
         */
        @JvmStatic
        @Suppress("UNCHECKED_CAST")
        fun <T> success(result: T?, isBreak: Boolean): ListenResult<T> {
            return result?.let { ListenResultImpl(it, true, isBreak, null) } ?: kotlin.run {
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
        @Suppress("UNCHECKED_CAST")
        fun <T> failed(result: T?, throwable: Throwable?, isBreak: Boolean): ListenResult<T> {
            return result?.let { ListenResultImpl(it, false, isBreak, throwable) } ?: kotlin.run {
                val canObj: Boolean = isBreak && throwable == null
                if (canObj) {
                    if (isBreak) {
                        EmptyFailedBreakResult as ListenResult<T>
                    } else {
                        EmptyFailedNoBreakResult as ListenResult<T>
                    }
                } else {
                    ListenResultImpl(null, false, isBreak, throwable)
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
        throwable: Throwable?
    ): ListenResult<*> {
        return if (result is ListenResult<*>) {
            result
        } else {
            listenResult {
                this.result = result
                this.success = throwable == null && result != null
                this.throwable = throwable
                // TODO break.
            }
        }
    }
}








