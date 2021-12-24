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

@file:JvmName("ContinuousSessionScopeContextUtil")

package love.forte.simbot.event

import kotlinx.coroutines.*
import love.forte.simbot.Api4J
import java.util.concurrent.Future
import java.util.concurrent.TimeoutException
import kotlin.coroutines.Continuation


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
public interface ContinuousSessionContainer : Map<String, ContinuousSession<*>> {
    /**
     * 分组名称
     */
    public val group: String

    /**
     * 根据key获取一个 [ContinuousSession].
     */
    public override operator fun get(key: String): ContinuousSession<*>?

    /**
     * 判断当前容器中是否存在对应的会话
     */
    public operator fun contains(id: String): Boolean

    /**
     * 获取所有的key
     */
    public override val keys: Set<String>

    /**
     * 得到其中的元素数量
     */
    public override val size: Int
}


public fun ContinuousSessionContainer.cancel(cause: Throwable? = null) {
    this.keys.forEach { key -> this[key]?.cancel(cause) }
}


/**
 *
 * 持续会话的作用域.
 *
 *
 * @author ForteScarlet
 * @since 2.3.0
 */
public abstract class ContinuousSessionScopeContext {


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
    public abstract fun get(@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") group: String): ContinuousSessionContainer?


    /**
     * 获取一个[group]和[key]下对应的[ContinuousSession].
     *
     * [get] 更多的用于判断 [group]中[key] 的存在与否，如果需要推送内容，使用 [push] 或者通过 [take] 获取后使用。
     *
     */
    public abstract operator fun get(group: String, key: String): ContinuousSession<*>?

    /**
     * 推送得到的值.
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


