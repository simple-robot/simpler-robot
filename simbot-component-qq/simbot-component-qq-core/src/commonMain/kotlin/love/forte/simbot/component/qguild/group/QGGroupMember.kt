/*
 *     Copyright (c) 2024-2026. ForteScarlet.
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

package love.forte.simbot.component.qguild.group

import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.common.time.Timestamp
import love.forte.simbot.common.time.Timestamp.Companion.toTimestamp
import love.forte.simbot.definition.Member
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageContent
import love.forte.simbot.message.MessageReceipt
import love.forte.simbot.qguild.common.QGInternalInheritanceApi
import love.forte.simbot.qguild.model.group.GroupBotReceiveMessageSetting
import love.forte.simbot.qguild.model.group.GroupBotState
import kotlin.jvm.JvmExposeBoxed
import kotlin.jvm.JvmSynthetic

/**
 * QQ 群内成员的基础信息。
 */
@SubclassOptInRequired(QGInternalInheritanceApi::class)
public interface QGGroupMember : Member {
    /**
     * 此成员在 [groupId] 所属群中的成员 OpenID。
     */
    override val id: ID

    /**
     * 此成员所属群的 OpenID。
     *
     * @since 5.0
     */
    public val groupId: ID

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

/**
 * 机器人在指定 QQ 群内作为成员的表现，包含一次查询得到的 [botState] 信息。
 *
 * @since 5.0
 */
@OptIn(ExperimentalStdlibApi::class)
@JvmExposeBoxed
public abstract class QGGroupBotMember internal constructor() : QGGroupMember {
    /**
     * 机器人在 [groupId] 所属群内的原始状态。
     */
    public abstract val botState: GroupBotState

    /**
     * 机器人在群内作为的成员 OpenID。
     */
    override val id: ID
        get() = botState.memberOpenid.ID

    /**
     * 机器人在群内的角色。
     */
    public val memberRole: QGGroupRole
        get() = botState.memberRole.toQGGroupRole()

    /**
     * 机器人加入此群的时间。
     */
    public val joinTime: Timestamp
        get() = botState.joinedAt.toTimestamp()

    /**
     * 机器人是否接收主动推送。
     */
    public val allowProactiveMsg: Boolean
        get() = botState.allowProactiveMsg

    /**
     * 机器人的消息接收设置。
     */
    public val recvMsgSetting: GroupBotReceiveMessageSetting
        get() = botState.recvMsgSetting
}

/**
 * 一个通过群消息事件得到的群成员信息。
 *
 * @since 4.4.0
 */
@SubclassOptInRequired(QGInternalInheritanceApi::class)
public interface QGGroupAuthor : QGGroupMember {
    /**
     * 消息发送者在群内的身份
     */
    public val memberRole: QGGroupRole

    /**
     * 是否为机器人
     */
    public val isBot: Boolean
}

private fun sendToMemberIsUnsupported(id: ID): Nothing =
    throw UnsupportedOperationException("Send message to QGGroupMember(id=$id) is unsupported")
