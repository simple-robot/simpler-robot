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
import love.forte.simbot.qguild.api.PostQQGuildApi
import love.forte.simbot.qguild.api.SimplePostApiDescription
import love.forte.simbot.qguild.model.panel.CommandPanelCreate
import love.forte.simbot.qguild.model.panel.CommandPanelCreateBuilder
import love.forte.simbot.qguild.model.panel.CommandPanelCreated
import kotlin.jvm.JvmStatic
import kotlin.jvm.JvmSynthetic

/**
 * [创建指令面板](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/menu-panel/panel/post.html)。
 *
 * @since 5.0
 */
public class CreateCommandPanelApi private constructor(
    override val body: CommandPanelCreate,
) : PostQQGuildApi<CommandPanelCreated>() {
    /**
     * [CreateCommandPanelApi] 的描述与构建入口。
     */
    public companion object Factory : SimplePostApiDescription("/v2/panels") {
        /**
         * 使用 [body] 构建 [CreateCommandPanelApi]。
         */
        @JvmStatic
        public fun create(body: CommandPanelCreate): CreateCommandPanelApi = CreateCommandPanelApi(body)

        /**
         * 通过 DSL 构建器创建 [CreateCommandPanelApi]。
         */
        @JvmSynthetic
        public inline fun create(block: CommandPanelCreateBuilder.() -> Unit): CreateCommandPanelApi =
            create(CommandPanelCreateBuilder().apply(block).build())
    }

    override val path: Array<String> = arrayOf("v2", "panels")

    override val resultDeserializationStrategy: DeserializationStrategy<CommandPanelCreated>
        get() = CommandPanelCreated.serializer()

    override fun createBody(): Any? = null
}
