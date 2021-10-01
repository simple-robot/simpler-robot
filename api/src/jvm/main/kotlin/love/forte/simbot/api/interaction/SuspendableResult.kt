package love.forte.simbot.api.interaction

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
