/*
 *     Copyright (c) 2025-2026. ForteScarlet.
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

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.content.*
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import love.forte.simbot.component.onebot.v11.core.internal.api.CustomOneBotApiBuilderImpl
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic


/**
 * 定制化的 OneBot API。
 *
 * Kotlin:
 * ```Kotlin
 * val api = CustomOneBotApi(action = "action_name") {
 *     body = RequestBody(value1, value2)
 *     // ⚠️注意: deserializer 是必须的！
 *     deserializer(OneBotApiResult.serializer(Int.serializer()))
 * }
 * ```
 *
 * Java:
 * ```Java
 * final var api = CustomOneBotApi.builder("action_name", HttpMethod.Post)
 *     .body(RequestBody(value1, value2))
 *     // ⚠️注意: deserializer 是必须的！
 *     .deserializer(raw -> deserialize(raw))
 *     .build();
 * ```
 *
 * @author ForteScarlet
 * @since 1.9.0
 */
@ExperimentalCustomOneBotApi
public interface CustomOneBotApi<T : Any> : BasicOneBotApi<T> {
    /**
     * 预期结果 [OneBotApiResult] 类型的反序列化器。
     * 不一定存在，因为 [deserialize] 可能是定制化的逻辑而非基于 kotlinx-serialization。
     */
    public val apiResultDeserializer: DeserializationStrategy<OneBotApiResult<T>>?

    public companion object {
        /**
         * 获取一个用于动态构建 OneBot API 的构建器。
         */
        @JvmStatic
        @JvmOverloads
        public fun <T : Any> builder(
            action: String,
            method: HttpMethod = HttpMethod.Post,
        ): CustomOneBotApiBuilder<T> = CustomOneBotApiBuilderImpl(action, method)
    }
}

/**
 * 一个用于动态构建定制化 OneBot API 的构建器，
 * 可以在不直接实现 [BasicOneBotApi] 的情况下以 DSL 或链式 API 的形式构建一个 [BasicOneBotApi]。
 *
 * @since 1.9.0
 *
 * @author ForteScarlet
 */
@ExperimentalCustomOneBotApi
public interface CustomOneBotApiBuilder<T : Any> {
    /**
     * API 请求方式。默认为 [HttpMethod.Post].
     */
    public val method: HttpMethod

    /**
     * OneBot API 的动作/行为名称。
     */
    public val action: String

    /**
     * 请求体，在非 Get 请求时使用。
     *
     * body 的类型可以是：
     * - null
     * - 一个 JSON 字符串。
     * - 一个支持通过 kotlinx-serialization 序列化的任意对象。
     * - 一个可以被 ktor 的 [HttpRequestBuilder.setBody][setBody] 识别的其他类型，例如 [OutgoingContent]。
     *
     * 如果 [method] 是 [HttpMethod.Get]，body 必须为 null，否则会抛出 [IllegalArgumentException]。
     *
     * @throws IllegalArgumentException 如果在 method 为 [HttpMethod.Get] 时 设置非 null 值
     */
    public var body: Any?

    /**
     * 响应结果的反序列化器。
     */
    public var deserializer: DynamicOneBotApiDeserializer<T>?

    /**
     * 提供请求体。
     */
    public fun body(body: Any?): CustomOneBotApiBuilder<T> = apply { this.body = body }

    /**
     * 提供一个对 API 响应的文本内容进行反序列化的函数。
     *
     * 如果打算直接使用 kotlinx-serialization 进行反序列化，
     * 请使用 [deserializer(DeserializationStrategy<OneBotApiResult<T>>)][deserializer]
     * 或 [dataDeserializer]。
     *
     * @see deserializer
     * @see dataDeserializer
     */
    public fun deserializer(deserializer: DynamicOneBotApiDeserializer<T>): CustomOneBotApiBuilder<T> = apply {
        this.deserializer = deserializer
    }

    /**
     * 提供一个对 API 响应的文本内容进行反序列化的函数。
     *
     * @see deserializer
     * @see dataDeserializer
     */
    public fun deserializer(
        deserializationStrategy: DeserializationStrategy<OneBotApiResult<T>>
    ): CustomOneBotApiBuilder<T>


    public fun build(): CustomOneBotApi<T>
}

/**
 * 反序列化 [OneBotApiResult] 结果的函数类型。
 *
 * @author ForteScarlet
 * @since 1.9.0
 */
@ExperimentalCustomOneBotApi
public fun interface DynamicOneBotApiDeserializer<T : Any> {
    @Throws(Throwable::class)
    public fun deserialize(raw: String): OneBotApiResult<T>
}

/**
 * 提供一个 [OneBotApiResult.data] 的类型 [T] 的序列化器，
 * 来作为 [CustomOneBotApiBuilder] 的反序列化方案。
 *
 * @see CustomOneBotApiBuilder.deserializer
 *
 * @since 1.9.0
 */
@ExperimentalCustomOneBotApi
public fun <T : Any> CustomOneBotApiBuilder<T>.dataDeserializer(
    dataDeserializer: KSerializer<T>
): CustomOneBotApiBuilder<T> =
    deserializer(OneBotApiResult.serializer(dataDeserializer))


/**
 * [CustomOneBotApi] DSL builder function.
 *
 * @since 1.9.0
 */
@ExperimentalCustomOneBotApi
public inline fun <T : Any> CustomOneBotApi(
    action: String,
    method: HttpMethod = HttpMethod.Post,
    block: CustomOneBotApiBuilder<T>.() -> Unit
): CustomOneBotApi<T> = CustomOneBotApi.builder<T>(action, method).apply(block).build()
