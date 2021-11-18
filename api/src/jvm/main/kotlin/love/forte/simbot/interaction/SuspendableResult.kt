/*
 *  Copyright (c) 2021-2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simbot.interaction

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * 根据 [deferred] 得到 [SuspendableResult] 实例。
 */
@Suppress("FunctionName")
public actual fun <T> CoroutineScope.SuspendableResult(deferred: Deferred<T>): SuspendableResult<T> {
    return SuspendableResultImpl(deferred)
}


private class SuspendableResultImpl<T>(private val deferred: Deferred<T>) : SuspendableResult<T> {
    override val exception: Throwable?
        get() = runBlocking { getException() }

    override val value: T
        get() = runBlocking { getValue() }

    override suspend fun getValue(): T = deferred.await()

    override suspend fun getException(): Throwable? = suspendCancellableCoroutine { continuation ->
        deferred.invokeOnCompletion(continuation::resume)
    }

    override fun getValueOrNull(): T? = runBlocking { valueOrNull() }

    override suspend fun valueOrNull(): T? {
        return getException()?.let { null } ?: getValue()
    }
}
