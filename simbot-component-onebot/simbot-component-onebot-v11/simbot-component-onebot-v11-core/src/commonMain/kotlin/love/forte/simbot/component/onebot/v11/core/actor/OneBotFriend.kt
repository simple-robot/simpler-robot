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

package love.forte.simbot.component.onebot.v11.core.actor

import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.literal
import love.forte.simbot.component.onebot.common.annotations.OneBotInternalImplementationsOnly
import love.forte.simbot.component.onebot.v11.common.utils.qqAvatar640
import love.forte.simbot.component.onebot.v11.core.bot.OneBotBot
import love.forte.simbot.component.onebot.v11.message.OneBotMessageReceipt
import love.forte.simbot.definition.Contact
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageContent
import love.forte.simbot.suspendrunner.ST
import kotlin.coroutines.CoroutineContext


/**
 * 一个用来代表 OneBot 中好友的类型。
 *
 * 好友通常的来源：
 * - 来自事件
 * - 来自 Bot relation API
 * ([OneBotBot.contactRelation][love.forte.simbot.component.onebot.v11.core.bot.OneBotBot.contactRelation])
 *
 * @author ForteScarlet
 */
@OneBotInternalImplementationsOnly
public interface OneBotFriend : Contact, SendLikeSupport, OneBotStrangerAware {
    /**
     * 协程上下文。源自 [OneBotBot], 但是不含 [Job][kotlinx.coroutines.Job]。
     */
    override val coroutineContext: CoroutineContext

    /**
     * 此人的ID
     */
    override val id: ID

    /**
     * 头像链接
     */
    override val avatar: String
        get() = qqAvatar640(id.literal)

    /**
     * 向此好友发送消息。
     *
     * @throws Throwable 任何请求API过程中可能产生的异常
     */
    @ST
    override suspend fun send(message: Message): OneBotMessageReceipt

    /**
     * 向此好友发送消息。
     *
     * @throws Throwable 任何请求API过程中可能产生的异常
     */
    @ST
    override suspend fun send(messageContent: MessageContent): OneBotMessageReceipt

    /**
     * 向此好友发送消息。
     *
     * @throws Throwable 任何请求API过程中可能产生的异常
     */
    @ST
    override suspend fun send(text: String): OneBotMessageReceipt
}
