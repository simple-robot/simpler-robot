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

package love.forte.simbot.qguild.stdlib

import kotlinx.serialization.Serializable

/**
 * QQ 机器人发送消息时的目的地。
 *
 * @since 4.5.0
 */
@Serializable
public enum class MessageDestination {
    /** QQ频道的文字子频道。 */
    CHANNEL,

    /** QQ频道的私信会话。 */
    DMS,

    /** QQ群。 */
    GROUP,

    /** QQ 用户的 C2C 私信会话。 */
    USER,
}
