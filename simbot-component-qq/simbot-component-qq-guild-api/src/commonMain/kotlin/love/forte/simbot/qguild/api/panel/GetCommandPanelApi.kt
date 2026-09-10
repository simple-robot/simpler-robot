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

package love.forte.simbot.qguild.api.panel

import kotlinx.serialization.DeserializationStrategy
import love.forte.simbot.qguild.api.GetQQGuildApi
import love.forte.simbot.qguild.api.SimpleGetApiDescription
import love.forte.simbot.qguild.model.panel.CommandPanelRecord
import kotlin.jvm.JvmStatic

/**
 * [获取指令面板详情](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/menu-panel/panel/get.html)。
 *
 * @since 5.0
 */
public class GetCommandPanelApi private constructor(
    panelId: String,
) : GetQQGuildApi<CommandPanelRecord>() {
    /**
     * [GetCommandPanelApi] 的描述与构建入口。
     */
    public companion object Factory : SimpleGetApiDescription("/v2/panels/{panel_id}") {
        /**
         * 使用 [panelId] 构建 [GetCommandPanelApi]。
         */
        @JvmStatic
        public fun create(panelId: String): GetCommandPanelApi = GetCommandPanelApi(panelId)
    }

    override val path: Array<String> = arrayOf("v2", "panels", panelId)

    override val resultDeserializationStrategy: DeserializationStrategy<CommandPanelRecord>
        get() = CommandPanelRecord.serializer()
}
