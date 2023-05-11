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
import love.forte.simbot.action.SendSupport
import love.forte.simbot.bot.Bot


/**
 * [Objective] 是对与 [Bot] 相关联的对象 （一个[组织][Organization]或一个具体的[用户][User]） 的统称。
 *
 * 不论 [组织][Organization] 还是 [用户][User]，它们均来自一个 [Bot].
 *
 * [Objective] 本身仅代表这个对象的概念，不能保证其本身拥有 [发送消息][SendSupport] 的能力。
 *
 *
 *
 * @author ForteScarlet
 */
public sealed interface Objective : BotContainer, IDContainer {
    
    /**
     * 当前对象对应的唯一ID。
     *
     * @see ID
     */
    override val id: ID
    
    /**
     * 当前 [Objective] 来自的bot。
     */
    override val bot: Bot
}

// Actor?
