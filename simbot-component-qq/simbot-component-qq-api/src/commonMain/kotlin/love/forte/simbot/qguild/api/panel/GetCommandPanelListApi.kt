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

import io.ktor.http.*
import kotlinx.serialization.DeserializationStrategy
import love.forte.simbot.qguild.api.GetQQGuildApi
import love.forte.simbot.qguild.api.SimpleGetApiDescription
import love.forte.simbot.qguild.model.panel.CommandPanelPage
import love.forte.simbot.qguild.model.panel.CommandPanelScope
import kotlin.jvm.JvmExposeBoxed
import kotlin.jvm.JvmStatic

/**
 * [获取指令面板列表](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/menu-panel/panel/get-list.html)。
 *
 * @since 5.0
 */
public class GetCommandPanelListApi private constructor(
    private val scope: CommandPanelScope,
    private val cursor: String?,
    private val limit: Int?,
) : GetQQGuildApi<CommandPanelPage>() {
    /**
     * [GetCommandPanelListApi] 的描述与构建入口。
     */
    @OptIn(ExperimentalStdlibApi::class)
    public companion object Factory : SimpleGetApiDescription("/v2/panels") {
        /**
         * 使用给定查询参数构建 [GetCommandPanelListApi]。
         *
         * 注意参数不在本地进行校验。
         *
         * @param scope 生效场景，按指定场景筛选面板列表
         * @param cursor 分页游标。首次请求不传或传空串，后续请求传入上次响应中的 next_cursor 值
         * @param limit 每页拉取条数，默认 20，最大 50
         *
         * @since 5.0
         */
        @JvmStatic
        @JvmExposeBoxed
        public fun create(
            scope: CommandPanelScope,
            cursor: String? = null,
            limit: Int? = null,
        ): GetCommandPanelListApi = GetCommandPanelListApi(scope, cursor, limit)
    }

    override val path: Array<String> = arrayOf("v2", "panels")

    override val resultDeserializationStrategy: DeserializationStrategy<CommandPanelPage>
        get() = CommandPanelPage.serializer()

    override fun URLBuilder.buildUrl() {
        parameters.append("scope", scope.scope)
        cursor?.also { parameters.append("cursor", it) }
        limit?.also { parameters.append("limit", it.toString()) }
    }
}
