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
 * [`get_record`-获取语音](https://github.com/botuniverse/onebot-11/blob/master/api/public.md#get_record-获取语音)
 *
 * @author ForteScarlet
 */
public class GetRecordApi private constructor(
    override val body: Any,
) : OneBotApi<GetRecordResult> {
    override val action: String
        get() = ACTION

    override val resultDeserializer: DeserializationStrategy<GetRecordResult>
        get() = GetRecordResult.serializer()

    override val apiResultDeserializer: DeserializationStrategy<OneBotApiResult<GetRecordResult>>
        get() = RES_SER

    public companion object Factory {
        private const val ACTION: String = "get_record"

        private val RES_SER: KSerializer<OneBotApiResult<GetRecordResult>> =
            OneBotApiResult.serializer(GetRecordResult.serializer())

        /**
         * 构建一个 [GetRecordApi].
         *
         * @param file 收到的语音文件名（消息段的 `file` 参数），如 `0B38145AA44505000B38145AA4450500.silk`
         * @param outFormat 要转换到的格式，目前支持 `mp3`、`amr`、`wma`、`m4a`、`spx`、`ogg`、`wav`、`flac`
         */
        @JvmStatic
        public fun create(file: String, outFormat: String): GetRecordApi = GetRecordApi(
            Body(
                file,
                outFormat
            )
        )
    }

    /**
     * @property file 收到的语音文件名（消息段的 `file` 参数），如 `0B38145AA44505000B38145AA4450500.silk`
     * @property outFormat 要转换到的格式，目前支持 `mp3`、`amr`、`wma`、`m4a`、`spx`、`ogg`、`wav`、`flac`
     */
    @Serializable
    internal data class Body(
        internal val file: String,
        @SerialName("out_format")
        internal val outFormat: String,
    )
}

/**
 * [GetRecordApi] 的响应体。
 *
 * @property file 转换后的语音文件路径，如 `/home/somebody/cqhttp/data/record/0B38145AA44505000B38145AA4450500.mp3`
 */
@Serializable
public data class GetRecordResult @ApiResultConstructor constructor(
    public val file: String,
)
