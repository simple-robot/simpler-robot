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

import love.forte.simbot.ID
import love.forte.simbot.bot.Bot


/**
 * 陌生人。
 * 一个并非好友或群成员的人。
 *
 * 陌生人并不一定就是一个 [联系人][Contact], 无法保证能够与其能够交流/通信。
 *
 * @author ForteScarlet
 */
public interface Stranger : User {
    override val id: ID
    override val bot: Bot
}
