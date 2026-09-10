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

package love.forte.simbot.qguild.model.panel

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.ApiModel
import love.forte.simbot.qguild.ApiModelConstructor
import love.forte.simbot.qguild.QQGuild
import kotlin.jvm.JvmStatic

/**
 * 在会话中展示指令或链接的指令面板配置。
 *
 * [官方文档](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/menu-panel/)
 *
 * @property items 面板元素。定义面板中展示的指令或链接项，一个指令面板里最多配置 20 个面板元素。
 * @property remark 面板备注，用于开发者标记面板用途，最多 255 个字符，不对用户展示。
 * @property version 当前版本号。
 *
 * @since 5.0
 */
@ApiModel
@Serializable
public class CommandPanel @ApiModelConstructor internal constructor(
    public val items: List<Item> = emptyList(),
    public val remark: String? = null,
    public val version: Int? = null,
) {
    /**
     * 指令面板中的元素。
     *
     * @since 5.0
     */
    @ApiModel
    @Serializable
    public class Item @ApiModelConstructor internal constructor(
        /**
         * 元素名称。
         *
         * - [type] = [CommandPanelItemType.Command] 时用户点击后该内容会填入聊天输入框
         * - [type] = [CommandPanelItemType.Link] 时仅用于面板展示
         *
         * 最多 14 个字符，约 7 个中文汉字
         */
        public val name: String? = null,
        /**
         * 元素描述，用于补充说明该指令或链接的功能，在面板中展示给用户。
         * 最多 30 个字符，约 15 个中文汉字。
         */
        public val desc: String? = null,
        /**
         * 元素类型。
         */
        public val type: CommandPanelItemType? = null,
        /**
         * 是否仅管理员可操作。
         *
         * `true` 时仅频道/群管理员可点击，`false` 时所有用户可点击
         */
        @SerialName("only_admin")
        public val onlyAdmin: Boolean? = null,
        /**
         * 仅 [CommandPanelItemType.Link] 有效的跳转链接。
         */
        public val link: String? = null,
    ) {
        override fun toString(): String {
            return "Item(name=$name, desc=$desc, type=$type, onlyAdmin=$onlyAdmin, link=$link)"
        }
    }

    public companion object {
        /**
         * 获取 [CommandPanelBuilder]。
         */
        @JvmStatic
        public fun builder(): CommandPanelBuilder = CommandPanelBuilder()

        /**
         * 将 JSON 字符串解析为 [CommandPanel]。
         */
        @JvmStatic
        public fun parse(jsonString: String): CommandPanel =
            QQGuild.DefaultJson.decodeFromString(serializer(), jsonString)
    }

    override fun toString(): String {
        return "CommandPanel(items=$items, remark=$remark, version=$version)"
    }
}
