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
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.api.PutQQGuildApi
import love.forte.simbot.qguild.api.SimplePutApiDescription
import love.forte.simbot.qguild.model.panel.CommandPanel
import love.forte.simbot.qguild.model.panel.CommandPanelBuilder
import love.forte.simbot.qguild.model.panel.CommandPanelUpdated
import kotlin.jvm.JvmStatic
import kotlin.jvm.JvmSynthetic

/**
 * [修改指令面板](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/menu-panel/panel/put.html)。
 *
 * 此接口只覆盖面板元素与备注，不改变关联对象；参数不在本地进行校验。
 *
 * @since 5.0
 */
public class ModifyCommandPanelApi private constructor(
    panelId: String,
    override val body: Body,
) : PutQQGuildApi<CommandPanelUpdated>() {
    /**
     * [ModifyCommandPanelApi] 的描述与构建入口。
     *
     * @since 5.0
     */
    public companion object Factory : SimplePutApiDescription("/v2/panels/{panel_id}") {
        /**
         * 使用 [panel] 构建 [ModifyCommandPanelApi]。
         */
        @JvmStatic
        public fun create(panelId: String, panel: CommandPanel? = null): ModifyCommandPanelApi =
            ModifyCommandPanelApi(panelId, Body(panel))

        /**
         * 通过 DSL 构建面板并创建 [ModifyCommandPanelApi]。
         */
        @JvmSynthetic
        public inline fun create(panelId: String, block: CommandPanelBuilder.() -> Unit): ModifyCommandPanelApi =
            create(panelId, CommandPanelBuilder().apply(block).build())
    }

    override val path: Array<String> = arrayOf("v2", "panels", panelId)

    override val resultDeserializationStrategy: DeserializationStrategy<CommandPanelUpdated>
        get() = CommandPanelUpdated.serializer()

    override fun createBody(): Any? = null

    /**
     * [ModifyCommandPanelApi] 的请求体。
     *
     * @property panel 要整体覆盖的面板配置。
     *
     * @since 5.0
     */
    @Serializable
    public class Body internal constructor(
        public val panel: CommandPanel? = null,
    )
}
