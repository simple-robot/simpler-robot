/*
 *     Copyright (c) 2022-2026. ForteScarlet.
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

package test

import kotlinx.serialization.json.Json
import love.forte.simbot.qguild.api.message.ArkMessageTemplates
import love.forte.simbot.qguild.model.Message
import kotlin.test.Test

private val json = Json { prettyPrint = true }

/**
 *
 * @author ForteScarlet
 */
class MessageTemplateTest {

    @Test
    fun test() {
        val t23 = ArkMessageTemplates.TextLinkList(
            desc = "desc",
            prompt = "prompt",
            list = listOf(
                ArkMessageTemplates.TextLinkList.Desc("DESC"),
                ArkMessageTemplates.TextLinkList.Desc("DESC", "https://sss")
            )
        )
        println(json.encodeToString(Message.Ark.serializer(), t23.ark))


    }
}
