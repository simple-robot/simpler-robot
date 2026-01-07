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

package love.forte.simbot.component.onebot.v11.core.bot

import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.bot.SerializableBotConfiguration
import love.forte.simbot.component.onebot.common.annotations.InternalOneBotAPI
import love.forte.simbot.component.onebot.v11.core.component.OneBot11Component
import love.forte.simbot.component.onebot.v11.message.segment.OneBotImage


/**
 * 用于可序列化场景下的反序列化目标。
 *
 * 仅用于序列化，不保证代码内直接使用的兼容性与稳定性，
 * 请不要直接在代码中使用它。
 *
 * @author ForteScarlet
 */
@InternalOneBotAPI
@Serializable
@SerialName(OneBot11Component.ID_VALUE)
public data class OneBotBotSerializableConfiguration(
    val authorization: Authorization,
    val config: Config? = null,
) : SerializableBotConfiguration() {
    @Serializable
    public data class Authorization(
        val botUniqueId: String,
        val apiServerHost: String? = null,
        val eventServerHost: String? = null,
        val accessToken: String? = null,
        val apiAccessToken: String? = null,
        val eventAccessToken: String? = null,
    )

    @Serializable
    public data class Config(
        /**
         * @see OneBotBotConfiguration.apiHttpRequestTimeoutMillis
         */
        val apiHttpRequestTimeoutMillis: Long? = null,
        /**
         * @see OneBotBotConfiguration.apiHttpConnectTimeoutMillis
         */
        val apiHttpConnectTimeoutMillis: Long? = null,
        /**
         * @see OneBotBotConfiguration.apiHttpSocketTimeoutMillis
         */
        val apiHttpSocketTimeoutMillis: Long? = null,
        /**
         * @see OneBotBotConfiguration.wsConnectMaxRetryTimes
         */
        val wsConnectMaxRetryTimes: Int? = null,
        /**
         * @see OneBotBotConfiguration.wsConnectRetryDelayMillis
         */
        val wsConnectRetryDelayMillis: Long? = null,

        /**
         * @see OneBotBotConfiguration.defaultImageAdditionalParams
         */
        val defaultImageAdditionalParams: AdditionalParams? = null,
    )

    @Serializable
    public data class AdditionalParams(
        val localFileToBase64: Boolean = false,
        val type: String? = null,
        val cache: Boolean? = null,
        val proxy: Boolean? = null,
        val timeout: Int? = null,
    ) {
        internal fun resolve(): OneBotImage.AdditionalParams =
            OneBotImage.AdditionalParams().apply {
                localFileToBase64 = this@AdditionalParams.localFileToBase64
                type = this@AdditionalParams.type
                cache = this@AdditionalParams.cache
                proxy = this@AdditionalParams.proxy
                timeout = this@AdditionalParams.timeout
            }
    }

    public fun toConfiguration(): OneBotBotConfiguration =
        OneBotBotConfiguration().also { conf ->
            conf.botUniqueId = authorization.botUniqueId
            authorization.apiServerHost?.also { conf.apiServerHost = Url(it) }
            authorization.eventServerHost?.also { conf.eventServerHost = Url(it) }
            authorization.accessToken?.also { conf.accessToken(it) }
            authorization.apiAccessToken?.also { conf.apiAccessToken = it }
            authorization.eventAccessToken?.also { conf.eventAccessToken = it }

            config?.apply {
                apiHttpRequestTimeoutMillis?.also { conf.apiHttpRequestTimeoutMillis = it }
                apiHttpConnectTimeoutMillis?.also { conf.apiHttpConnectTimeoutMillis = it }
                apiHttpSocketTimeoutMillis?.also { conf.apiHttpSocketTimeoutMillis = it }
                wsConnectMaxRetryTimes?.also { conf.wsConnectMaxRetryTimes = it }
                wsConnectRetryDelayMillis?.also { conf.wsConnectRetryDelayMillis = it }
                defaultImageAdditionalParams?.also { conf.defaultImageAdditionalParams(it.resolve()) }
            }
        }
}
