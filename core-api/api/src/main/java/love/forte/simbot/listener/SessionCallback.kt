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


@Suppress("MemberVisibilityCanBePrivate")
public class SessionCallbackBuilder<T> {

    private companion object {
        private val defOnResume: (Any?) -> Unit = {}
        private val defOnError: (Throwable) -> Unit = { throwable -> throw throwable }
        private val defOnCancel: (Throwable?) -> Unit get() = defOnResume

        private val ignoreOnResume: (Any?) -> Unit get() = defOnResume
        private val ignoreOnError: (Throwable) -> Unit = defOnResume
        private val ignoreOnCancel: (Throwable?) -> Unit = defOnCancel
    }

    private var onResume: (T) -> Unit = defOnResume
    private var onError: (Throwable) -> Unit = defOnError
    private var onCancel: (Throwable?) -> Unit = defOnCancel

    /**
     * 处理得到的结果
     */
    fun onResume(block: OnResume<T>): SessionCallbackBuilder<T> = also { this.onResume = block::invoke }
    fun ignoreOnResume(): SessionCallbackBuilder<T> = onResume(ignoreOnResume)

    /**
     * 处理出现的异常。
     * 一般情况下，有可能会与 [onCancel] 同时触发。
     *
     * 默认情况下，[onError] 会直接抛出异常。
     */
    fun onError(block: OnError): SessionCallbackBuilder<T> = also { this.onError = block::invoke }

    /**
     * 直接忽略 [onError] 的异常处理。
     */
    fun ignoreOnError(): SessionCallbackBuilder<T> = onError(ignoreOnError)

    /**
     * 当被关闭时进行处理。一般可能发生在 [超时][java.util.concurrent.TimeoutException] 或 [手动关闭][kotlinx.coroutines.CancellationException] 的时候。
     * 很多情况下会与 [onError] 会一同被触发。
     *
     * 默认情况下，[onCancel] 中不会进行任何处理。
     *
     * @see onErrorAndCancel
     */
    fun onCancel(block: OnCancel): SessionCallbackBuilder<T> = also { this.onCancel = block::invoke }

    /**
     * 直接忽略 [onCancel] 的情况处理。
     */
    fun ignoreOnCancel(): SessionCallbackBuilder<T> = onCancel(ignoreOnCancel)

    /**
     * 同时处理 [onError] 和 [onCancel]. 即二者使用同一个处理逻辑。
     */
    fun onErrorAndCancel(block: OnCancel): SessionCallbackBuilder<T> = also {
        with(block::invoke) {
            onError = this
            onCancel = this
        }
    }

    /**
     * 忽略 [onCancel] 和 [onError]
     */
    fun ignoreOnErrorAndCancel(): SessionCallbackBuilder<T> = also {
        ignoreOnError()
        ignoreOnCancel()
    }

    fun build(): SessionCallback<T> = SessionCallbackImpl(onResume, onError, onCancel)


    public fun interface OnResume<T>  {
        operator fun invoke(value: T)
    }

    public fun interface OnError {
        /**
         * 处理非正常 resume 的情况。
         *
         * @param exception 当 [java.util.concurrent.TimeoutException] 则代表为超时关闭,
         * 当 [kotlinx.coroutines.CancellationException] 则为被普通执行了关闭,
         * 其他情况则是其他关闭调用方所提供的异常。
         */
        operator fun invoke(exception: Throwable)
    }

    public fun interface OnCancel {
        /**
         *  处理被关闭的情况。
         *
         * @param cause 当 [java.util.concurrent.TimeoutException] 则代表为超时关闭,
         * 为空则代表普通关闭。
         * 其他情况则是其他关闭调用方所提供的异常。
         */
        operator fun invoke(cause: Throwable?)
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


