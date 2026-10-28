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

import love.forte.simbot.qguild.api.DeleteQQGuildApi
import love.forte.simbot.qguild.api.QQGuildApiWithoutResult
import love.forte.simbot.qguild.api.SimpleDeleteApiDescription
import kotlin.jvm.JvmStatic

/**
 * [删除指令面板](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/menu-panel/panel/delete.html)。
 *
 * @since 5.0
 */
public class DeleteCommandPanelApi private constructor(
    panelId: String,
) : QQGuildApiWithoutResult, DeleteQQGuildApi<Unit>() {
    /**
     * [DeleteCommandPanelApi] 的描述与构建入口。
     */
    public companion object Factory : SimpleDeleteApiDescription("/v2/panels/{panel_id}") {
        /**
         * 使用 [panelId] 构建 [DeleteCommandPanelApi]。
         */
        @JvmStatic
        public fun create(panelId: String): DeleteCommandPanelApi = DeleteCommandPanelApi(panelId)
    }

    override val path: Array<String> = arrayOf("v2", "panels", panelId)
}
