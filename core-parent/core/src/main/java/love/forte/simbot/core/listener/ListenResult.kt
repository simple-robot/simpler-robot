/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     ListenResult.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.listener


/**
 *
 * 监听函数的执行回执。
 *
 *
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
interface ListenResult<T> {

    /**
     * 是否执行成功。
     */
    fun isSuccess(): Boolean

    /**
     * 是否阻断接下来的监听函数的执行。
     */
    fun isBreak(): Boolean

    /**
     * 最终的执行结果。
     */
    val result: T?

    /**
     * 如果执行出现了异常，此处为异常。
     */
    val throwable: Throwable?

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


open class ListenResultImpl<T>(
    override val result: T?,
    private val success: Boolean,
    private val isBreak: Boolean,
    override val throwable: Throwable?
) : ListenResult<T> {
    override fun isSuccess(): Boolean = success
    override fun isBreak(): Boolean = isBreak

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