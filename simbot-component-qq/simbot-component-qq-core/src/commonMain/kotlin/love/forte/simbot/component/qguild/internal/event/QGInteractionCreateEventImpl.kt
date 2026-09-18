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

package love.forte.simbot.component.qguild.internal.event

import love.forte.simbot.component.qguild.event.QGInteractionCreateEvent
import love.forte.simbot.component.qguild.internal.bot.QGBotImpl
import love.forte.simbot.qguild.api.interaction.InteractionResponseApi
import love.forte.simbot.qguild.event.InteractionCreate
import love.forte.simbot.qguild.stdlib.requestDataBy

internal class QGInteractionCreateEventImpl(
    override val bot: QGBotImpl,
    override val sourceEventRaw: String,
    override val sourceEventEntity: InteractionCreate,
) : QGInteractionCreateEvent() {
    override suspend fun respond(code: Int) {
        InteractionResponseApi.create(sourceEventEntity.data.id, code).requestDataBy(bot.source)
    }
}
