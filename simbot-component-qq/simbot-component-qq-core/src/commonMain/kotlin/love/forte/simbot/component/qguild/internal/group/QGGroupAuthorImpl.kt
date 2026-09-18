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

package love.forte.simbot.component.qguild.internal.group

import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.component.qguild.group.QGGroupAuthor
import love.forte.simbot.component.qguild.group.QGGroupRole
import love.forte.simbot.component.qguild.internal.bot.QGBotImpl
import love.forte.simbot.component.qguild.internal.bot.newSupervisorCoroutineContext
import love.forte.simbot.qguild.event.GroupMessageAuthor
import love.forte.simbot.qguild.event.GroupMessageAuthorRole
import kotlin.coroutines.CoroutineContext

internal class QGGroupAuthorImpl(
    bot: QGBotImpl,
    private val authorData: GroupMessageAuthor,
) : QGGroupAuthor {
    override val id: ID = authorData.memberOpenid.ID
    override val coroutineContext: CoroutineContext = bot.newSupervisorCoroutineContext()

    override val isBot: Boolean
        get() = authorData.bot

    override val memberRole: QGGroupRole
        get() = when (authorData.memberRole) {
            GroupMessageAuthorRole.OWNER -> QGGroupRole.OWNER
            GroupMessageAuthorRole.ADMIN -> QGGroupRole.ADMIN
            GroupMessageAuthorRole.MEMBER -> QGGroupRole.MEMBER
        }
}
