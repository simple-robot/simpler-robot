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

/**
 * 与自定义菜单项类型相关的已知可选值的常量类。
 *
 * @see CustomMenu.Item.type
 * @see CustomMenu.SubItem.type
 * @see CustomMenuItemType
 * @since 4.7.0
 */
public object CustomMenuItemTypeValues {
    /**
     * 开关菜单项类型。
     */
    public const val SWITCH: String = "switch"

    /**
     * 发送消息菜单项类型。
     */
    public const val SEND_MESSAGE: String = "send_message"

    /**
     * 链接菜单项类型。
     */
    public const val LINK: String = "link"

    /**
     * 包含二级菜单的菜单项类型。
     */
    public const val MENU: String = "menu"
}
