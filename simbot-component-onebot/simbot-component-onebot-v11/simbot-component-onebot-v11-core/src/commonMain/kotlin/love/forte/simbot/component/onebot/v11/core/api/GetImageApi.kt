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
 * [`get_image`-获取图片](https://github.com/botuniverse/onebot-11/blob/master/api/public.md#get_image-获取图片)
 *
 * @author ForteScarlet
 */
public class GetImageApi private constructor(
    override val body: Any,
) : OneBotApi<GetImageResult> {
    override val action: String
        get() = ACTION

    override val resultDeserializer: DeserializationStrategy<GetImageResult>
        get() = GetImageResult.serializer()

    override val apiResultDeserializer: DeserializationStrategy<OneBotApiResult<GetImageResult>>
        get() = RES_SER

    public companion object Factory {
        private const val ACTION: String = "get_image"

        private val RES_SER: KSerializer<OneBotApiResult<GetImageResult>> =
            OneBotApiResult.serializer(GetImageResult.serializer())

        /**
         * 构建一个 [GetImageApi].
         *
         * @param file 收到的图片文件名（消息段的 `file` 参数），如 `6B4DE3DFD1BD271E3297859D41C530F5.jpg`
         */
        @JvmStatic
        public fun create(file: String): GetImageApi = GetImageApi(Body(file))
    }

    /**
     * @property file 收到的图片文件名（消息段的 `file` 参数），如 `6B4DE3DFD1BD271E3297859D41C530F5.jpg`
     */
    @Serializable
    internal data class Body(
        internal val file: String,
    )
}

/**
 * [GetImageApi] 的响应体。
 *
 * @property file 下载后的图片文件路径，如 `/home/somebody/cqhttp/data/image/6B4DE3DFD1BD271E3297859D41C530F5.jpg`
 */
@Serializable
public data class GetImageResult @ApiResultConstructor constructor(
    public val file: String,
)
