/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library.
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

import kotlinx.serialization.json.Json
import love.forte.simbot.bot.configuration.DispatcherConfiguration
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

/**
 *
 * @author ForteScarlet
 */
class DispatcherConfigurationTests {

    @Test
    fun demoteTest() {
        val jsonStr = """
            {
                "type": "j21_virtual",
                "demote": {
                    "type": "custom",
                    "coreThreads": 4,
                    "demote": {
                        "type": "io",
                        "demote": {
                            "type": "custom",
                            "coreThreads": 8,
                            "demote": {
                                "type": "default"
                            }
                        }
                    }
                }
            }
        """.trimIndent()

        // virtual -> custom -> io -> custom -> default

        val json = Json { isLenient = true; ignoreUnknownKeys = true }

        val config = json.decodeFromString(DispatcherConfiguration.serializer(), jsonStr)

        assertIs<DispatcherConfiguration.Virtual>(config)
        val virtualDemote = config.demote
        assertIs<DispatcherConfiguration.Custom>(virtualDemote)
        val customDemote = virtualDemote.demote
        assertIs<DispatcherConfiguration.IO>(customDemote)
        val ioDemote = customDemote.demote
        assertIs<DispatcherConfiguration.Custom>(ioDemote)
        val custom1Demote = ioDemote.demote
        assertIs<DispatcherConfiguration.Default>(custom1Demote)

        val config0 = DispatcherConfiguration.Virtual(
            demote = DispatcherConfiguration.Custom(
                coreThreads = 4,
                demote = DispatcherConfiguration.IO(
                    DispatcherConfiguration.Custom(
                        coreThreads = 8,
                        demote = DispatcherConfiguration.Default
                    )
                )
            )
        )

        assertEquals(config0, config)

        println(config)
    }

}
