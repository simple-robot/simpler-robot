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

package love.forte.simbot.component.onebot.v11.core.api

import io.ktor.http.*
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import love.forte.simbot.bot.SerializableBotConfiguration
import love.forte.simbot.component.onebot.v11.core.bot.OneBotBotSerializableConfiguration
import love.forte.simbot.component.onebot.v11.core.useOneBot11
import love.forte.simbot.core.application.launchSimpleApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs


/**
 *
 * @author ForteScarlet
 */
class ApplicationSerializableConfigurationTests {


    @Test
    fun serializableConfigurationTest() = runTest {
        val app = launchSimpleApplication { useOneBot11() }

        val json = Json {
            isLenient = true
            ignoreUnknownKeys = true
            serializersModule = app.components.serializersModule
        }

        val configuration = json.decodeFromString(
            SerializableBotConfiguration.serializer(),
            """{
              "component": "simbot.onebot11",
              "authorization": {
                "botUniqueId": "123456",
                "apiServerHost": "http://localhost:8080",
                "eventServerHost": "ws://localhost:9090"
              },
              "config": {
                "apiHttpRequestTimeoutMillis": 99999,
                "apiHttpConnectTimeoutMillis": 99999,
                "apiHttpSocketTimeoutMillis": 99999,
                "wsConnectMaxRetryTimes": 99999,
                "wsConnectRetryDelayMillis": 99999
              }
            }"""
        )

        assertIs<OneBotBotSerializableConfiguration>(configuration)

        val conf = configuration.toConfiguration()
        assertEquals("123456", conf.botUniqueId)
        assertEquals(Url("http://localhost:8080"), conf.apiServerHost)
        assertEquals(Url("ws://localhost:9090"), conf.eventServerHost)
        assertEquals(99999, conf.apiHttpRequestTimeoutMillis)
        assertEquals(99999, conf.apiHttpConnectTimeoutMillis)
        assertEquals(99999, conf.apiHttpSocketTimeoutMillis)
        assertEquals(99999, conf.wsConnectMaxRetryTimes)
        assertEquals(99999, conf.wsConnectRetryDelayMillis)

    }

}
