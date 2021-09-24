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
 *
 *
 *
 * @author ForteScarlet
 */
public interface SessionCallback<T> {

    /**
     * 当被唤醒了。
     */
    fun onResume(value: T)


    /**
     * 当发生了异常, 例如发生了超时，或者推送了各种错误等。
     */
    fun onError(exception: Throwable)


    /**
     * 被关闭了. 一般发生在使用了 [ContinuousSessionScopeContext.remove] 或 超时
     */
    fun onCancel(exception: Throwable?)


    companion object {
        @JvmStatic
        fun <T> builder(): SessionCallbackBuilder<T> = SessionCallbackBuilder()

        @Suppress("UNUSED_PARAMETER")
        @JvmStatic
        fun <T> builder(type: Class<T>): SessionCallbackBuilder<T> = SessionCallbackBuilder()
    }
}


public class SessionCallbackBuilder<T> {

    private companion object {
        private val defOnResume: (Any?) -> Unit = {}
        private val defOnError: (Throwable) -> Unit = { throwable -> throw throwable }
        private val defOnCancel: (Throwable?) -> Unit = { throwable -> if (throwable != null) throw throwable }
    }

    private var onResume: (T) -> Unit = defOnResume
    private var onError: (Throwable) -> Unit = defOnError
    private var onCancel: (Throwable?) -> Unit = defOnCancel

    fun onResume(block: OnResume<T>): SessionCallbackBuilder<T> = also { this.onResume = block::invoke }

    fun onError(block: OnError): SessionCallbackBuilder<T> = also { this.onError = block::invoke }

    fun onCancel(block: OnCancel): SessionCallbackBuilder<T> = also { this.onCancel = block::invoke }

    fun build(): SessionCallback<T> = SessionCallbackImpl(onResume, onError, onCancel)


    public fun interface OnResume<T>  {
        operator fun invoke(p1: T)
    }

    public fun interface OnError {
        operator fun invoke(p1: Throwable)
    }

    public fun interface OnCancel {
        operator fun invoke(p1: Throwable?)
    }

}


private class SessionCallbackImpl<T>(
    private val onResume: (T) -> Unit,
    private val onError: (Throwable) -> Unit,
    private val onCancel: (Throwable?) -> Unit,
) : SessionCallback<T> {

    override fun onResume(value: T) {
        onResume.invoke(value)
    }

    override fun onError(exception: Throwable) {
        onError.invoke(exception)
    }

    override fun onCancel(exception: Throwable?) {
        onCancel.invoke(exception)
    }
}


