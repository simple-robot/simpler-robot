/*
 *     Copyright (c) 2022-2026. ForteScarlet.
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

package love.forte.simbot.component.kook

import love.forte.simbot.ability.DeleteOption
import love.forte.simbot.ability.DeleteSupport
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.component.kook.message.KookMessageCreatedReceipt
import love.forte.simbot.component.kook.message.KookMessageReceipt
import love.forte.simbot.definition.Contact
import love.forte.simbot.kook.api.userchat.DeleteUserChatApi
import love.forte.simbot.kook.api.userchat.TargetInfo
import love.forte.simbot.kook.api.userchat.UserChatView
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageContent
import love.forte.simbot.suspendrunner.ST
import kotlin.jvm.JvmSynthetic

/**
 * KOOK 的 [user-chat 私聊会话](https://developer.kaiheila.cn/doc/http/user-chat)。
 *
 * KOOK 组件会将 [私聊会话][KookUserChat] 视为 [Contact] 处理，同时实现 [Contact] 来提供可交流的联系人能力。
 *
 * ## 可删除的
 * KOOK 中的聊天会话是可以通过 [DeleteUserChatApi] 进行删除的。因此 [KookUserChat] 实现了 [DeleteSupport] 来支持 [删除操作][delete]。
 *
 * @author ForteScarlet
 */
public interface KookUserChat : Contact, DeleteSupport {
    /**
     * 私聊会话对应用户的用户ID，同 [source.targetInfo.id][TargetInfo.id] 。
     *
     * 如果需要此会话的会话 `code`，使用 [chatCode] 来获取。
     *
     * @see chatCode
     * @see TargetInfo.id
     */
    override val id: ID
        get() = source.targetInfo.id.ID

    /**
     * 私聊会话的会话 `code` , 同 [source.code][UserChatView.code] 。
     *
     * 如果需要此会话对应的目标用户id，使用 [id]。
     *
     * @see id
     * @see UserChatView.code
     */
    public val chatCode: String
        get() = source.code

    /**
     * 用户会话源信息对象。
     */
    public val source: UserChatView

    override val avatar: String get() = source.targetInfo.avatar
    override val name: String get() = source.targetInfo.username

    /**
     * 向当前好友（私聊会话）发送消息。
     */
    @ST
    override suspend fun send(message: Message): KookMessageReceipt

    /**
     * 向当前好友（私聊会话）发送消息。
     */
    @ST
    override suspend fun send(text: String): KookMessageCreatedReceipt

    /**
     * 向当前好友（私聊会话）发送消息。
     */
    @ST
    override suspend fun send(messageContent: MessageContent): KookMessageReceipt

    /**
     * 删除当前 [聊天会话][KookUserChat].
     *
     * 通过 [DeleteUserChatApi] 删除当前聊天会话。
     * 在删除的过程中会直接抛出 [DeleteUserChatApi] 在请求过程中可能产生的任何异常。
     *
     */
    @JvmSynthetic
    override suspend fun delete(vararg options: DeleteOption)
}
