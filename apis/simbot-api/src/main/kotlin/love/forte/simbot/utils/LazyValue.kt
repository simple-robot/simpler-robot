/*
 *  Copyright (c) 2022-2022 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simbot.utils

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock


/**
 * 一个类似于 [Deferred] 的支持suspend的懒加载器。
 */
public interface LazyValue<T> {

    /**
     * 挂起并等待结果值被加载。如果已经加载则会立即返回。
     * 如果加载过程中出现了异常，则每次调用此函数都会得到那个异常。
     */
    public suspend fun await(): T

    /**
     * 立即得到加载的值。如果尚未加载，则会返回包裹了 [NotInitializedException] 异常的 result。
     *
     * @throws NotInitializedException 当结果值尚未初始化时。
     */
    public val value: Result<T>

    /**
     * 判断是否已经加载完成。
     */
    public val isCompleted: Boolean


    public class NotInitializedException internal constructor() : IllegalStateException()
}


public fun <T> lazyValue(initializer: suspend () -> T): LazyValue<T> {
    return LazyValueImpl(initializer)
}


private class LazyValueImpl<T>(
    initializer: suspend () -> T,
) : LazyValue<T> {
    private var initializer: (suspend () -> T)? = initializer
    private val lock = Mutex(false)

    // NO_INIT or Result<T>
    @Volatile
    private var _value: Any = NO_INIT // Result<T>

    override suspend fun await(): T {
        val v1 = _value
        if (v1 !== NO_INIT) {
            @Suppress("UNCHECKED_CAST")
            (v1 as Result<T>).getOrThrow()
        }

        return lock.withLock {
            val v2 = _value
            if (v2 !== NO_INIT) {
                @Suppress("UNCHECKED_CAST")
                (v2 as Result<T>).getOrThrow()
            } else {
                val typedResult = kotlin.runCatching { initializer!!() }
                _value = typedResult
                initializer = null
                typedResult.getOrThrow()
            }
        }
    }

    override val isCompleted: Boolean
        get() = _value !== NO_INIT

    override val value: Result<T>
        get() {
            val v = _value
            if (v !== NO_INIT) {
                @Suppress("UNCHECKED_CAST")
                return v as Result<T>
            } else {
                return Result.failure(LazyValue.NotInitializedException())
            }
        }

    @Suppress("ClassName")
    private object NO_INIT
}
