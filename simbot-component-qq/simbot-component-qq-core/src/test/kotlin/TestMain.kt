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

import love.forte.simbot.component.qguild.event.QGAtMessageCreateEvent
import love.forte.simbot.component.qguild.qqGuildBots
import love.forte.simbot.component.qguild.useQQGuild
import love.forte.simbot.core.application.createSimpleApplication
import love.forte.simbot.core.event.listeners
import love.forte.simbot.message.Image.Key.toImage
import love.forte.simbot.message.Text
import love.forte.simbot.message.plus
import love.forte.simbot.resources.Resource.Companion.toResource
import kotlin.io.path.Path

suspend fun main() {
    val app = createSimpleApplication {
        useQQGuild()
    }

    app.eventListenerManager.listeners {
        QGAtMessageCreateEvent { event ->
            event.reply(Text { "1:hi\nhello!" })
            event.reply(Text { "2:hi\nhello!" } +
                    Path("/Users/forte/Desktop/DT/6C0E80A3247FF69F8F99D55C122A1181.jpg").toResource().toImage())
            event.reply(Path("/Users/forte/Desktop/DT/6C0E80A3247FF69F8F99D55C122A1181.jpg").toResource().toImage())

        }
    }

    app.qqGuildBots {
        register("101986850", "972f64f7c426096f9344b74ba85102fb", "g57N4WsHHRIx1udptqy7GBAEVsfLgynq") {
            botConfig {
                useSandboxServerUrl()
            }
        }.start()
    }


    app.join()
}
