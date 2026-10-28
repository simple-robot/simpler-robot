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

import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.test.runTest
import love.forte.simbot.qguild.event.GuildCreate
import love.forte.simbot.qguild.stdlib.Bot
import love.forte.simbot.qguild.stdlib.BotFactory
import love.forte.simbot.qguild.stdlib.ConfigurableBotConfiguration
import love.forte.simbot.qguild.stdlib.internal.BotImpl
import love.forte.simbot.qguild.stdlib.subscribe
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 *
 * @author ForteScarlet
 */
class BotSubscribeRegisterTest {

    /**
     * 确保普通的 subscribe 和同名扩展函数
     * 不会产生冲突。
     */
    @Test
    fun subscribeTest() {
        val bot = BotImpl(
            Bot.Ticket("", "", ""),
            ConfigurableBotConfiguration().apply {
                apiClientEngine = MockEngine { respondOk() }
                wsClientEngine = MockEngine { respondOk() }
            }
        )

        bot.subscribe {
        }

        bot.subscribe<GuildCreate> {
        }
    }

    @Test
    fun apiClientAdditionalConfigurationIsAppliedWhenBotIsCreated() = runTest {
        var requestCount = 0
        val bot = BotFactory.create(
            Bot.Ticket("", "", ""),
            ConfigurableBotConfiguration().apply {
                wsClientEngine = MockEngine { respondOk() }
                apiClientEngine = MockEngine {
                    assertEquals("enabled", it.headers["X-Test-Configuration"])
                    requestCount++
                    respond(
                        content = "",
                        status = if (requestCount == 1) HttpStatusCode.InternalServerError else HttpStatusCode.OK,
                    )
                }
                apiClientAdditionalConfiguration {
                    defaultRequest {
                        headers.append("X-Test-Configuration", "enabled")
                    }
                }
                apiClientAdditionalConfiguration {
                    install(HttpRequestRetry) {
                        maxRetries = 1
                        retryOnServerErrors()
                    }
                }
            }
        )

        try {
            assertEquals(HttpStatusCode.OK, bot.apiClient.get("https://example.test/retry").status)
            assertEquals(2, requestCount)
        } finally {
            bot.apiClient.close()
            bot.cancel()
        }
    }

}
