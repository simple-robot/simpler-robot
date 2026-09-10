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

import love.forte.simbot.qguild.api.PutQQGuildApi
import love.forte.simbot.qguild.api.QQGuildApiWithoutResult
import love.forte.simbot.qguild.api.SimplePutApiDescription
import love.forte.simbot.qguild.model.panel.CommandPanelTargetUpdate
import love.forte.simbot.qguild.model.panel.CommandPanelTargetUpdateBuilder
import kotlin.jvm.JvmStatic
import kotlin.jvm.JvmSynthetic

/**
 * [修改指令面板关联对象](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/menu-panel/panel/put-target.html)。
 *
 * @since 4.7.0
 */
public class ModifyCommandPanelTargetApi private constructor(
    panelId: String,
    override val body: CommandPanelTargetUpdate,
) : PutQQGuildApi<Unit>(), QQGuildApiWithoutResult {
    /**
     * [ModifyCommandPanelTargetApi] 的描述与构建入口。
     *
     * @since 4.7.0
     */
    public companion object Factory : SimplePutApiDescription("/v2/panels/{panel_id}/target") {
        /**
         * 使用 [body] 构建 [ModifyCommandPanelTargetApi]。
         */
        @JvmStatic
        public fun create(panelId: String, body: CommandPanelTargetUpdate): ModifyCommandPanelTargetApi =
            ModifyCommandPanelTargetApi(panelId, body)

        /**
         * 通过 DSL 构建请求体并创建 [ModifyCommandPanelTargetApi]。
         */
        @JvmSynthetic
        public inline fun create(
            panelId: String,
            block: CommandPanelTargetUpdateBuilder.() -> Unit,
        ): ModifyCommandPanelTargetApi =
            create(panelId, CommandPanelTargetUpdateBuilder().apply(block).build())
    }

    override val path: Array<String> = arrayOf("v2", "panels", panelId, "target")

    override fun createBody(): Any? = null
}
