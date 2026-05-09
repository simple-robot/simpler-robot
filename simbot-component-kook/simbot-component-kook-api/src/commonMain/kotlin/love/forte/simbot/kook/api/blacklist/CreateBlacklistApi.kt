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

package love.forte.simbot.kook.api.blacklist

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import love.forte.simbot.kook.api.KookPostApi
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * [加入黑名单](https://developer.kookapp.cn/doc/http/blacklist#%E5%8A%A0%E5%85%A5%E9%BB%91%E5%90%8D%E5%8D%95)
 *
 * @since 4.3.0
 *
 * @author ForteScarlet
 */
public class CreateBlacklistApi private constructor(
    private val guildId: String,
    private val targetId: String,
    private val remark: String? = null,
    private val delMsgDays: Int? = null,
) : KookPostApi<Unit>() {
    public companion object Factory {
        private val PATH = ApiPath.create("blacklist", "create")

        /**
         * 构建 [CreateBlacklistApi]
         *
         * @param guildId 服务器id
         * @param targetId 目标用户id
         * @param remark 加入黑名单的原因
         * @param delMsgDays 删除最近几天的消息，最大 7 天, 默认 0
         */
        @JvmStatic
        @JvmOverloads
        public fun create(
            guildId: String,
            targetId: String,
            remark: String? = null,
            delMsgDays: Int? = null
        ): CreateBlacklistApi =
            CreateBlacklistApi(guildId, targetId, remark, delMsgDays)
    }

    override val apiPath: ApiPath
        get() = PATH

    override val resultDeserializationStrategy: DeserializationStrategy<Unit>
        get() = Unit.serializer()

    override fun createBody(): Any = Body(guildId, targetId, remark, delMsgDays)

    @Serializable
    private data class Body(
        @SerialName("guild_id")
        val guildId: String,
        @SerialName("target_id")
        val targetId: String,
        val remark: String? = null,
        @SerialName("del_msg_days")
        val delMsgDays: Int? = null,
    )
}
