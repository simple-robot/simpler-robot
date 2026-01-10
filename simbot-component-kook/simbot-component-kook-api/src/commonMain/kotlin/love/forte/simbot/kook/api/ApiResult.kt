/*
 *     Copyright (c) 2022-2026. ForteScarlet.
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

package love.forte.simbot.kook.api

import io.ktor.http.*
import kotlinx.serialization.*
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import love.forte.simbot.kook.InternalKookApi
import love.forte.simbot.kook.api.KookApiResults.SUCCESS_CODE
import love.forte.simbot.kook.api.RateLimit.Companion.DEFAULT_LIMIT
import love.forte.simbot.kook.api.RateLimit.Companion.DEFAULT_REMAINING
import love.forte.simbot.kook.api.RateLimit.Companion.DEFAULT_RESET
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmSynthetic


/**
 *
 * Kook Api的响应值标准格式。
 *
 * 参考 [文档](https://developer.kaiheila.cn/doc/reference#%E5%B8%B8%E8%A7%84%20http%20%E6%8E%A5%E5%8F%A3%E8%A7%84%E8%8C%83).
 *
 * 响应值类型无外乎三种形式：列表、对象、空。
 *
 * ```json
 *{
 *    "code" : 0, // integer, 错误码，0代表成功，非0代表失败，具体的错误码参见错误码一览
 *    "message" : "error info", // string, 错误消息，具体的返回消息会根据Accept-Language来返回。
 *    "data" : [], // mixed, 具体的数据。
 *}
 * ```
 *
 *
 * 当返回数据为对象时，使用 [KookApi] 的预期结果即为目标结果。
 *
 * 当返回数据为空时，使用 [KookApi] 的预期结果考虑使用不变常量，例如 [Unit].
 *
 * 当返回数据为列表时，使用 [KookApi] 的预期结果考虑使用 [ListData].
 *
 * @author ForteScarlet
 */
public object KookApiResults {
    /**
     * 代表成功的 '错误码'。
     */
    public const val SUCCESS_CODE: Int = 0

    /**
     * 响应体中作为 `code` 的属性名, Int类型。
     *
     * 错误码，0代表成功，非0代表失败。
     *
     * @see SUCCESS_CODE
     */
    public const val CODE_PROPERTY_NAME: String = "code"

    /**
     * 响应体中作为 `message` 的属性名。
     *
     * 错误消息，具体的返回消息会根据Accept-Language来返回。
     */
    public const val MESSAGE_PROPERTY_NAME: String = "message"


    /**
     * 响应体中作为 `data` 的属性名。
     */
    public const val DATA_PROPERTY_NAME: String = "data"
}


/**
 * 当响应体中的 `data` 为列表类型时，其有特殊的数据格式:
 * ```json
 * {
 *      "items": [...],
 *      "meta": {
 *          "page": 1,
 *          "page_total": 10,
 *          "page_size": 50,
 *          "total": 480
 *      },
 *      "sort": { "id": 2 }
 * }
 * ```
 *
 * @see ListData
 *
 */
public abstract class ListDataResponse<out T, SORT> {
    public abstract val items: List<T>
    public abstract val meta: ListMeta
    public abstract val sort: SORT
}


/**
 * 当返回值为列表时的分页响应元数据。
 */
@Serializable
public data class ListMeta(
    val page: Int,
    @SerialName("page_total")
    val pageTotal: Int,
    @SerialName("page_size")
    val pageSize: Int,
    val total: Int,
)

/**
 *
 * [ListData] 将 sort 类型直接视为 Map 进行解析。
 *
 * @param T 数据元素的类型。
 */
@Serializable
public class ListData<out T>(
    override val items: List<T> = emptyList(),
    override val meta: ListMeta,
    override val sort: Map<String, Int> = emptyMap()
) : ListDataResponse<T, Map<String, Int>>(), Iterable<T> by items {
    override fun toString(): String {
        return "ListData(items=$items, meta=$meta, sort=$sort)"
    }
}

/**
 * 对 KOOK API 标准响应数据的封装。
 *
 * [ApiResult] 由内部反序列化器构造并组装，不应被外界实例化。
 */
