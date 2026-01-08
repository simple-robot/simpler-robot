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
