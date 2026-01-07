/*
 *     Copyright (c) 2025-2026. ForteScarlet.
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

package love.forte.simbot.component.onebot.v11.core.internal.api

import io.ktor.http.*
import kotlinx.serialization.DeserializationStrategy
import love.forte.simbot.component.onebot.v11.core.api.CustomOneBotApi
import love.forte.simbot.component.onebot.v11.core.api.DynamicOneBotApiDeserializer
import love.forte.simbot.component.onebot.v11.core.api.ExperimentalCustomOneBotApi
import love.forte.simbot.component.onebot.v11.core.api.OneBotApiResult

/**
 * @author ForteScarlet
 */
@OptIn(ExperimentalCustomOneBotApi::class)
internal class CustomOneBotApiImpl<T : Any>(
    private val deserialize: DynamicOneBotApiDeserializer<T>,
    override val action: String,
    override val method: HttpMethod,
    override val body: Any?,
    override val apiResultDeserializer: DeserializationStrategy<OneBotApiResult<T>>?,
) : CustomOneBotApi<T> {
    override fun deserialize(responseBody: String): OneBotApiResult<T> = deserialize.deserialize(responseBody)

    override fun toString(): String {
        return "DynamicOneBotApi(action='$action', body=$body)"
    }
}
