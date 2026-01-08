/*
 * Copyright (c) 2024. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.qguild.api.app

import io.ktor.http.*
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.ApiModel
import love.forte.simbot.qguild.api.PostQQGuildApi
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

    @Serializable
    public data class Body(
        val appId: String,
        val clientSecret: String
    )
}

/**
 * Result of [GetAppAccessTokenApi]
 * @property accessToken 获取到的凭证。
 * @property expiresIn 凭证有效时间，单位：秒。目前是7200秒之内的值。
 */
@ApiModel
@Serializable
public data class AppAccessToken(
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("expires_in")
    val expiresIn: Int,
)
