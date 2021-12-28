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

@file:JvmName("ContinuousSessionScopeContextUtil")

package love.forte.simbot.event

import kotlinx.coroutines.*
import love.forte.simbot.Api4J
import java.util.concurrent.Future
import java.util.concurrent.TimeoutException
import java.util.function.Consumer
import kotlin.coroutines.Continuation
import kotlin.time.Duration


/**
 * 持续会话的延续，其类似于 [Continuation], 用于向待延续结果中推送结果。
 */
public interface ContinuousSession<T> {

    /**
     * 推送一个结果。
     *
     * @throws IllegalStateException 如果结果已经被推送过
     * @return 当且仅当推送成功的时候得到true
     */
    public fun push(value: T)


    /**
     * 推送一个错误。
     *
     * @throws IllegalStateException 如果结果已经被推送过
     * @return 当且仅当推送成功的时候得到true
     */
    public fun pushException(e: Throwable)


    /**
     * 直接关闭.
     */
    public fun cancel(e: Throwable? = null)
}


/**
 *
 * [ContinuousSession] 的容器, 面向外部的接口。
 *
 */
public interface ContinuousSessionContainer {
    /**
     * 分组名称
     */
    public val group: String

    /**
     * 根据key获取一个 [ContinuousSession].
     */
    public operator fun <T> get(key: String): ContinuousSession<T>?

    /**
     * 判断当前容器中是否存在对应的会话
     */
    public operator fun contains(id: String): Boolean

    /**
     * 获取所有的key
     */
    public val keys: Set<String>

    /**
     * 得到其中的元素数量
     */
    public val size: Int
}


public fun ContinuousSessionContainer.cancel(cause: Throwable? = null) {
    this.keys.forEach { key -> get<Any?>(key)?.cancel(cause) }
}


/**
 *
 * 持续会话的作用域.
 *
 *
 * @author ForteScarlet
 * @since 2.3.0
 */
public abstract class ContinuousSessionContext3 {


    public abstract val coroutineScope: CoroutineScope

    /**
     * 默认的超时时间。
     */
    protected abstract val defaultTimeoutMills: Long

