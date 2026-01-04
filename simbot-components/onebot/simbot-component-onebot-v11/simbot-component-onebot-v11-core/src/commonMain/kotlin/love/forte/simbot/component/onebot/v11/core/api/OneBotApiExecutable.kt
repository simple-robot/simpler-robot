/*
 *     Copyright (c) 2024-2026. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library (Alias: simple-robot, simbot, etc.).
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     Lesser GNU General Public License for more details.
 *
 *     You should have received a copy of the Lesser GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package love.forte.simbot.component.onebot.v11.core.api

import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.KSerializer
import love.forte.simbot.suspendrunner.ST
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.JvmInline


/**
 * 可以用于执行 [BasicOneBotApi] 的执行器接口描述。
 *
 * _应仅由内部实现，第三方实现不保证稳定。_
 *
 * @since 1.1.0
 *
 * @author ForteScarlet
 */
public interface OneBotApiExecutable {
    /**
     * 使用当前 [OneBotApiExecutable] 执行 [api] 并得到原始的 [HttpResponse] 结果。
     *
     * 更多描述参考 [BasicOneBotApi.request]
     *
     * @since 1.1.0
     *
     * @see BasicOneBotApi.request
     */
    @ST
    public suspend fun execute(api: BasicOneBotApi<*>): HttpResponse

    /**
     * 使用当前 [OneBotApiExecutable] 执行 [api] 并得到原始的 [String] 结果。
     *
     * 更多描述参考 [BasicOneBotApi.requestRaw]
     *
     * @since 1.1.0
     *
     * @throws OneBotApiResponseNotSuccessException 如果响应状态码不是 2xx (参考 [HttpStatusCode.isSuccess])
     * @see BasicOneBotApi.requestRaw
     */
    @ST
    public suspend fun executeRaw(api: BasicOneBotApi<*>): String

    /**
     * 使用当前 [OneBotApiExecutable] 执行 [api] 并得到 [OneBotApiResult] 结果。
     *
     * 更多描述参考 [BasicOneBotApi.requestResult]
     *
     * @since 1.1.0
     *
     * @throws OneBotApiResponseNotSuccessException 如果响应状态码不是 2xx (参考 [HttpStatusCode.isSuccess])
     * @see BasicOneBotApi.requestResult
     */
    @ST
    public suspend fun <T : Any> executeResult(api: BasicOneBotApi<T>): OneBotApiResult<T>

    /**
     * 使用当前 [OneBotApiExecutable] 执行 [api] 并得到 [T] 结果。
     *
     * 更多描述参考 [BasicOneBotApi.requestData]
     *
     * @since 1.1.0
     *
     * @throws OneBotApiResponseNotSuccessException 如果响应状态码不是 2xx (参考 [HttpStatusCode.isSuccess])
     * @throws IllegalStateException 如果响应结果体的状态 [OneBotApiResult.retcode]
     * 不是成功或 [OneBotApiResult.data] 为 `null`
     * @see BasicOneBotApi.requestData
     */
    @ST
    public suspend fun <T : Any> executeData(api: BasicOneBotApi<T>): T
}

/**
 * 在 [OneBotApiExecutable] 的基础上提供更多作用域API，
 * 允许在 Kotlin 中使用DSL的风格请求API。
 *
 * @since 1.1.0
 *
 * @see OneBotApiExecutable
 * @see withExecutableScope
 * @see inExecutableScope
 */
@JvmInline
public value class OneBotApiExecutableScope(private val executable: OneBotApiExecutable) {
    /**
     * 使用当前 [OneBotApiExecutable] 执行 [BasicOneBotApi] 并得到原始的 [HttpResponse] 结果。
     *
     * 更多描述参考 [BasicOneBotApi.request]
     *
     * @since 1.1.0
     *
     * @throws OneBotApiResponseNotSuccessException 如果响应状态码不是 2xx (参考 [HttpStatusCode.isSuccess])
     * @see BasicOneBotApi.request
     */
    public suspend fun BasicOneBotApi<*>.execute(): HttpResponse =
        executable.execute(this)

    /**
     * 使用当前 [OneBotApiExecutable] 执行 [BasicOneBotApi] 并得到原始的 [String] 结果。
     *
     * 更多描述参考 [BasicOneBotApi.requestRaw]
     *
     * @since 1.1.0
     *
     * @throws OneBotApiResponseNotSuccessException 如果响应状态码不是 2xx (参考 [HttpStatusCode.isSuccess])
     * @see BasicOneBotApi.requestRaw
     */
    public suspend fun BasicOneBotApi<*>.executeRaw(): String =
        executable.executeRaw(this)

    /**
     * 使用当前 [OneBotApiExecutable] 执行 [BasicOneBotApi] 并得到 [OneBotApiResult] 结果。
     *
     * 更多描述参考 [BasicOneBotApi.requestResult]
     *
     * @since 1.1.0
     *
     * @throws OneBotApiResponseNotSuccessException 如果响应状态码不是 2xx (参考 [HttpStatusCode.isSuccess])
     * @see BasicOneBotApi.requestResult
     */
    public suspend fun <T : Any> BasicOneBotApi<T>.executeResult(): OneBotApiResult<T> =
        executable.executeResult(this)

    /**
     * 使用当前 [OneBotApiExecutable] 执行 [BasicOneBotApi] 并得到 [T] 结果。
     *
     * 更多描述参考 [BasicOneBotApi.requestData]
     *
     * @since 1.1.0
     *
     * @throws OneBotApiResponseNotSuccessException 如果响应状态码不是 2xx (参考 [HttpStatusCode.isSuccess])
     * @throws IllegalStateException 如果响应结果体的状态 [OneBotApiResult.retcode]
     * 不是成功或 [OneBotApiResult.data] 为 `null`
     * @see BasicOneBotApi.requestData
     */
    public suspend fun <T : Any> BasicOneBotApi<T>.executeData(): T =
        executable.executeData(this)
}

