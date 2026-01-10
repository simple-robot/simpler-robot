/*
 *     Copyright (c) 2026. ForteScarlet.
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

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.component.onebot.v11.core.api.OneBotApi
import love.forte.simbot.component.onebot.v11.core.api.OneBotApiResult

// LLOneBot GetFile API

class GetFileApi private constructor(
    override val body: Any
) : OneBotApi<GetFileResult> {

    override val action: String
        get() = ACTION

    override val resultDeserializer: DeserializationStrategy<GetFileResult>
        get() = GetFileResult.serializer()

    override val apiResultDeserializer: DeserializationStrategy<OneBotApiResult<GetFileResult>>
        get() = RESULT_RES

    companion object {
        private const val ACTION: String = "get_file"
        private val RESULT_RES = OneBotApiResult.serializer(GetFileResult.serializer())

        @JvmStatic
        fun create(fileId: String): GetFileApi {
            return GetFileApi(Body(fileId))
        }
    }

    @Serializable
    internal data class Body(
        @SerialName("file_id")
        internal val fileId: String,
    )
}

@Serializable
data class GetFileResult(
    /**
     * 文件的绝对路径
     */
    val file: String,
    @SerialName("file_name")
    val fileName: String = "",
    @SerialName("file_size")
    val fileSize: Long = -1L,
    val base64: String = "",
)
