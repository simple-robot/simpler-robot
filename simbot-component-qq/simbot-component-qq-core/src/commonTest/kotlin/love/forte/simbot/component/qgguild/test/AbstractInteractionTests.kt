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

package love.forte.simbot.component.qgguild.test

import io.ktor.client.engine.mock.*
import io.ktor.client.request.*
import love.forte.simbot.application.listeners
import love.forte.simbot.component.qguild.bot.QGBot
import love.forte.simbot.component.qguild.firstQQGuildBotManager
import love.forte.simbot.component.qguild.internal.bot.QGBotImpl
import love.forte.simbot.component.qguild.useQQGuild
import love.forte.simbot.core.application.SimpleApplication
import love.forte.simbot.core.application.launchSimpleApplication
import love.forte.simbot.event.EventListenerContext
import love.forte.simbot.event.InternalMessageInteractionEvent
import love.forte.simbot.event.process

/**
 *
 * @author ForteScarlet
 */
abstract class AbstractInteractionTests {
    internal suspend inline fun app(
        crossinline mockClient: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData
    ): SimpleApplication {
        return launchSimpleApplication {
            useQQGuild {
                botManager {
                    botConfigure { _, _, _ ->
                        apiClientEngine = MockEngine {
                            mockClient(it)
                        }
                        disableWs = true
                    }
                }
            }
        }
    }

    internal fun SimpleApplication.bot(): QGBot {
        return botManagers
            .firstQQGuildBotManager()
            .register("test", "test", "test") {

            }
    }

    internal suspend inline fun <
            T,
            reified E : InternalMessageInteractionEvent
            > testInteractionEvent(
        crossinline mockClient: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData,
        prepare: suspend SimpleApplication.(QGBotImpl) -> T,
        crossinline listener: suspend EventListenerContext.(T, E, SimpleApplication, QGBotImpl) -> Unit,
        block: suspend SimpleApplication.(T, QGBotImpl) -> Unit,
    ) {
        val app = app(mockClient)
        val bot = app.bot() as QGBotImpl
        try {
            val t = app.prepare(bot)
            app.listeners {
                process<E> {
                    listener(t, it, app, bot)
                }
            }
            app.block(t, bot)
        } finally {
            app.cancel()
            bot.cancel()
        }
    }
}
