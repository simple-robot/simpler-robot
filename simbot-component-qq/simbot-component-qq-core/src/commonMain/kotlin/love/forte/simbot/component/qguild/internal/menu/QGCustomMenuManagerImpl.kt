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

package love.forte.simbot.component.qguild.internal.menu

import love.forte.simbot.component.qguild.ExperimentalQGApi
import love.forte.simbot.component.qguild.bot.QGBot
import love.forte.simbot.component.qguild.menu.InternalForInheritanceQGMenuApi
import love.forte.simbot.component.qguild.menu.QGCustomMenuManager
import love.forte.simbot.component.qguild.menu.QGCustomMenuSnapshot
import love.forte.simbot.component.qguild.menu.QGCustomMenuUpdateReceipt
import love.forte.simbot.qguild.api.menu.GetCustomMenuApi
import love.forte.simbot.qguild.api.menu.ModifyCustomMenuApi
import love.forte.simbot.qguild.model.menu.CustomMenu

@OptIn(ExperimentalQGApi::class, InternalForInheritanceQGMenuApi::class)
internal class QGCustomMenuManagerImpl(private val bot: QGBot) : QGCustomMenuManager {
    override suspend fun get(): QGCustomMenuSnapshot {
        val source = bot.executeData(GetCustomMenuApi.create())
        return QGCustomMenuSnapshot(source = source)
    }

    override suspend fun update(menu: CustomMenu): QGCustomMenuUpdateReceipt {
        val source = bot.executeData(ModifyCustomMenuApi.create(menu))
        return QGCustomMenuUpdateReceipt(source = source)
    }
}