/**
 * 在 [OneBotApiExecutableScope] 的作用域下执行 [action]。
 *
 * ```kotlin
 * withExecutableScope(executable) {
 *     api.execute()
 * }
 * ```
 *
 * @since 1.1.0
 */
@OptIn(ExperimentalContracts::class)
public inline fun <T> withExecutableScope(
    executable: OneBotApiExecutable,
    action: OneBotApiExecutableScope.() -> T
): T {
    contract {
        callsInPlace(action, InvocationKind.EXACTLY_ONCE)
    }
    return OneBotApiExecutableScope(executable).action()
}

/**
 * 在 [OneBotApiExecutableScope] 的作用域下执行 [action]。
 *
 * ```kotlin
 * executable.inExecutableScope {
 *     api.execute()
 * }
 * ```
 *
 * @since 1.1.0
 */
@OptIn(ExperimentalContracts::class)
public inline fun <T> OneBotApiExecutable.inExecutableScope(
    action: OneBotApiExecutableScope.() -> T
): T {
    contract {
        callsInPlace(action, InvocationKind.EXACTLY_ONCE)
    }
    return withExecutableScope(this, action)
}

/**
 * 基于 [CustomOneBotApi] 直接构建一个 Api 并发起请求。
 *
 * 没有到达需要反序列化的步骤，直接默认使用 [OneBotApiResult.emptySerializer] 作为反序列化器。
 *
 * @since 1.9.0
 */
@ExperimentalCustomOneBotApi
public suspend inline fun OneBotApiExecutable.execute(
    action: String,
    method: HttpMethod = HttpMethod.Post,
    body: Any? = null,
    block: CustomOneBotApiBuilder<*>.() -> Unit = {}
): HttpResponse {
    return execute(
        CustomOneBotApi(action, method) {
            body(body)
            deserializer(OneBotApiResult.emptySerializer())
            block()
        }
    )
}

/**
 * 基于 [CustomOneBotApi] 直接构建一个 Api 并发起请求，得到对应的 [OneBotApiResult]。
 *
 * 需要配置 [CustomOneBotApiBuilder.deserializer] 指定反序列化器。
 *
 * @since 1.9.0
 */
@ExperimentalCustomOneBotApi
public suspend inline fun <T : Any> OneBotApiExecutable.executeResult(
    action: String,
    method: HttpMethod = HttpMethod.Post,
    body: Any? = null,
    block: CustomOneBotApiBuilder<T>.() -> Unit
): OneBotApiResult<T> {
    return executeResult(
        CustomOneBotApi(action, method) {
            body(body)
            block()
        }
    )
}

/**
 * 基于 [CustomOneBotApi] 直接构建一个 Api 并发起请求，得到对应的 [OneBotApiResult]。
 *
 * 需要配置 [CustomOneBotApiBuilder.deserializer] 指定反序列化器。
 *
 * @since 1.9.0
 */
@ExperimentalCustomOneBotApi
public suspend inline fun <T : Any> OneBotApiExecutable.executeData(
    action: String,
    method: HttpMethod = HttpMethod.Post,
    body: Any? = null,
    block: CustomOneBotApiBuilder<T>.() -> Unit
): T {
    return executeData(
        CustomOneBotApi(action, method) {
            body(body)
            block()
        }
    )
}

/**
 * 基于 [CustomOneBotApi] 直接构建一个 Api 并发起请求，得到对应的 [OneBotApiResult]。
 *
 * @since 1.9.0
 */
@ExperimentalCustomOneBotApi
public suspend inline fun <T : Any> OneBotApiExecutable.executeData(
    action: String,
    dataSerializer: KSerializer<T>,
    method: HttpMethod = HttpMethod.Post,
    body: Any? = null,
    block: CustomOneBotApiBuilder<T>.() -> Unit = {}
): T {
    return executeData(
        CustomOneBotApi(action, method) {
            body(body)
            dataDeserializer(dataSerializer)
            block()
        }
    )
}
