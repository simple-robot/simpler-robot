/*
 *     Copyright (c) 2024-2026. ForteScarlet.
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

package love.forte.simbot.qguild.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.ApiModel
import love.forte.simbot.qguild.ApiModelConstructor
import love.forte.simbot.qguild.QQGuild
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic


/**
 * [消息交互=>消息按钮](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/message/trans/msg-btn.html)
 *
 * @author ForteScarlet
 */
@ApiModel
@Serializable
public data class MessageKeyboard @ApiModelConstructor constructor(
    /**
     * 按钮ID：在一个 keyboard 消息内设置唯一
     */
    val id: String? = null,
    @SerialName("render_data")
    val renderData: RenderData? = null,
    val action: Action? = null,
) {
    public companion object {
        /**
         * Create a [MessageKeyboard]
         */
        @JvmStatic
        public fun create(id: String): MessageKeyboard =
            MessageKeyboard(id = id)

        /**
         * Create a [MessageKeyboardBuilder].
         *
         * @since 4.2.3
         * @see MessageKeyboardBuilder
         */
        @JvmStatic
        @JvmOverloads
        public fun builder(id: String? = null): MessageKeyboardBuilder = MessageKeyboardBuilder(id)

        /**
         * 将一个JSON字符串解析为 [MessageKeyboard] 对象。
         *
         * @since 4.2.3
         */
        @JvmStatic
        public fun parse(jsonString: String): MessageKeyboard {
            return QQGuild.DefaultJson.decodeFromString(serializer(), jsonString)
        }
    }

    /**
     * [MessageKeyboard.renderData].
     * 参考 [官方文档](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/message/trans/msg-btn.html)
     */
    @ApiModel
    @Serializable
    public data class RenderData @ApiModelConstructor constructor(
        /**
         * 	按钮上的文字
         */
        val label: String,
        /**
         * 点击后按钮的上文字
         */
        val visitedLabel: String,
        /**
         * 按钮样式：0 灰色线框，1 蓝色线框
         */
        val style: Int,
    )

    /**
     * [Action.permission].
     * 参考 [官方文档](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/message/trans/msg-btn.html)
     */
    @ApiModel
    @Serializable
    public data class ActionPermission @ApiModelConstructor constructor(
        /**
         * 0 指定用户可操作，1 仅管理者可操作，2 所有人可操作，3 指定身份组可操作（仅频道可用）
         */
        val type: Int,
        /**
         * 有权限的用户 id 的列表
         */
        @SerialName("specify_user_ids")
        val specifyUserIds: List<String>? = null,
        /**
         * 有权限的身份组 id 的列表（仅频道可用）
         */
        @SerialName("specify_role_ids")
        val specifyRoleIds: List<String>? = null,
    )

    /**
     * [MessageKeyboard.action].
     * 参考 [官方文档](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/message/trans/msg-btn.html)
     */
    @ConsistentCopyVisibility
    @ApiModel
    @Serializable
    public data class Action @ApiModelConstructor internal constructor(
        val permission: ActionPermission? = null,
        /**
         * 操作相关的数据
         */
        val data: String?,
        /**
         * 指令按钮可用，指令是否带引用回复本消息，默认 false。支持版本 8983
         */
        val reply: Boolean? = null,
        /**
         * 指令按钮可用，点击按钮后直接自动发送 data，仅单聊可用，默认 false。支持版本 8983
         */
        val enter: Boolean? = null,
        /**
         * 本字段仅在指令按钮下有效，设置后后会忽略 action.enter 配置。
         * 设置为 1 时 ，点击按钮自动唤起启手Q选图器，其他值暂无效果。
         * （仅支持手机端版本 8983+ 的单聊场景，桌面端不支持）
         */
        val anchor: Int? = null,
        /**
         * 客户端不支持本 action 的时候，弹出的 toast 文案
         */
        @SerialName("unsupport_tips")
        val unsupportTips: String,
        /**
         * 设置 0 跳转按钮：http 或 小程序 客户端识别 scheme，
         * 设置 1 回调按钮：回调后台接口, data 传给后台，
         * 设置 2 指令按钮：自动在输入框插入 @bot data。
         *
         * @since 4.2.3
         */
        val type: Int,
    ) {
        /**
         * 构造函数。
         * 4.2.2 版本以前作为主构造使用，现在用作兼容性辅助构造。
         * 为了兼容，`type` 属性默认为 `2`。
         */
        @ApiModelConstructor
        public constructor(
            permission: ActionPermission? = null,
            data: String?,
            reply: Boolean? = null,
            enter: Boolean? = null,
            anchor: Int? = null,
            unsupportTips: String,
        ) : this(
            permission = permission,
            data = data,
            reply = reply,
            enter = enter,
            anchor = anchor,
            unsupportTips = unsupportTips,
            type = 2
        )

        /**
         * 用于兼容截止到 4.2.2 版本的 data class copy 函数而使用的兼容性函数。
         */
        public fun copy(
            permission: ActionPermission? = this.permission,
            data: String? = this.data,
            reply: Boolean? = this.reply,
            enter: Boolean? = this.enter,
            anchor: Int? = this.anchor,
            unsupportTips: String = this.unsupportTips,
        ): Action = copy(
            permission = permission,
            data = data,
            reply = reply,
            enter = enter,
            anchor = anchor,
            unsupportTips = unsupportTips,
            type = type
        )
    }
}
