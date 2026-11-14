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

package love.forte.simbot.component.kook.message

import love.forte.simbot.event.InteractionMessage
import love.forte.simbot.kook.api.message.SendChannelMessageApi

/**
 * 频道原生发送请求。拦截器可保留该实例或使用另一个请求替换它。
 *
 * [KookChannelMessageSendOption] 可覆盖请求中的对应参数，替换请求可以改变实际发送目标。
 * 替换为标准消息时使用上下文的目标和选项。
 */
public class KookChannelRequestMessage(
    /**
     * 完整的原生请求，没有适用的选项覆盖时会直接用于执行。
     */
    public val request: SendChannelMessageApi,
) : InteractionMessage.Extension()

/**
 * KOOK 消息发送目标。
 *
 * 根据具体类型区分频道、用户和私聊会话，无需请求平台。
 */
public sealed class KookMessageSendTarget private constructor() {
    /**
     * 频道 ID、用户 ID 或私聊会话 code。
     */
    public abstract val id: String

    /**
     * 频道 ID目标。
     */
    public class Channel internal constructor(override val id: String) : KookMessageSendTarget() {
        override fun equals(other: Any?): Boolean =
            this === other || other is Channel && id == other.id

        override fun hashCode(): Int = id.hashCode()

        override fun toString(): String = "Channel(id=$id)"
    }

    /**
     * 用户 ID目标。
     */
    public class User internal constructor(override val id: String) : KookMessageSendTarget() {
        override fun equals(other: Any?): Boolean =
            this === other || other is User && id == other.id

        override fun hashCode(): Int = id.hashCode()

        override fun toString(): String = "User(id=$id)"
    }

    /**
     * 私聊会话 code目标。
     */
    public class ChatCode internal constructor(override val id: String) : KookMessageSendTarget() {
        override fun equals(other: Any?): Boolean =
            this === other || other is ChatCode && id == other.id

        override fun hashCode(): Int = id.hashCode()

        override fun toString(): String = "ChatCode(id=$id)"
    }

    companion {
        /**
         * 创建频道 ID 目标。
         */
        public fun channel(id: String): Channel = Channel(id)

        /**
         * 创建用户 ID 目标。
         */
        public fun user(id: String): User = User(id)

        /**
         * 创建私聊会话 code 目标。
         */
        public fun chatCode(id: String): ChatCode = ChatCode(id)
    }
}

/**
 * 保留请求参数，并应用发送选项中已指定的值。
 */
internal fun SendChannelMessageApi.withOptions(
    quote: String?,
    nonce: String?,
    tempTargetId: String?,
    templateId: String?,
): SendChannelMessageApi {
    if (quote == null && nonce == null && tempTargetId == null && templateId == null) {
        return this
    }
    return SendChannelMessageApi.create(
        type = type,
        targetId = targetId,
        content = content,
        quote = quote ?: this.quote,
        nonce = nonce ?: this.nonce,
        tempTargetId = tempTargetId ?: this.tempTargetId,
        templateId = templateId ?: this.templateId,
    )
}
