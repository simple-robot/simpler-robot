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
 * [私聊消息更新](https://developer.kookapp.cn/doc/event/direct-message#%E7%A7%81%E8%81%8A%E6%B6%88%E6%81%AF%E6%9B%B4%E6%96%B0)
 *
 * @author ForteScarlet
 */
@Serializable
@SerialName(UpdatedPrivateMessageEventExtra.TYPE)
public data class UpdatedPrivateMessageEventExtra(override val body: Body) : SystemExtra() {
    public companion object {
        public const val TYPE: String = "updated_private_message"
    }

    /**
     * [UpdatedPrivateMessageEventExtra] 事件的事件体
     * @see UpdatedPrivateMessageEventExtra.body
     */
    @Serializable
    public data class Body(
        /**
         * 被更新的消息的 id
         */
        @SerialName("msg_id") val msgId: String,
        /**
         * 被更新的消息的创建者 id
         */
        @SerialName("author_id") val authorId: String,
        /**
         * 被更新的消息的目标用户 id
         */
        @SerialName("target_id") val targetId: String,
        /**
         * 更新后的文本
         */
        val content: String,
        /**
         * 私聊 code
         */
        @SerialName("chat_code") val chatCode: String,
        /**
         * 更新时间戳(毫秒)
         */
        @SerialName("updated_at") val updatedAt: Long,
    )
}

/**
 * [私聊消息删除](https://developer.kookapp.cn/doc/event/direct-message#%E7%A7%81%E8%81%8A%E6%B6%88%E6%81%AF%E8%A2%AB%E5%88%A0%E9%99%A4)
 *
 * @author ForteScarlet
 */
@Serializable
@SerialName(DeletedPrivateMessageEventExtra.TYPE)
public data class DeletedPrivateMessageEventExtra(override val body: Body) : SystemExtra() {
    public companion object {
        public const val TYPE: String = "deleted_private_message"
    }

    /**
     * [DeletedPrivateMessageEventExtra] 事件的事件体
     * @see DeletedPrivateMessageEventExtra.body
     */
    @Serializable
    public data class Body(
        /**
         * 被删除的消息的 id
         */
        @SerialName("msg_id") val msgId: String,
        /**
         * 被删除的消息的创建者 id
         */
        @SerialName("author_id") val authorId: String,
        /**
         * 被删除的消息的目标用户 id
         */
        @SerialName("target_id") val targetId: String,
        /**
         * 私聊 code
         */
        @SerialName("chat_code") val chatCode: String,
        /**
         * 删除的时间戳(毫秒)
         */
        @SerialName("deleted_at") val deletedAt: Long,
    )
}


// TODO 私聊内用户添加 reaction etc.
// https://developer.kookapp.cn/doc/event/direct-message#%E7%A7%81%E8%81%8A%E5%86%85%E7%94%A8%E6%88%B7%E6%B7%BB%E5%8A%A0%20reaction
