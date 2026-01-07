/*
 *     Copyright (c) 2023-2026. ForteScarlet.
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

import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import love.forte.simbot.common.apidefinition.ApiDefinition
import love.forte.simbot.kook.InternalKookApi
import love.forte.simbot.kook.Kook
import love.forte.simbot.kook.util.buildUrl

/**
 * 一个用于表示 KOOK API 封装的抽象类。
 *
 * [KookApi] 面向 `Ktor` 并基于 `kotlinx.serialization` 进行反序列化。
 *
 * @param T 此API预期的响应结果数据类型
 *
 * @author ForteScarlet
 */
public interface KookApi<T : Any> : ApiDefinition<T> {
    /**
     * 此请求最终的url。
     */
    override val url: Url

    /**
     * 此请求使用的 [HttpMethod]。
     */
    override val method: HttpMethod

    /**
     * 预期结果类型的反序列化策略。
     *
     * 更多有关 API 响应体的说明参考 [KookApiResults]。
     *
     */
    override val resultDeserializationStrategy: DeserializationStrategy<T>

    /**
     * 此次请求所发送的Body数据。为null则代表没有参数。
     */
    override val body: Any?

    /**
     * 本次请求提供的请求头。
     */
    override val headers: Headers

    public companion object {
        /**
         * 常规情况下分页API的默认最大值。
         */
        public const val DEFAULT_MAX_PAGE_SIZE: Int = 50

        /**
         * 常规情况下分页API的默认起始页码
         */
        public const val DEFAULT_START_PAGE: Int = 1

        @OptIn(ExperimentalSerializationApi::class)
        @InternalKookApi
        public val DEFAULT_JSON: Json = Json {
            isLenient = true
            encodeDefaults = true
            ignoreUnknownKeys = true
            allowSpecialFloatingPointValues = true
            allowStructuredMapKeys = true
            prettyPrint = false
            useArrayPolymorphism = false
            // see https://github.com/kaiheila/api-docs/issues/174
            explicitNulls = false
        }
    }
}


/**
 * [KookApi] 的进一步抽象实现，简化针对 [KookApi.url] 的构造过程。
 */
public abstract class BaseKookApi<T : Any> : KookApi<T> {
    override val headers: Headers
        get() = Headers.Empty

    /**
     * API路径片段，例如：
     * ```
     * ApiPath(isEncoded = true /* by default */ , "foo", "bar") // -> url: /foo/bar
     * ```
     *
     * [apiPath] 的值基本静态，无特殊情况考虑置于伴生对象中。
     *
     */
    protected abstract val apiPath: ApiPath

    /**
     * [Url] 构建器，用于首次初始化 [url] 时执行的逻辑处理，
     * 默认无逻辑，实现类型在有需要的时候重写此方法。
     *
     * [url] 的初始化不加锁、不验证实例唯一，因此 [urlBuild] 可能会被执行多次。
     *
     */
    protected open fun urlBuild(builder: URLBuilder) {}

    private lateinit var _url: Url

    /**
     * [Url] 构建器，用于首次初始化 [url] 时执行，过程中会使用 [urlBuild]。
     *
     * [url] 的初始化不加锁、不验证实例唯一，因此 [initUrl] 可能会被执行多次。
     *
     */
    protected open fun initUrl(): Url {
        return buildUrl(Kook.SERVER_URL_WITH_VERSION) {
            apiPath.includeTo(this)
            urlBuild(this)
        }
    }

    /**
     * 根据当前API获取请求URL。
     *
     * [url] 是懒初始化的，会在获取时初始化。
     *
     * 初始化过程不加锁、不验证实例唯一。
     */
    override val url: Url
        get() = if (::_url.isInitialized) _url else initUrl().also { _url = it }

    /**
     * 提供API的完整路径后缀信息。
     *
     * _Internal type._
     */
    protected sealed class ApiPath {
        internal abstract val apiPath: List<String>
        internal abstract fun includeTo(builder: URLBuilder)

        public companion object {
            public fun create(isEncoded: Boolean, vararg apiPath: String): ApiPath {
                return if (isEncoded) {
                    EncodedApiPath(apiPath.asList())
                } else {
                    SimpleApiPath(apiPath.asList())
                }
            }

            public fun create(vararg apiPath: String): ApiPath = create(true, apiPath = apiPath)
        }

        private data class EncodedApiPath(override val apiPath: List<String>) : ApiPath() {
            override fun includeTo(builder: URLBuilder) {
                builder.appendEncodedPathSegments(apiPath)
            }
        }

        private data class SimpleApiPath(override val apiPath: List<String>) : ApiPath() {
            override fun includeTo(builder: URLBuilder) {
                builder.appendPathSegments(apiPath)
            }
        }
    }
}


/**
 * 使用 [`POST`][HttpMethod.Post] 进行请求的 [KookApi] 基础抽象。
 *
 * [KookPostApi] 对外暴露其用于请求的 [body] 实体。
 *
 */
public abstract class KookPostApi<T : Any> : BaseKookApi<T>() {
    override val method: HttpMethod
        get() = HttpMethod.Post

    @kotlin.concurrent.Volatile
    private lateinit var _body: Any

    /**
     * 用于请求的body实体。
     *
     * [body] 内部懒加载，
     * 但不保证任何时间得到的结果始终如一 （无锁）
     *
     * 可通过重写 [body] 来改变此行为
     *
     */
    override val body: Any?
        get() = if (::_body.isInitialized) _body.takeIf { it !is NULL } else {
            createBody().also {
                _body = it ?: NULL
            }
        }

    /**
     * 用于为 [body] 提供实例构造的函数。
     * 当得到null时 [body] 的结果即为null。
     */
    protected open fun createBody(): Any? = null

    /**
     * NULL Marker
     */
    private object NULL
}

/**
 * 使用 [`GET`][HttpMethod.Get] 进行请求的 [KookApi] 基础抽象。
 *
 *
 */
public abstract class KookGetApi<T : Any> : BaseKookApi<T>() {
    override val method: HttpMethod
        get() = HttpMethod.Get

    /**
     * 默认情况下body为null。
     */
    override val body: Any?
        get() = null
}