@Suppress("MemberVisibilityCanBePrivate")
@Serializable
public class ApiResult @ApiResultType constructor(
    /**
     * 错误码，`0`代表成功，非`0`代表失败，具体的错误码参见错误码一览
     */
    public val code: Int,
    /**
     * 错误消息，具体的返回消息会根据Accept-Language来返回。
     */
    public val message: String,
    /**
     * mixed, 具体的数据
     */
    public val data: JsonElement = EMPTY_OBJECT,
) {

    /**
     * 当前 result 反序列化前的原始JSON字符串。
     */
    @Transient
    public lateinit var raw: String
        @JvmSynthetic @InternalKookApi internal set // hide from Java, warning to Kt

    /**
     * 当前api响应值的 [速率限制][RateLimit] 信息。
     */
    @Transient
    public lateinit var rateLimit: RateLimit
        @JvmSynthetic @InternalKookApi internal set // hide from Java, warning to Kt

    /**
     * 此API的HTTP响应状态码
     *
     * @see HttpStatusCode
     */
    @Transient
    public lateinit var httpStatus: HttpStatusCode
        @JvmSynthetic @InternalKookApi internal set // hide from Java, warning to Kt

    /**
     * http响应状态码
     *
     * @see httpStatus
     */
    public val httpStatusCode: Int get() = httpStatus.value

    /**
     * http响应状态描述
     *
     * @see httpStatus
     */
    public val httpStatusDescription: String get() = httpStatus.description

    /**
     * 此接口的响应码是否为成功的响应码.
     */
    public val isSuccess: Boolean get() = isHttpSuccess && code == SUCCESS_CODE

    /**
     * 判断 [httpStatus] 是否 [成功][HttpStatusCode.isSuccess]。
     */
    public val isHttpSuccess: Boolean get() = httpStatus.isSuccess()

    /**
     * 提供解析参数来使用当前result中的data内容解析为目标结果。
     * 不会有任何判断，
     *
     * @param json 用于解析 [data] 的json反序列化器
     * @param deserializationStrategy 解析目标的反序列化策略，参考 [KookApi.resultDeserializationStrategy]
     * @throws SerializationException see [Json.decodeFromJsonElement].
     */
    @JvmOverloads
    public fun <T> parseData(
        json: Json = KookApi.DEFAULT_JSON,
        deserializationStrategy: DeserializationStrategy<T>
    ): T {
        if (deserializationStrategy == Unit.serializer()) {
            @Suppress("UNCHECKED_CAST")
            return Unit as T
        }

        return json.decodeFromJsonElement(deserializationStrategy, data)
    }

    /**
     * 当 [code] 为成功的时候解析 data 数据, 如果 [code] 不为成功([KookApiResults.SUCCESS_CODE]), 则抛出 [ApiResultException] 异常。
     *
     * @param json 用于解析 [data] 的json反序列化器
     * @param deserializationStrategy 解析目标的反序列化策略，参考 [KookApi.resultDeserializationStrategy]
     * @throws ApiResultException 如果 [code] 不为成功
     * @throws SerializationException see [Json.decodeFromJsonElement].
     */
    @JvmOverloads
    public fun <T> parseDataOrThrow(
        json: Json = KookApi.DEFAULT_JSON,
        deserializationStrategy: DeserializationStrategy<T>
    ): T {
        if (!isSuccess) {
            throw ApiResultException(this, message)
        }
        return parseData(json, deserializationStrategy)
    }


    override fun toString(): String =
        "ApiResult(" +
                "code=$code, " +
                "message=$message, " +
                "data=$data, " +
                "httpStatus=${httpStatusOrNull ?: "<Not Initialized>"}, " +
                "rateLimit=${rateLimitOrNull ?: "<Not Initialized>"}, " +
                "raw=${rawOrNull ?: "<Not Initialized>"})"

    private val httpStatusOrNull
        get() = if (::httpStatus.isInitialized) httpStatus else null

    private val rateLimitOrNull
        get() = if (::rateLimit.isInitialized) rateLimit else null

    private val rawOrNull
        get() = if (::raw.isInitialized) raw else null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ApiResult) return false

        if (code != other.code) return false
        if (message != other.message) return false
        return data == other.data
    }

    override fun hashCode(): Int {
        var result = code
        result = 31 * result + message.hashCode()
        result = 31 * result + data.hashCode()
        return result
    }

    public companion object {
        private val EMPTY_OBJECT = buildJsonObject {}
    }
}


/**
 * [速率限制](https://developer.kaiheila.cn/doc/rate-limit) 请求头中的数据体。
 */
@Serializable
public data class RateLimit(
    /**
     * `X-Rate-Limit-Limit`, 一段时间内允许的最大请求次数，
     * 当无法获取时填充 [`-1`][DEFAULT_LIMIT]。
     */
    public val limit: Long,

    /**
     * `X-Rate-Limit-Remaining`, 一段时间内还剩下的请求数，
     * 当无法获取时填充 [`-1`][DEFAULT_REMAINING]。
     */
    public val remaining: Long,

    /**
     * `X-Rate-Limit-Reset`, 回复到最大请求次数需要等待的时间，
     * 当无法获取时填充 [`-1`][DEFAULT_RESET]。
     */
    public val reset: Long,

    /**
     * `X-Rate-Limit-Bucket`, 请求数的bucket
     */
    public val bucket: String?,

    /**
     * `X-Rate-Limit-Global`, 触犯全局请求次数限制
     */
    public val isGlobalLimit: Boolean,

    ) {
    public companion object {
        public const val X_RATE_LIMIT_LIMIT: String = ApiRateLimits.RATE_LIMIT_LIMIT_HEAD // "X-Rate-Limit-Limit"
        public const val X_RATE_LIMIT_REMAINING: String =
            ApiRateLimits.RATE_LIMIT_REMAINING_HEAD // "X-Rate-Limit-Remaining"
        public const val X_RATE_LIMIT_RESET: String = ApiRateLimits.RATE_LIMIT_RESET_HEAD // "X-Rate-Limit-Reset"
        public const val X_RATE_LIMIT_BUCKET: String = ApiRateLimits.RATE_LIMIT_BUCKET_HEAD // "X-Rate-Limit-Bucket"
        public const val X_RATE_LIMIT_GLOBAL: String = ApiRateLimits.RATE_LIMIT_GLOBAL_HEAD // "X-Rate-Limit-Global"

        public const val DEFAULT_LIMIT: Long = -1
        public const val DEFAULT_REMAINING: Long = -1
        public const val DEFAULT_RESET: Long = -1
    }
}
