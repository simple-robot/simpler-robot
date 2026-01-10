/*
 * Copyright (c) 2022-2023. ForteScarlet.
 *
 * This file is part of simbot-component-tencent-guild.
 *
 * simbot-component-tencent-guild is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-tencent-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-tencent-guild. If not, see <https://www.gnu.org/licenses/>.
 */

package test

import kotlinx.serialization.json.Json
import love.forte.simbot.qguild.event.Intents
import love.forte.simbot.qguild.event.Shard
import love.forte.simbot.qguild.event.Signal
import kotlin.test.Test


/**
 *
 * @author ForteScarlet
 */
class RangeSerializerTest {

    private val json = Json

    @Test
    fun test() {

        val id = Signal.Identify(
            Signal.Identify.Data(
                token = "token",
                intents = Intents(153) + Intents(224) + Intents(810),
                shard = Shard(0..4),
                properties = mapOf(
                    "os" to "windows",
                    "browser" to "chrome",
                    "device" to "abc"
                )
            )
        )

        println(id)
        val idJson = json.encodeToString(Signal.Identify.serializer(), id)
        println(idJson)
        println(json.decodeFromString(Signal.Identify.serializer(), idJson))

    }


}