    /**
     * 获取一个[group]下对应的[ContinuousSessionContainer].
     *
     * [get] 更多的用于判断 [group] 的存在与否，如果需要推送内容，使用 [push] 或者通过 [take] 获取后使用。
     *
     */
    public abstract operator fun get(@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") group: String): ContinuousSessionContainer?


    /**
     * 获取一个[group]和[key]下对应的[ContinuousSession].
     *
     * [get] 更多的用于判断 [group]中[key] 的存在与否，如果需要推送内容，使用 [push] 或者通过 [take] 获取后使用。
     *
     */
    public abstract operator fun <T> get(group: String, key: String): ContinuousSession<T>?

    /**
     * 推送得到的值给指定的会话并使其恢复。
     *
     * 当推送并恢复指定会话的时候，当前的 [push] 节点会被暂停挂起，直到恢复会话处结束整个逻辑或者进入到下一个挂起等待点。
     *
     * @see resume
     * @see resumeAsync
     * @see tryPush
     *
     * @throws ClassCastException 当类型与实际所需不匹配的时候.
     * @throws IllegalStateException 如果被推送目标已经被推送过了（这一般发生在通过 [get] 得到 [ContinuousSession] 但是没有去移除它而导致的）
     * @return 只有当推送成功的时候得到true
     */
    public open fun <T> push(group: String, key: String, value: T): Boolean {
        @Suppress("UNCHECKED_CAST")
        return (take(group, key) as? ContinuousSession<T>)?.push(value)?.let { true } ?: false
    }

    /**
     * 推送得到的值给指定的会话并使其恢复。[tryPush] 只会进行尝试推送, 不提供任何方法返回值。
     * 假若 [tryPush] 推送成功，不同于 [push], 当前节点将不会被挂起。
     *
     * [tryPush] 仅为 Java 提供，Kotlin可以有更多更优方法，例如 [resumeAsync].
     *
     * @param failedHandler 当出现异常时或者推送失败（返回值为false）时使用的handler。如果推送失败但是没有异常，则 [failedHandler] 的参数为null。
     *
     * @see push
     * @see resumeAsync
     */
    @Api4J
    public open fun <T> tryPush(group: String, key: String, value: T, failedHandler: Consumer<Throwable?> = Consumer {}) {
        coroutineScope.launch { kotlin.runCatching { push(group, key, value) }.onFailure { failedHandler.accept(it) }.onSuccess { if (!it) { failedHandler.accept(null) } } }
    }

    /**
     * 推送一个错误
     */
    public open fun pushException(group: String, key: String, value: Throwable) {
        take(group, key)?.pushException(value)
    }

    /**
     * 等待下一次的事件唤醒。
     *
     * @throws CancellationException 如果被手动关闭
     * @throws TimeoutException 如果设置了超时时间且超时
     * @throws Exception 其他可能由于 [ContinuousSession.pushException] 而推送的异常
     */
    @JvmSynthetic
    public abstract suspend fun <T> waiting(
        group: String,
        key: String,
        timeout: Long = defaultTimeoutMills,
        invokeOnCancellation: CompletionHandler? = null,
    ): T

    /**
     * 等待下一次的事件唤醒。提供一个 [回调函数][SessionCallback] 来实现事件触发。
     *
     * 如果 timeout
     *
     */
    @Api4J
    @JvmOverloads
    @Suppress("FunctionName")
    @JvmName("waiting")
    public fun <T> _waiting4J(group: String, key: String, timeout: Long = defaultTimeoutMills, callback: SessionCallback<T>) {
        coroutineScope.launch {
            try {
                callback.onResume(waiting(group, key, timeout, callback::onCancel))
            } catch (e: Throwable) {
                if (e is TimeoutCancellationException) {
                    callback.onError(TimeoutException().also { it.initCause(e) })
                } else callback.onError(e)
            }
        }
    }

    /**
     * 等待下一次的事件唤醒。提供一个 [回调函数][SessionCallback] 来实现事件触发。
     *
     */
    @Api4J
    @JvmOverloads
    @Suppress("FunctionName")
    @JvmName("waiting")
    public fun <T> _waiting4J(
        group: String,
        key: String,
        timeout: Long = defaultTimeoutMills,
        callbackBuilder: SessionCallbackBuilder<T>
    ) {
        _waiting4J(group, key, timeout, callbackBuilder.build())
    }

    /**
     * 等待下一次的事件唤醒。提供一个 [回调函数][SessionCallback] 来实现事件触发。
     *
     */
    @Api4J
    @JvmOverloads
    @Suppress("FunctionName")
    @JvmName("waiting")
    public fun <T> _waiting4J(
        group: String,
        key: String,
        timeout: Long = defaultTimeoutMills,
        onResume: SessionCallbackBuilder.OnResume<T>,
    ) {
        coroutineScope.launch {
            onResume(waiting(group, key, timeout))
        }
    }

    /**
     * 阻塞的会话等待。
     *
     * **需要注意，阻塞会话等待有概率会导致一些问题，例如资源不足或死锁，请斟酌使用。**
     *
     * @see [waiting][_waiting4J]
     * @see waitFuture
     * @throws TimeoutException 当超时的时候
     * @throws CancellationException 当被普通的主动关闭时
     */
    @Api4J
    @Throws(TimeoutException::class)
    public abstract fun <T> waitBlocking(group: String, key: String, timeout: Long): T

    /**
     * 阻塞的会话等待。
     *
     * **需要注意，阻塞会话等待有概率会导致一些问题，例如资源不足或死锁，请斟酌使用。**
     *
     * @see [waiting][_waiting4J]
     * @see waitFuture
     * @throws TimeoutException 当超时的时候
     * @throws CancellationException 当被普通的主动关闭时
     */
    @Api4J
    @Throws(TimeoutException::class)
    public open fun <T> waitBlocking(group: String, key: String): T = waitBlocking(group, key, defaultTimeoutMills)

    /**
     * 将执行结果作为异步的 [Future] 返回。
     *
     * @see waitBlocking
     * @see [waiting][_waiting4J]
     */
    @Api4J
    public abstract fun <T> waitFuture(group: String, key: String, timeout: Long): Future<T>

    /**
     * 将执行结果作为异步的 [Future] 返回。
     *
     * @see waitBlocking
     * @see [waiting][_waiting4J]
     */
    @Api4J
    public open fun <T> waitFuture(group: String, key: String): Future<T> = waitFuture(group, key, defaultTimeoutMills)

    /**
     * 移除某个指定分组下的 [ContinuousSessionContainer], 并关闭.
     * 注意，如果有对应的值，在返回之前就会执行 [ContinuousSessionContainer.cancel]。
     *
     * 如果你只希望获取, 不希望关闭，请使用 [get].
     * 如果你希望获取并移除，不希望关闭，请使用 [take].
     */
    public open fun remove(group: String): ContinuousSessionContainer? =
        take(group)?.also { it.cancel() }

    /**
     * 移除某个指定的 [ContinuousSession], 并关闭.
     * 注意，如果有对应的值，在返回之前就会执行 [ContinuousSession.cancel]。
     *
     * 如果你只希望获取, 不希望关闭，请使用 [get].
     * 如果你希望获取并移除，不希望关闭，请使用 [take].
     */
    public open fun remove(group: String, key: String): ContinuousSession<*>? = take(group, key)?.also { it.cancel() }

    /**
     * 移除某个指定的 [ContinuousSession].
     * 被移除的对象（如果有的话）不会进行推送操作，因此直到超时（如果有的话）之前，你或许需要主动进行推送。
     *
     */
    public abstract fun take(group: String, key: String): ContinuousSession<*>?

    /**
     * 直接获取并移除某指定的 [ContinuousSessionContainer].
     * 被移除的对象（如果有的话）不会进行任何操作
     */
    public abstract fun take(group: String): ContinuousSessionContainer?


    /**
     * 获取所有元素的数量，此处的数量指的是 [ContinuousSessionContainer] 的数量。
     */
    public abstract fun size(): Int


    /**
     * 获取所有的 `groups`
     *
     */
    public abstract val keys: Set<String>
}


public suspend fun <T> ContinuousSessionContext3.waiting(group: String, key: String, timeout: Duration, invokeOnCancellation: CompletionHandler? = null): T {
    return waiting(group, key, timeout.inWholeMilliseconds, invokeOnCancellation)
}



/**
 * 推送一个结果值并恢复正在等待此结果的事件。
 *
 * @see ContinuousSessionContext3.push
 *
 */
public fun <T> ContinuousSessionContext3.resume(group: String, key: String, value: T): Boolean = push(group, key, value)

/**
 * 推送一个结果值并恢复正在等待此结果的事件, 但是是通过异步进行推送，会立即返回 [Deferred] 并执行后续逻辑。
 *
 * @see ContinuousSessionContext3.push
 *
 */
public fun <T> ContinuousSessionContext3.resumeAsync(group: String, key: String, value: T): Deferred<Boolean> = coroutineScope.async { push(group, key, value) }