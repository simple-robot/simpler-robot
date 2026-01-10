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
import love.forte.simbot.kook.objects.SimpleUser

/**
 * [MessageBtnClickEventExtra] 事件体。
 */
@Serializable
public data class MessageBtnClickEventBody(
    /**
     * 用户点击的消息 id
     */
    @SerialName("msg_id") val msgId: String,
    /**
     * 点击的用户
     */
    @SerialName("user_id") val userId: String,
    /**
     * return-val的值
     */
    val value: String,
    /**
     * 消息发送的目标 id,频道消息为频道
     */
    @SerialName("target_id") val targetId: String,
    /**
     * 用户信息，同 User Object
     */
    @SerialName("user_info") val userInfo: SimpleUser,
)

/**
 * [Card 消息中的 Button 点击事件](https://developer.kookapp.cn/doc/event/user#Card%20%E6%B6%88%E6%81%AF%E4%B8%AD%E7%9A%84%20Button%20%E7%82%B9%E5%87%BB%E4%BA%8B%E4%BB%B6)
 *
 * @author ForteScarlet
 */
@Serializable
@SerialName(MessageBtnClickEventExtra.TYPE)
public data class MessageBtnClickEventExtra(override val body: MessageBtnClickEventBody) : SystemExtra() {
    public companion object {
        public const val TYPE: String = "message_btn_click"
    }
}
