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
import kotlinx.serialization.Serializable
import love.forte.simbot.component.onebot.common.annotations.ApiResultConstructor
import kotlin.jvm.JvmStatic

/**
 * [`can_send_image`-检查是否可以发送图片](https://github.com/botuniverse/onebot-11/blob/master/api/public.md#can_send_image-检查是否可以发送图片)
 *
 * @author ForteScarlet
 */
public class CanSendImageApi private constructor() : OneBotApi<CanSendImageResult> {
    override val action: String
        get() = ACTION

    override val resultDeserializer: DeserializationStrategy<CanSendImageResult>
        get() = CanSendImageResult.serializer()

    override val apiResultDeserializer: DeserializationStrategy<OneBotApiResult<CanSendImageResult>>
        get() = RES_SER

    override val body: Any?
        get() = null

    public companion object Factory {
        private const val ACTION: String = "can_send_image"

        private val RES_SER: KSerializer<OneBotApiResult<CanSendImageResult>> =
            OneBotApiResult.serializer(CanSendImageResult.serializer())

        private val INSTANCE: CanSendImageApi = CanSendImageApi()

        /**
         * 构建一个 [CanSendImageApi].
         */
        @JvmStatic
        public fun create(): CanSendImageApi = INSTANCE
    }
}

/**
 * [CanSendImageApi] 的响应体。
 *
 * @property yes 是或否
 */
@Serializable
public data class CanSendImageResult @ApiResultConstructor constructor(
    public val yes: Boolean,
)
