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

package love.forte.simbot.component.kook.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.common.id.ID
import love.forte.simbot.component.kook.event.KookChannelMessageEvent
import love.forte.simbot.component.kook.message.KookTempTarget.Companion.byId
import love.forte.simbot.component.kook.message.KookTempTarget.Companion.current
import love.forte.simbot.kook.api.message.SendChannelMessageApi
import kotlin.jvm.JvmStatic

/**
 *
 * 频道聊天消息中的**临时消息ID**。
 *
 * > `temp_target_id`: 用户 id,如果传了，代表该消息是临时消息，该消息不会存数据库，但是会在频道内只给该用户推送临时消息。用于在频道内针对用户的操作进行单独的回应通知等。
 *
 * 当消息链中存在时，会在最终产生的消息发送API中为 [SendChannelMessageApi.tempTargetId] 填充用作临时消息ID的用户ID。
 *
 * 一个消息链中只会有**一个** [KookTempTarget] 最终生效，非频道消息无效。
 *
 * _更多说明参考 [KOOK文档](https://developer.kookapp.cn/doc/http/message#%E5%8F%91%E9%80%81%E9%A2%91%E9%81%93%E8%81%8A%E5%A4%A9%E6%B6%88%E6%81%AF)_
 *
 * @see SendChannelMessageApi
 * @see byId
 * @see current
 *
 * @author ForteScarlet
 */
@Serializable
@SerialName("kook.temp.target")
public sealed class KookTempTarget : KookMessageElement {

    public companion object {
        /**
         * 指定一个具体的ID目标作为临时消息的目标用户ID。
         */
        @JvmStatic
        public fun byId(id: ID): KookTempTarget = Target(id)

        /**
         * 使用“当前”事件中的用户ID。
         *
         * 当通过 [KookChannelMessageEvent.reply] 时，通过 [current] 可以自动填充当前事件中的用户目标ID。
         * 其他情况下 [current] 将会被忽略。
         *
         * 如果希望指定具体的用户ID，参考 [byId]。
         */
        @JvmStatic
        public fun current(): KookTempTarget = Current
    }


    /**
     * 指定具体的用户ID
     */
    @Serializable
    @SerialName("kook.temp.target.std")
    internal data class Target(val id: ID) : KookTempTarget()


    /**
     * 尝试自动使用可获取到的目标用户ID
     */
    @Serializable
    @SerialName("kook.temp.target.curr")
    internal object Current : KookTempTarget() {
        override fun equals(other: Any?): Boolean = this === other
        override fun hashCode(): Int = super.hashCode()
        override fun toString(): String = "Current"
    }
}
