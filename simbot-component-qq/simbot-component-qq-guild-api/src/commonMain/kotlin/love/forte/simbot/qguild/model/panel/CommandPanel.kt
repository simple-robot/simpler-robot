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
 * @since 4.7.0
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
     * @since 4.7.0
     */
    @ApiModel
    @Serializable
    public class Item @ApiModelConstructor internal constructor(
        /**
         * 元素名称。
         *
         * - `type=command` 时用户点击后该内容会填入聊天输入框
         * - `type=link` 时仅用于面板展示
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
         *
         * 可选值：
         * - `command`（指令）
         * - `link`（链接跳转）
         */
        public val type: String? = null,
        /**
         * 是否仅管理员可操作。
         *
         * `true` 时仅频道/群管理员可点击，`false` 时所有用户可点击
         */
        @SerialName("only_admin")
        public val onlyAdmin: Boolean? = null,
        /**
         * 仅 [TYPE_LINK] 有效的跳转链接。
         */
        public val link: String? = null,
    ) {
        public companion object {
            /**
             * 指令元素类型。
             */
            public const val TYPE_COMMAND: String = "command"

            /**
             * 链接元素类型。
             */
            public const val TYPE_LINK: String = "link"
        }

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

/**
 * 指令面板记录。
 *
 * 列表查询不会返回 [userOpenids] 和 [groupOpenids]；详情查询才会在适用时返回它们。
 *
 * @since 4.7.0
 */
@ApiModel
@Serializable
public class CommandPanelRecord @ApiModelConstructor internal constructor(
    /**
     * 面板 ID。
     */
    @SerialName("panel_id")
    public val panelId: String,
    /**
     * 面板生效场景。
     */
    public val scope: CommandPanelScope,
    /**
     * 面板生效范围。
     */
    @SerialName("target_type")
    public val targetType: String,
    /**
     * 面板配置。
     */
    public val panel: CommandPanel,
    /**
     * 面板创建时间。
     */
    @SerialName("created_at")
    public val createdAt: String? = null,
    /**
     * 面板更新时间。
     */
    @SerialName("updated_at")
    public val updatedAt: String? = null,
    /**
     * 面板版本。
     */
    public val version: Int? = null,
    /**
     * 关联的 C2C 用户 OpenID 列表。
     */
    @SerialName("user_openids")
    public val userOpenids: List<String>? = null,
    /**
     * 关联的群 OpenID 列表。
     */
    @SerialName("group_openids")
    public val groupOpenids: List<String>? = null,
) {

    public companion object {
        /**
         * C2C 单聊场景。
         */
        public const val SCOPE_C2C: String = "c2c"

        /**
         * 群聊场景。
         */
        public const val SCOPE_GROUP: String = "group"

        /**
         * 文字子频道场景。
         */
        public const val SCOPE_CHANNEL: String = "channel"

        /**
         * 频道私信场景。
         */
        public const val SCOPE_DM: String = "dm"

        /**
         * 对指定场景下的所有目标生效。
         */
        public const val TARGET_TYPE_ALL: String = "all"

        /**
         * 仅对指定用户或群生效。
         */
        public const val TARGET_TYPE_SPECIFIC: String = "specific"
    }

    override fun toString(): String {
        return "CommandPanelRecord(" +
            "panelId='$panelId', " +
            "scope='$scope', " +
            "targetType='$targetType', " +
            "panel=$panel, " +
            "createdAt=$createdAt, " +
            "updatedAt=$updatedAt, " +
            "version=$version, " +
            "userOpenids=$userOpenids, " +
            "groupOpenids=$groupOpenids)"
    }


}

/**
 * 指令面板分页查询结果。
 *
 * @since 4.7.0
 */
@ApiModel
@Serializable
public class CommandPanelPage @ApiModelConstructor internal constructor(
    /**
     * 本页记录。
     */
    public val records: List<CommandPanelRecord> = emptyList(),
    /**
     * 下一页游标。
     */
    @SerialName("next_cursor")
    public val nextCursor: String = "",
    /**
     * 是否已经到达最后一页。
     */
    @SerialName("is_end")
    public val isEnd: Boolean = false,
) {
    override fun toString(): String {
        return "CommandPanelPage(records=$records, nextCursor='$nextCursor', isEnd=$isEnd)"
    }
}

/**
 * 创建指令面板的请求体。
 *
 * @since 4.7.0
 */
@ApiModel
@Serializable
public class CommandPanelCreate @ApiModelConstructor internal constructor(
    /**
     * 面板生效场景。
     *
     * 可选值：
     * - `c2c`（单聊）
     * - `group`（群聊）
     * - `channel`（文字子频道）
     * - `dm`（频道私信）
     * 四种场景均支持创建面板，但 channel 和 dm 场景仅支持全局配置（target_type 只能为 all）
     */
    public val scope: CommandPanelScope? = null,
    /**
     * 面板生效范围。
     *
     * 可选值：
     * - `all`（对该场景下所有用户/群生效）
     * - `specific`（仅对指定用户/群生效）
     *
     * 仅 c2c 和 group 场景支持 specific；channel 和 dm 场景只能传 all
     */
    @SerialName("target_type")
    public val targetType: String? = null,
    /**
     * C2C 场景中关联的用户 OpenID，仅 c2c 场景且 target_type=specific 时有效。
     * 指定面板对这些用户生效，一次最多传 20 个。后续可通过「修改指令面板关联对象」接口增删
     */
    @SerialName("user_openids")
    public val userOpenids: List<String>? = null,
    /**
     * 群聊场景中关联的群 OpenID，仅 group 场景且 target_type=specific 时有效。
     * 指定面板对这些群生效，一次最多传 20 个。
     */
    @SerialName("group_openids")
    public val groupOpenids: List<String>? = null,
    /**
     * 面板配置内容，定义面板中展示的指令和链接项
     */
    public val panel: CommandPanel? = null,
) {
    public companion object {
        /**
         * 获取 [CommandPanelCreateBuilder]。
         */
        @JvmStatic
        public fun builder(): CommandPanelCreateBuilder = CommandPanelCreateBuilder()
    }

    override fun toString(): String {
        return "CommandPanelCreate(" +
            "scope=$scope, " +
            "targetType=$targetType, " +
            "userOpenids=$userOpenids, " +
            "groupOpenids=$groupOpenids, " +
            "panel=$panel)"
    }


}

/**
 * 修改指令面板关联对象的请求体。
 *
 * @property op 关联操作类型。
 * @property userOpenids C2C 场景中要操作的用户 OpenID。
 * @property groupOpenids 群聊场景中要操作的群 OpenID。
 *
 * @since 4.7.0
 */
@ApiModel
@Serializable
public class CommandPanelTargetUpdate @ApiModelConstructor internal constructor(
    public val op: String? = null,
    @SerialName("user_openids")
    public val userOpenids: List<String>? = null,
    @SerialName("group_openids")
    public val groupOpenids: List<String>? = null,
) {
    public companion object {
        /**
         * 添加关联对象的操作类型。
         */
        public const val OP_ADD: String = "add"

        /**
         * 删除关联对象的操作类型。
         */
        public const val OP_DEL: String = "del"

        /**
         * 获取 [CommandPanelTargetUpdateBuilder]。
         */
        @JvmStatic
        public fun builder(): CommandPanelTargetUpdateBuilder = CommandPanelTargetUpdateBuilder()
    }

    override fun toString(): String {
        return "CommandPanelTargetUpdate(op=$op, userOpenids=$userOpenids, groupOpenids=$groupOpenids)"
    }

}

/**
 * 创建指令面板后的结果。
 *
 * @property panelId 新建面板 ID。
 *
 * @since 4.7.0
 */
@ApiModel
@Serializable
public class CommandPanelCreated @ApiModelConstructor internal constructor(
    @SerialName("panel_id")
    public val panelId: String,
) {
    override fun toString(): String {
        return "CommandPanelCreated(panelId='$panelId')"
    }
}

/**
 * 指令面板更新后的版本。
 *
 * @property version 更新后的面板版本。
 *
 * @since 4.7.0
 */
@ApiModel
@Serializable
public class CommandPanelUpdated @ApiModelConstructor internal constructor(
    public val version: Int = 0,
) {
    override fun toString(): String {
        return "CommandPanelUpdated(version=$version)"
    }
}
