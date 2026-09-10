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

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.ApiModel
import love.forte.simbot.qguild.ApiModelConstructor
import love.forte.simbot.qguild.QQGuild
import kotlin.jvm.JvmStatic

/**
 * 机器人 C2C 单聊窗口底部的全局自定义菜单。
 *
 * [官方文档](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/menu-panel/)
 *
 * @since 4.7.0
 */
@ApiModel
@Serializable
public class CustomMenu @ApiModelConstructor internal constructor(
    /**
     * 菜单项列表。
     */
    public val items: List<Item> = emptyList(),
) {
    /**
     * [CustomMenu] 的菜单项。
     *
     * @since 4.7.0
     */
    @ApiModel
    @Serializable
    public class Item @ApiModelConstructor internal constructor(
        /**
         * 菜单项名称。
         */
        public val name: String? = null,
        /**
         * 菜单项类型。
         */
        public val type: CustomMenuItemType? = null,
        /**
         * 仅 [CustomMenuItemType.Menu] 有效的二级菜单项。
         */
        @SerialName("sub_menu_items")
        public val subMenuItems: List<SubItem>? = null,
        /**
         * 仅 [CustomMenuItemType.SendMessage] 有效的待填入消息。
         */
        @SerialName("send_message")
        public val sendMessage: String? = null,
        /**
         * 仅 [CustomMenuItemType.Link] 有效的跳转链接。
         */
        public val link: String? = null,
        /**
         * 仅 [CustomMenuItemType.Switch] 有效的开关配置。
         */
        public val switch: Switch? = null,
    ) {
        override fun toString(): String {
            return "Item(" +
                "link=$link, " +
                "name=$name, " +
                "type=$type, " +
                "subMenuItems=$subMenuItems, " +
                "sendMessage=$sendMessage, " +
                "switch=$switch)"
        }
    }

    /**
     * [Item] 的二级菜单项。
     *
     * @since 4.7.0
     */
    @ApiModel
    @Serializable
    public class SubItem @ApiModelConstructor internal constructor(
        /**
         * 二级菜单项名称。
         */
        public val name: String? = null,
        /**
         * 二级菜单项类型。
         */
        public val type: CustomMenuItemType? = null,
        /**
         * 仅 [CustomMenuItemType.SendMessage] 有效的待填入消息。
         */
        @SerialName("send_message")
        public val sendMessage: String? = null,
        /**
         * 仅 [CustomMenuItemType.Link] 有效的跳转链接。
         */
        public val link: String? = null,
    ) {
        override fun toString(): String {
            return "SubItem(link=$link, name=$name, type=$type, sendMessage=$sendMessage)"
        }
    }

    /**
     * [Item] 的开关配置。
     *
     * @since 4.7.0
     */
    @ApiModel
    @Serializable
    public class Switch @ApiModelConstructor internal constructor(
        /**
         * 开关唯一标识。
         */
        @SerialName("switch_id")
        public val switchId: String? = null,
        /**
         * 开关初始状态。
         */
        @SerialName("default")
        public val defaultValue: Boolean? = null,
    ) {
        override fun toString(): String {
            return "Switch(defaultValue=$defaultValue, switchId=$switchId)"
        }
    }

    public companion object {
        /**
         * 获取 [CustomMenuBuilder]。
         */
        @JvmStatic
        public fun builder(): CustomMenuBuilder = CustomMenuBuilder()

        /**
         * 将 JSON 字符串解析为 [CustomMenu]。
         */
        @JvmStatic
        public fun parse(jsonString: String): CustomMenu =
            QQGuild.DefaultJson.decodeFromString(serializer(), jsonString)
    }

    override fun toString(): String {
        return "CustomMenu(items=$items)"
    }
}

/**
 * 当前生效的自定义菜单及其版本。
 *
 * @since 4.7.0
 */
@ApiModel
@Serializable
public class CustomMenuSnapshot @ApiModelConstructor internal constructor(
    /**
     *  当前菜单版本。
     */
    public val version: Int = 0,
    /**
     * 当前生效的菜单；从未设置时为空。
     */
    public val menu: CustomMenu? = null,
) {
    override fun toString(): String {
        return "CustomMenuSnapshot(menu=$menu, version=$version)"
    }
}

/**
 * 自定义菜单更新后的版本。
 *
 * @since 4.7.0
 */
@ApiModel
@Serializable
public class CustomMenuUpdated @ApiModelConstructor internal constructor(
    /**
     * 更新后的菜单版本。
     */
    public val version: Int = 0,
) {
    override fun toString(): String {
        return "CustomMenuUpdated(version=$version)"
    }
}
