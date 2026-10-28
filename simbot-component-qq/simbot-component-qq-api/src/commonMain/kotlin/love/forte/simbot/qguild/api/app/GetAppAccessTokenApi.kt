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

package love.forte.simbot.qguild.api.app

import io.ktor.http.*
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.api.PostQQGuildApi
import love.forte.simbot.qguild.common.ApiModel
import love.forte.simbot.qguild.common.ApiModelConstructor
import love.forte.simbot.qguild.common.DataClassCompatibilities
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic


/**
 * [获取调用凭证](https://bot.q.qq.com/wiki/develop/api-v2/dev-prepare/interface-framework/api-use.html#获取调用凭证)
 *
 * 这个API似乎是一个特殊的API，它有自己的HTTP URL: `https://bots.qq.com/app/getAppAccessToken`，
 * 使用时需要专门处理。
 *
 * @author ForteScarlet
 */
public class GetAppAccessTokenApi private constructor(
    override val body: Body
) : PostQQGuildApi<AppAccessToken>() {
    public companion object Factory {
        public const val HTTP_URL_STRING: String = "https://bots.qq.com"

        @JvmField
        public val httpUrl: Url = Url(HTTP_URL_STRING)

        private val PATH = arrayOf("app", "getAppAccessToken")

        /**
         * Create [GetAppAccessTokenApi].
         */
        @JvmStatic
        public fun create(
            appId: String,
            clientSecret: String
        ): GetAppAccessTokenApi = GetAppAccessTokenApi(Body(appId, clientSecret))
    }


    override val resultDeserializationStrategy: DeserializationStrategy<AppAccessToken>
        get() = AppAccessToken.serializer()

    override val path: Array<String>
        get() = PATH

    override fun createBody(): Any = body

    override val url: Url = URLBuilder(httpUrl).apply {
        appendEncodedPathSegments(components = PATH)
    }.build()

    /**
     * 获取调用凭证的请求体。
     *
     * @property appId 应用 ID。
     * @property clientSecret 应用密钥。
     */
    @Serializable
    public class Body @ApiModelConstructor public constructor(
        public val appId: String,
        public val clientSecret: String
    ) {
        public companion object {
            /**
             * 构造 [Body]。
             *
             * @since 5.0
             */
            @JvmStatic
            public fun of(appId: String, clientSecret: String): Body = Body(appId, clientSecret)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Body) return false

            if (appId != other.appId) return false
            if (clientSecret != other.clientSecret) return false

            return true
        }

        override fun hashCode(): Int {
            var result = appId.hashCode()
            result = 31 * result + clientSecret.hashCode()
            return result
        }

        override fun toString(): String = "Body(appId=$appId, clientSecret=$clientSecret)"

        //region data-class 兼容
        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("appId"))
        public operator fun component1(): String = appId

        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("clientSecret"))
        public operator fun component2(): String = clientSecret

        @Suppress("DeprecatedCallableAddReplaceWith")
        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
        public fun copy(
            appId: String = this.appId,
            clientSecret: String = this.clientSecret,
        ): Body = Body(appId, clientSecret)
        //endregion
    }
}

/**
 * Result of [GetAppAccessTokenApi]
 * @property accessToken 获取到的凭证。
 * @property expiresIn 凭证有效时间，单位：秒。目前是7200秒之内的值。
 */
@ApiModel
@Serializable
public class AppAccessToken @ApiModelConstructor public constructor(
    @SerialName("access_token")
    public val accessToken: String,
    @SerialName("expires_in")
    public val expiresIn: Int,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AppAccessToken) return false

        if (accessToken != other.accessToken) return false
        if (expiresIn != other.expiresIn) return false

        return true
    }

    override fun hashCode(): Int {
        var result = accessToken.hashCode()
        result = 31 * result + expiresIn
        return result
    }

    override fun toString(): String = "AppAccessToken(accessToken=$accessToken, expiresIn=$expiresIn)"

    //region data-class 兼容
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("accessToken"))
    public operator fun component1(): String = accessToken

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("expiresIn"))
    public operator fun component2(): Int = expiresIn

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
    public fun copy(
        accessToken: String = this.accessToken,
        expiresIn: Int = this.expiresIn,
    ): AppAccessToken = AppAccessToken(accessToken, expiresIn)
    //endregion
}
