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

package love.forte.simbot.component.onebot.v11.core.event

import kotlinx.serialization.DeserializationStrategy
import love.forte.simbot.event.Event

/**
 * 基于 Kotlin Serialization 反序列化器的 [CustomEventResolver]。
 * @since 1.8.0
 * @author ForteScarlet
 */
@ExperimentalCustomEventResolverApi
public fun interface CustomKotlinSerializationEventResolver : CustomEventResolver {

    /**
     * 根据 [context] 得到一个 [DeserializationStrategy]。
     */
    public fun serializer(context: CustomEventResolver.Context): DeserializationStrategy<Event>?

    override fun resolve(context: CustomEventResolver.Context): Event? {
        return serializer(context)?.let {
            return context.json.decodeFromJsonElement(it, context.rawEventResolveResult.json)
        }
    }
}
