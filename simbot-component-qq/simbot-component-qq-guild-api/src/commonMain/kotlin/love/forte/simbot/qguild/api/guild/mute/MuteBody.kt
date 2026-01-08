/*
 * Copyright (c) 2023. ForteScarlet.
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

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 *
 * [MuteAllApi] 和 [MuteMemberApi] 的请求参数体。
 *
 * @author ForteScarlet
 */
@Serializable
internal data class MuteBody(
    /**
     * 禁言到期时间戳，绝对时间戳，单位：秒（与 mute_seconds 字段同时赋值的话，以该字段为准）
     */
    @SerialName("mute_end_timestamp") val muteEndTimestamp: String? = null,

    /**
     * 禁言多少秒（两个字段二选一，默认以 mute_end_timestamp 为准）
     */
    @SerialName("mute_seconds") val muteSeconds: String? = null
) {
    companion object {
        val Unmute = MuteBody(muteSeconds = "0")
    }
}
