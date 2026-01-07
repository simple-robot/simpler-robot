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

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.component.onebot.common.annotations.ApiResultConstructor
import kotlin.jvm.JvmStatic

/**
 * [`get_version_info`-获取版本信息](https://github.com/botuniverse/onebot-11/blob/master/api/public.md#get_version_info-获取版本信息)
 *
 * @author ForteScarlet
 */
public class GetVersionInfoApi private constructor() : OneBotApi<GetVersionInfoResult> {
    override val action: String
        get() = ACTION

    override val resultDeserializer: DeserializationStrategy<GetVersionInfoResult>
        get() = GetVersionInfoResult.serializer()

    override val apiResultDeserializer:
        DeserializationStrategy<OneBotApiResult<GetVersionInfoResult>>
        get() = RES_SER

    override val body: Any?
        get() = null

    public companion object Factory {
        private const val ACTION: String = "get_version_info"

        private val RES_SER: KSerializer<OneBotApiResult<GetVersionInfoResult>> =
            OneBotApiResult.serializer(GetVersionInfoResult.serializer())

        private val INSTANCE: GetVersionInfoApi = GetVersionInfoApi()

        /**
         * 构建一个 [GetVersionInfoApi].
         */
        @JvmStatic
        public fun create(): GetVersionInfoApi = INSTANCE
    }
}

/**
 * [GetVersionInfoApi] 的响应体。
 *
 * @property appName 应用标识，如 `mirai-native`
 * @property appVersion 应用版本，如 `1.2.3`
 * @property protocolVersion OneBot 标准版本，如 `v11`
 */
@Serializable
public data class GetVersionInfoResult @ApiResultConstructor constructor(
    @SerialName("app_name")
    public val appName: String,
    @SerialName("app_version")
    public val appVersion: String,
    @SerialName("protocol_version")
    public val protocolVersion: String,
    // public val `……`: Nothing = ("……?"),
)
