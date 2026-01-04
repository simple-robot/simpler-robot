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
import kotlinx.serialization.json.Json
import love.forte.simbot.component.onebot.v11.core.OneBot11
import love.forte.simbot.component.onebot.v11.core.api.*

/**
 *
 * @author ForteScarlet
 */
@OptIn(ExperimentalCustomOneBotApi::class)
internal class CustomOneBotApiBuilderImpl<T : Any>(
    override val action: String,
    override val method: HttpMethod,
) : CustomOneBotApiBuilder<T> {
    override var body: Any? = null
        set(value) {
            require(method != HttpMethod.Get) { "Cannot set body for GET request." }
            field = value
        }

    override var deserializer: DynamicOneBotApiDeserializer<T>? = null
    private var apiResultDeserializer: DeserializationStrategy<OneBotApiResult<T>>? = null

    override fun deserializer(
        deserializationStrategy: DeserializationStrategy<OneBotApiResult<T>>
    ): CustomOneBotApiBuilder<T> = apply {
        apiResultDeserializer = deserializationStrategy
        deserializer = DynamicOneBotApiKotlinxDeserializer(deserializationStrategy)
    }

    override fun build(): CustomOneBotApi<T> {
        val deserializer = requireNotNull(this.deserializer) { "Deserializer must be not null." }
        val apiResultDeserializer = this.apiResultDeserializer

        return CustomOneBotApiImpl(
            deserialize = deserializer,
            action = action,
            body = body,
            method = method,
            apiResultDeserializer = apiResultDeserializer
        )
    }
}

@OptIn(ExperimentalCustomOneBotApi::class)
private class DynamicOneBotApiKotlinxDeserializer<T : Any>(
    val deserializationStrategy: DeserializationStrategy<OneBotApiResult<T>>,
    val decoder: Json = OneBot11.DefaultJson,
) : DynamicOneBotApiDeserializer<T> {
    override fun deserialize(raw: String): OneBotApiResult<T> =
        decoder.decodeFromString(deserializationStrategy, raw)
}
