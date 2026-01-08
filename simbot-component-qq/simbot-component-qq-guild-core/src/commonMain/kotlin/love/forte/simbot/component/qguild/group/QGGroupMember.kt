/*
 * Copyright (c) 2024. ForteScarlet.
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

package love.forte.simbot.component.qguild.group

import love.forte.simbot.common.id.ID
import love.forte.simbot.definition.Member
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageContent
import love.forte.simbot.message.MessageReceipt
import kotlin.jvm.JvmSynthetic

/**
 * 一个通过群消息事件得到的群成员信息。
 * 只能得到它的 openid。
 */
public interface QGGroupMember : Member {
    /**
     * 此成员的openid
     */
    override val id: ID

    /**
     * 无法得知对方的用户名，始终得到空字符串。
     */
    override val name: String
        get() = ""

    /**
     * 无法得知昵称，始终得到 `null`。
     */
    override val nick: String?
        get() = null

    /**
     * 无法得知头像，始终得到 `null`。
     */
    override val avatar: String?
        get() = null

    /**
     * 不支持主动向群成员发送消息，将会抛出 [UnsupportedOperationException]
     *
     * @throws UnsupportedOperationException
     */
    @JvmSynthetic
    override suspend fun send(message: Message): MessageReceipt {
        sendToMemberIsUnsupported(id)
    }

    /**
     * 不支持主动向群成员发送消息，将会抛出 [UnsupportedOperationException]
     *
     * @throws UnsupportedOperationException
     */
    @JvmSynthetic
    override suspend fun send(messageContent: MessageContent): MessageReceipt {
        sendToMemberIsUnsupported(id)
    }

    /**
     * 不支持主动向群成员发送消息，将会抛出 [UnsupportedOperationException]
     *
     * @throws UnsupportedOperationException
     */
    @JvmSynthetic
    override suspend fun send(text: String): MessageReceipt {
        sendToMemberIsUnsupported(id)
    }
}

private fun sendToMemberIsUnsupported(id: ID): Nothing =
    throw UnsupportedOperationException("Send message to QGGroupMember(id=$id)")
