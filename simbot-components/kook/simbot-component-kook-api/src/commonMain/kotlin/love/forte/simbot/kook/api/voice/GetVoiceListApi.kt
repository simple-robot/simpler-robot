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

package love.forte.simbot.kook.api.voice

import io.ktor.http.*
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.kook.api.ApiResultType
import love.forte.simbot.kook.api.KookGetApi
import love.forte.simbot.kook.api.ListData
import love.forte.simbot.kook.util.appendIfNotNull
import love.forte.simbot.kook.util.parameters
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * [获取频道列表](https://developer.kookapp.cn/doc/http/voice#获取频道列表)
 *
 * 获取机器人加入的语音频道列表
 *
 * @author ForteScarlet
 * @since 4.2.0
 */
public class GetVoiceListApi private constructor(
    private val page: Int? = null,
    private val pageSize: Int? = null
) : KookGetApi<ListData<JoinedVoiceChannel>>() {

    public companion object Factory {
        private val PATH = ApiPath.create("voice", "list")

        /**
         * 构造 [GetVoiceListApi].
         */
        @JvmStatic
        @JvmOverloads
        public fun create(page: Int? = null, pageSize: Int? = null): GetVoiceListApi = GetVoiceListApi(page, pageSize)
    }

    override val resultDeserializationStrategy: DeserializationStrategy<ListData<JoinedVoiceChannel>>
        get() = ListData.serializer(JoinedVoiceChannel.serializer())

    override val apiPath: ApiPath
        get() = PATH

    override fun urlBuild(builder: URLBuilder) {
        builder.parameters {
            appendIfNotNull("page", page) { it.toString() }
            appendIfNotNull("page_size", pageSize) { it.toString() }
        }
    }
}

/**
 * (机器人加入的)语音频道的信息
 */
@Serializable
public data class JoinedVoiceChannel @ApiResultType constructor(
    /**
     * 频道id
     */
    val id: String,
    /**
     * 服务器id
     */
    @SerialName("guild_id")
    val guildId: String,
    /**
     * 频道的父频道id
     */
    @SerialName("parent_id")
    val parentId: String,
    /**
     * 频道名
     */
    val name: String
)
