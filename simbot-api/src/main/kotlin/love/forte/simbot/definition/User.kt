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

@file:JvmName("AccountUtil")

package love.forte.simbot.definition

import love.forte.simbot.ID
import love.forte.simbot.bot.Bot

/**
 * 一个 **用户**。
 *
 * [Bot] 也是 [用户][User].
 *
 * 对于Bot来讲，一个用户可能是一个陌生的人，一个[群成员][Member], 或者一个好友。
 *
 * 当然，[User] 也有可能代表了 [Bot] 自身.
 *
 * @author ForteScarlet
 */
public interface User : Objective, UserInfo {
    override val id: ID
    override val bot: Bot
    override val username: String
    override val avatar: String
}
