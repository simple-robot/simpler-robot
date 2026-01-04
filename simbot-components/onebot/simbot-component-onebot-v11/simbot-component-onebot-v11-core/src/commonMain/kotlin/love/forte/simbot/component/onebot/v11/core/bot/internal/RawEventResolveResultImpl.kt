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

package love.forte.simbot.component.onebot.v11.core.bot.internal

import kotlinx.serialization.json.JsonObject
import love.forte.simbot.common.id.LongID
import love.forte.simbot.component.onebot.v11.core.event.ExperimentalCustomEventResolverApi
import love.forte.simbot.component.onebot.v11.core.event.RawEventResolveResult
import love.forte.simbot.component.onebot.v11.event.RawEvent

/**
 * 一个事件文本被进行解析后的主要内容。
 *
 * @author ForteScarlet
 */
@ExperimentalCustomEventResolverApi
internal data class RawEventResolveResultImpl(
    override val text: String,
    override val json: JsonObject,
    override val postType: String,
    override val subType: String?,
    override val time: Long?,
    override val selfId: LongID?,
    override val rawEvent: RawEvent?,
    override val reason: Throwable?
) : RawEventResolveResult
