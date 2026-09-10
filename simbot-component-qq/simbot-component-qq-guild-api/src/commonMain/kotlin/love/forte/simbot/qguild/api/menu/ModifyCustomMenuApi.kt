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
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.api.PutQQGuildApi
import love.forte.simbot.qguild.api.SimplePutApiDescription
import love.forte.simbot.qguild.model.menu.CustomMenu
import love.forte.simbot.qguild.model.menu.CustomMenuBuilder
import love.forte.simbot.qguild.model.menu.CustomMenuUpdated
import kotlin.jvm.JvmStatic
import kotlin.jvm.JvmSynthetic

/**
 * [设置自定义菜单](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/menu-panel/menu/put.html)。
 *
 * 传入的菜单内容会整体覆盖当前菜单配置；参数不在本地进行校验。
 *
 * @since 4.7.0
 */
public class ModifyCustomMenuApi private constructor(
    override val body: Body,
) : PutQQGuildApi<CustomMenuUpdated>() {
    /**
     * [ModifyCustomMenuApi] 的描述与构建入口。
     */
    public companion object Factory : SimplePutApiDescription("/v2/menu") {
        /**
         * 使用 [menu] 构建 [ModifyCustomMenuApi]。
         */
        @JvmStatic
        public fun create(menu: CustomMenu? = null): ModifyCustomMenuApi =
            ModifyCustomMenuApi(Body(menu))

        /**
         * 通过 DSL 构建菜单并创建 [ModifyCustomMenuApi]。
         */
        @JvmSynthetic
        public inline fun create(block: CustomMenuBuilder.() -> Unit): ModifyCustomMenuApi =
            create(CustomMenuBuilder().apply(block).build())
    }

    override val path: Array<String> = arrayOf("v2", "menu")

    override val resultDeserializationStrategy: DeserializationStrategy<CustomMenuUpdated>
        get() = CustomMenuUpdated.serializer()

    override fun createBody(): Any? = null

    /**
     * [ModifyCustomMenuApi] 的请求体。
     *
     * @property menu 要整体覆盖的菜单。
     *
     * @since 4.7.0
     */
    @Serializable
    public class Body internal constructor(
        public val menu: CustomMenu? = null,
    )
}
