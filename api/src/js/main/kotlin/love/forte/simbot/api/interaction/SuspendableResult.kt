package love.forte.simbot.api.interaction

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * 根据结果函数得到 [SuspendableResult] 实例。
 */
@Suppress("FunctionName")
public actual fun <T> CoroutineScope.SuspendableResult(deferred: Deferred<T>): SuspendableResult<T> {
    TODO("Not yet implemented")
}



private class SuspendableResultImpl<T>(private val deferred: Deferred<T>) : SuspendableResult<T> {
    override val exception: Throwable?
        get() = TODO() // getException()

    override val value: T
        get() = TODO() // runBlocking { getValue() }

    override suspend fun getValue(): T = deferred.await()

    override suspend fun getException(): Throwable? = suspendCancellableCoroutine { continuation ->
        deferred.invokeOnCompletion(continuation::resume)
    }

    override fun getValueOrNull(): T? = TODO() // runBlocking { valueOrNull() }

    override suspend fun valueOrNull(): T? {
        return getException()?.let { null } ?: getValue()
    }
}
