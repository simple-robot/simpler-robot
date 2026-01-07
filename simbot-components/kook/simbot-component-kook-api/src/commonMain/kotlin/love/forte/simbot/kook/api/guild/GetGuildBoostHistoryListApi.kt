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
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.kook.api.KookGetApi
import love.forte.simbot.kook.api.ListData
import love.forte.simbot.kook.objects.SimpleUser
import love.forte.simbot.kook.util.parameters
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic


/**
 * [服务器助力历史](https://developer.kookapp.cn/doc/http/guild#%E6%9C%8D%E5%8A%A1%E5%99%A8%E5%8A%A9%E5%8A%9B%E5%8E%86%E5%8F%B2)
 *
 * > 需要有 `服务器管理` 权限
 *
 * @author ForteScarlet
 */
public class GetGuildBoostHistoryListApi private constructor(
    private val guildId: String,
    private val startTime: Int? = null,
    private val endTime: Int? = null,
) : KookGetApi<ListData<BoostData>>() {
    public companion object Factory {
        private val PATH = ApiPath.create("guild-boost", "history")
        private val deserializer = ListData.serializer(BoostData.serializer())

        /**
         * 构造 [GetGuildBoostHistoryListApi].
         *
         * @param guildId 服务器 id
         * @param startTime unix 时间戳，时间范围的开始时间 (单位: 秒)
         * @param endTime unix 时间戳，时间范围的结束时间 (单位: 秒)
         *
         */
        @JvmStatic
        @JvmOverloads
        public fun create(guildId: String, startTime: Int? = null, endTime: Int? = null): GetGuildBoostHistoryListApi =
            GetGuildBoostHistoryListApi(guildId, startTime, endTime)

    }

    override val resultDeserializationStrategy: DeserializationStrategy<ListData<BoostData>>
        get() = deserializer

    override val apiPath: ApiPath
        get() = PATH

    override fun urlBuild(builder: URLBuilder) {
        builder.parameters {
            append("guild_id", guildId)
            startTime?.let { append("start_time", it.toString()) }
            endTime?.let { append("end_time", it.toString()) }
        }
    }
}

/**
 * 使用助力包的用户数据对象
 *
 * @see GetGuildBoostHistoryListApi
 */
@Serializable
public data class BoostData(
    @SerialName("user_id") val userId: String,
    @SerialName("guild_id") val guildId: String,
    @SerialName("start_time") val startTime: Int,
    @SerialName("end_time") val endTime: Int,
    val user: SimpleUser,
)
