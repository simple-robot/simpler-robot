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

package love.forte.simbot.qguild.api.menu

import kotlinx.serialization.DeserializationStrategy
import love.forte.simbot.qguild.api.GetQQGuildApi
import love.forte.simbot.qguild.api.SimpleGetApiDescription
import love.forte.simbot.qguild.model.menu.CustomMenuSnapshot
import kotlin.jvm.JvmStatic

/**
 * [获取自定义菜单](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/menu-panel/menu/get.html)。
 *
 * 自定义菜单仅在 C2C 单聊会话中展示。
 *
 * @since 4.7.0
 */
public class GetCustomMenuApi private constructor() : GetQQGuildApi<CustomMenuSnapshot>() {
    /**
     * [GetCustomMenuApi] 的描述与构建入口。
     *
     * @since 4.7.0
     */
    public companion object Factory : SimpleGetApiDescription("/v2/menu") {
        /**
         * 构建 [GetCustomMenuApi]。
         *
         * @since 4.7.0
         */
        @JvmStatic
        public fun create(): GetCustomMenuApi = GetCustomMenuApi()
    }

    override val path: Array<String> = arrayOf("v2", "menu")

    override val resultDeserializationStrategy: DeserializationStrategy<CustomMenuSnapshot>
        get() = CustomMenuSnapshot.serializer()
}
