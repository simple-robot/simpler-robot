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

package test

import io.ktor.http.*
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import love.forte.simbot.qguild.QQGuild
import love.forte.simbot.qguild.api.menu.ModifyCustomMenuApi
import love.forte.simbot.qguild.api.panel.CreateCommandPanelApi
import love.forte.simbot.qguild.api.panel.GetCommandPanelListApi.Factory.create
import love.forte.simbot.qguild.api.panel.ModifyCommandPanelTargetApi
import love.forte.simbot.qguild.model.menu.CustomMenu
import love.forte.simbot.qguild.model.panel.CommandPanel
import love.forte.simbot.qguild.model.panel.CommandPanelScope
import love.forte.simbot.qguild.model.panel.CommandPanelTargetTypeValues
import love.forte.simbot.qguild.model.panel.CommandPanelTargetUpdate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MenuPanelApiTests {
    @Test
    fun menuDslForwardsUnsupportedCombinationsWithoutLocalValidation() {
        val api = ModifyCustomMenuApi.create {
            item {
                name = "forwarded"
                type = "unknown"
                subMenuItem {
                    type = CustomMenu.SubItem.TYPE_LINK
                    link = "https://example.com"
                }
            }
        }
        val tree = QQGuild.DefaultJson.encodeToString(api.body).let(QQGuild.DefaultJson::parseToJsonElement).jsonObject
        val item = tree.getValue("menu").jsonObject.getValue("items").jsonArray.single().jsonObject
        assertEquals(HttpMethod.Put, api.method)
        assertEquals("/v2/menu", api.url.encodedPath)
        assertEquals(JsonPrimitive("unknown"), item["type"])
    }

    @Test
    fun commandPanelApisUseResourcesAndOmitEmptyTargetLists() {
        val list = create(CommandPanelScope.C2C, "cursor", 51)
        val create = CreateCommandPanelApi.create {
            scopeValue = CommandPanelScope.C2C
            targetType = CommandPanelTargetTypeValues.SPECIFIC
            addUserOpenid("user-openid")
            panel {
                item {
                    name = "/help"
                    type = CommandPanel.Item.TYPE_COMMAND
                }
            }
        }
        val target = ModifyCommandPanelTargetApi.create("panel-id") {
            op = CommandPanelTargetUpdate.OP_DEL
            clearGroupOpenids()
        }

        assertEquals(HttpMethod.Get, list.method)
        assertEquals("/v2/panels", list.url.encodedPath)
        assertEquals("51", list.url.parameters["limit"])
        assertEquals(HttpMethod.Post, create.method)
        assertEquals("/v2/panels", create.url.encodedPath)
        assertEquals(HttpMethod.Put, target.method)
        val targetTree = QQGuild.DefaultJson.encodeToString(
            target.body
        ).let(QQGuild.DefaultJson::parseToJsonElement).jsonObject
        assertNull(targetTree["group_openids"])
    }
}
