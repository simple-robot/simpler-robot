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

package love.forte.simbot.kook.event

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * [SelfJoinedGuildEventExtra]、[SelfExitedGuildEventExtra] 事件体。
 */
@Serializable
public data class BotSelfGuildEventBody(@SerialName("guild_id") val guildId: String)

/**
 * [自己新加入服务器](https://developer.kookapp.cn/doc/event/user#%E8%87%AA%E5%B7%B1%E6%96%B0%E5%8A%A0%E5%85%A5%E6%9C%8D%E5%8A%A1%E5%99%A8)
 *
 * > 当自己被邀请或主动加入新的服务器时, 产生该事件
 *
 * @author ForteScarlet
 */
@Serializable
@SerialName(SelfJoinedGuildEventExtra.TYPE)
public data class SelfJoinedGuildEventExtra(override val body: BotSelfGuildEventBody) : SystemExtra() {
    public companion object {
        public const val TYPE: String = "self_joined_guild"
    }
}

/**
 * [自己退出服务器](https://developer.kookapp.cn/doc/event/user#%E8%87%AA%E5%B7%B1%E9%80%80%E5%87%BA%E6%9C%8D%E5%8A%A1%E5%99%A8)
 *
 * > 当自己被踢出服务器或被拉黑或主动退出服务器时, 产生该事件
 *
 * @author ForteScarlet
 */
@Serializable
@SerialName(SelfExitedGuildEventExtra.TYPE)
public data class SelfExitedGuildEventExtra(override val body: BotSelfGuildEventBody) : SystemExtra() {
    public companion object {
        public const val TYPE: String = "self_exited_guild"
    }
}
