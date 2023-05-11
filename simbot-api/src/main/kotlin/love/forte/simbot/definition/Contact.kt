/*
 * Copyright (c) 2021-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.definition

import love.forte.simbot.action.SendSupport
import love.forte.simbot.bot.Bot
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageReceipt


/**
 *
 * 一个联系目标。联系人是除 [Bot] 以外的可以进行信息交流的 [User], 例如 [好友][Friend], 或者一个[群成员][Member]。
 *
 * 虽然 [Contact] 实现了 [SendSupport], 但是并不是 [Bot] 以外的所有人都一定可以进行信息交流，比如对于一个群成员，可能会受限于权限，或者受限于组织类型（例如一个订阅型组织，参考tg的"频道"）.
 *
 *
 * @author ForteScarlet
 */
public interface Contact : User, SendSupport, BotContainer {
    
    /**
     * 联系人所属的 [Bot].
     */
    override val bot: Bot
    
    
    /**
     * 向此联系目标发送消息。
     *
     * @throws love.forte.simbot.action.UnsupportedActionException 当此对象不支持发送消息时
     */
    @JvmSynthetic
    override suspend fun send(message: Message): MessageReceipt
}


