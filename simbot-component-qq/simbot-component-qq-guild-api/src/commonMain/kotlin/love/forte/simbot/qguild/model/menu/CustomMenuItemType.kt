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

package love.forte.simbot.qguild.model.menu

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmExposeBoxed
import kotlin.jvm.JvmInline
import kotlin.jvm.JvmStatic

/**
 * 自定义菜单项的类型。
 *
 * @see CustomMenuItemTypeValues
 * @see CustomMenu.Item.type
 * @see CustomMenu.SubItem.type
 *
 * @since 5.0
 * @author Forte Scarlet
 */
@OptIn(ExperimentalStdlibApi::class)
@JvmInline
@JvmExposeBoxed
@Serializable
public value class CustomMenuItemType private constructor(public val value: String) {
    public companion object {
        /**
         * 开关菜单项类型。
         */
        @JvmStatic
        @get:JvmExposeBoxed
        public val Switch: CustomMenuItemType = CustomMenuItemType(CustomMenuItemTypeValues.SWITCH)

        /**
         * 发送消息菜单项类型。
         */
        @JvmStatic
        @get:JvmExposeBoxed
        public val SendMessage: CustomMenuItemType = CustomMenuItemType(CustomMenuItemTypeValues.SEND_MESSAGE)

        /**
         * 链接菜单项类型。
         */
        @JvmStatic
        @get:JvmExposeBoxed
        public val Link: CustomMenuItemType = CustomMenuItemType(CustomMenuItemTypeValues.LINK)

        /**
         * 包含二级菜单的菜单项类型。
         */
        @JvmStatic
        @get:JvmExposeBoxed
        public val Menu: CustomMenuItemType = CustomMenuItemType(CustomMenuItemTypeValues.MENU)

        /**
         * 构建一个自定义值的 [CustomMenuItemType]。
         */
        @JvmStatic
        @JvmExposeBoxed
        public fun of(value: String): CustomMenuItemType = CustomMenuItemType(value)
    }
}
