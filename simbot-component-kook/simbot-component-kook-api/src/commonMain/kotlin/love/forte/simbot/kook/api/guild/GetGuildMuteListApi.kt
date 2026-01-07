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

package love.forte.simbot.kook.api.guild

import io.ktor.http.*
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import love.forte.simbot.kook.api.ApiResultType
import love.forte.simbot.kook.api.KookGetApi
import love.forte.simbot.kook.util.parameters


/**
 * [服务器静音闭麦列表](https://developer.kookapp.cn/doc/http/guild#%E6%9C%8D%E5%8A%A1%E5%99%A8%E9%9D%99%E9%9F%B3%E9%97%AD%E9%BA%A6%E5%88%97%E8%A1%A8)
 *
 * @author ForteScarlet
 */
public class GetGuildMuteListApi private constructor(private val guildId: String) : KookGetApi<MuteList>() {
    public companion object Factory {
        private val PATH = ApiPath.create("guild-mute", "list")
    }

    override val resultDeserializationStrategy: DeserializationStrategy<MuteList>
        get() = MuteList.serializer()

    override val apiPath: ApiPath
        get() = PATH

    override fun urlBuild(builder: URLBuilder) {
        builder.parameters {
            append("guild_id", guildId)
            // > 返回格式，建议为"detail", 其他情况仅作为兼容
            append("return_type", "detail")
        }
    }

}

/**
 * [GetGuildMuteListApi] 的响应结果。
 *
 */
@Serializable
public data class MuteList @ApiResultType constructor(
    /**
     * 麦克风闭麦
     */
    val mic: Data,
    /**
     * 耳机静音
     */
    val headset: Data
) {

    /**
     * [MuteList] 中 [mic] 和 [headset] 的数据结构，用于承载用户ID的集合。
     */
    @Serializable
    public data class Data @ApiResultType constructor(val userIds: List<String> = emptyList())
}
