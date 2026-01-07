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

package love.forte.simbot.component.onebot.v11.core

import io.ktor.client.engine.java.*
import io.ktor.http.*
import love.forte.simbot.application.listeners
import love.forte.simbot.bot.get
import love.forte.simbot.component.onebot.v11.core.bot.OneBotBotConfiguration
import love.forte.simbot.component.onebot.v11.core.bot.OneBotBotManager
import love.forte.simbot.component.onebot.v11.core.component.OneBot11Component
import love.forte.simbot.component.onebot.v11.core.event.message.OneBotPrivateMessageEvent
import love.forte.simbot.core.application.launchSimpleApplication
import love.forte.simbot.event.process

suspend fun main() {
    val app = launchSimpleApplication {
        install(OneBot11Component)
        install(OneBotBotManager)
    }

    app.listeners {
        process<OneBotPrivateMessageEvent> { event ->
            println("Event: $event")
            println("Event.plainText: ${event.messageContent.plainText}")
            println("Event.messages: ${event.messageContent.messages}")

            event.reply("喵")
        }

    }

    app.botManagers.get<OneBotBotManager>().apply {
        val bot = register(
            OneBotBotConfiguration().apply {
                botUniqueId = "2240189254"
                apiServerHost = Url("http://localhost:3000")
                eventServerHost = Url("ws://localhost:3001")
                wsClientEngineFactory = Java
                apiClientEngineFactory = Java
                accessToken("test")
            }
        )

        bot.start()
    }

    app.join()
}
