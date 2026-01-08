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
 * @suppress
 */
public abstract class BaseMuteAllApi internal constructor(guildId: String, private val _body: MuteBody) :
    QQGuildApiWithoutResult,
    PatchQQGuildApi<Unit>() {
    override val path: Array<String> = arrayOf("guilds", guildId, "mute")
    override fun createBody(): Any = _body
}

/**
 * [禁言全员](https://bot.q.qq.com/wiki/develop/api/openapi/guild/patch_guild_mute.html)
 *
 * 用于将频道的全体成员（非管理员）禁言。
 *
 * 需要使用的 `token` 对应的用户具备管理员权限。如果是机器人，要求被添加为管理员。
 * 该接口同样可用于解除禁言，将 `mute_end_timestamp` 或 `mute_seconds` 传值为字符串'0'即可。
 *
 *
 *
 * @author ForteScarlet
 */
public class MuteAllApi private constructor(guildId: String, body: MuteBody) : BaseMuteAllApi(guildId, body) {
    public companion object Factory : SimplePatchApiDescription(
        "/guilds/{guild_id}/mute"
    ) {
        /**
         * 使用 `mute_seconds` 的方式构建 [MuteAllApi]
         * @param muteSeconds 禁言多少秒
         *
         * @throws IllegalArgumentException [muteSeconds] 小于0
         */
        @JvmSynthetic
        public fun create(guildId: String, muteSeconds: Duration): MuteAllApi =
            createBySeconds(guildId, muteSeconds.inWholeSeconds)

        /**
         * 使用 `mute_seconds` 的方式构建 [MuteAllApi]
         * @param muteTime 禁言时长
         * @param timeUnit 时间单位
         *
         * @throws IllegalArgumentException [muteTime] 转为秒值后小于0
         */
        @JvmStatic
        public fun create(guildId: String, muteTime: Long, timeUnit: TimeUnit): MuteAllApi =
            createBySeconds(guildId, timeUnit.toSeconds(muteTime))

        /**
         * 构建 `mute_seconds` 为 `"0"` 的 [MuteAllApi]
         */
        @JvmStatic
        public fun createUnmute(guildId: String): MuteAllApi = MuteAllApi(guildId, MuteBody.Unmute)

        private fun createBySeconds(guildId: String, seconds: Long): MuteAllApi {
            return when {
                seconds == 0L -> MuteAllApi(guildId, MuteBody.Unmute)
                seconds > 0L -> MuteAllApi(guildId, MuteBody(muteSeconds = seconds.toString()))
                else -> throw IllegalStateException("mute seconds must >= 0, but $seconds")
            }
        }

    }
}
