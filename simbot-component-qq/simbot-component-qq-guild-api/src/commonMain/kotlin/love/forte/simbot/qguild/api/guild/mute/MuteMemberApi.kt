/*
 * Copyright (c) 2023-2024. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.qguild.api.guild.mute

import love.forte.simbot.common.time.TimeUnit
import love.forte.simbot.qguild.api.PatchQQGuildApi
import love.forte.simbot.qguild.api.QQGuildApiWithoutResult
import love.forte.simbot.qguild.api.SimplePatchApiDescription
import kotlin.jvm.JvmStatic
import kotlin.jvm.JvmSynthetic
import kotlin.time.Duration


/**
 * [禁言指定成员](https://bot.q.qq.com/wiki/develop/api/openapi/guild/patch_guild_member_mute.html)
 *
 * 用于禁言频道 `guild_id` 下的成员 `user_id`。
 *
 * 需要使用的 `token` 对应的用户具备管理员权限。如果是机器人，要求被添加为管理员。
 * 该接口同样可用于解除禁言，将 `mute_end_timestamp` 或 `mute_seconds` 传值为字符串'0'即可。
 *
 * @author ForteScarlet
 */
public class MuteMemberApi private constructor(guildId: String, userId: String, private val _body: MuteBody) :
    QQGuildApiWithoutResult, PatchQQGuildApi<Unit>() {
    public companion object Factory : SimplePatchApiDescription(
        "/guilds/{guild_id}/members/{user_id}/mute"
    ) {

        /**
         * 使用 `mute_seconds` 的方式构建 [MuteMemberApi]
         * @param muteSeconds 禁言多少秒
         *
         * @throws IllegalArgumentException [muteSeconds] 小于0
         */
        @JvmSynthetic
        public fun create(guildId: String, userId: String, muteSeconds: Duration): MuteMemberApi =
            createBySeconds(guildId, userId, muteSeconds.inWholeSeconds)


        /**
         * 使用 `mute_seconds` 的方式构建 [MuteMemberApi]
         * @param muteTime 禁言时长
         * @param timeUnit 时间单位
         *
         * @throws IllegalArgumentException [muteTime] 转为秒值后小于0
         */
        @JvmStatic
        public fun create(guildId: String, userId: String, muteTime: Long, timeUnit: TimeUnit): MuteMemberApi =
            createBySeconds(guildId, userId, timeUnit.toSeconds(muteTime))

        /**
         * 构建 `mute_seconds` 为 `"0"` 的 [MuteMemberApi]
         */
        @JvmStatic
        public fun createUnmute(guildId: String, userId: String): MuteMemberApi =
            MuteMemberApi(guildId, userId, MuteBody.Unmute)


        private fun createBySeconds(guildId: String, userId: String, seconds: Long): MuteMemberApi {
            return when {
                seconds == 0L -> MuteMemberApi(guildId, userId, MuteBody.Unmute)
                seconds > 0L -> MuteMemberApi(guildId, userId, MuteBody(muteSeconds = seconds.toString()))
                else -> throw IllegalArgumentException("mute seconds must >= 0, but $seconds")
            }
        }
    }

    override val path: Array<String> = arrayOf("guilds", guildId, "members", userId, "mute")

    override fun createBody(): Any = _body
}

