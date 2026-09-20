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

@file:OptIn(ExperimentalStdlibApi::class)

package love.forte.simbot.qguild.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.common.ApiModel
import love.forte.simbot.qguild.common.ApiModelConstructor
import love.forte.simbot.qguild.common.DataClassCompatibilities
import love.forte.simbot.qguild.common.QQ
import kotlin.jvm.JvmExposeBoxed
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic


/**
 * [消息交互=>消息按钮](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/message/trans/msg-btn.html)
 *
 * @see buildMessageKeyboard
 * @see MessageKeyboards
 *
 * @property id 按钮ID：在一个 keyboard 消息内设置唯一
 * @property renderData 按钮的展示数据
 * @property action 按钮的操作数据
 * @author ForteScarlet
 */
@ApiModel
@Serializable
public class MessageKeyboard @ApiModelConstructor constructor(
    public val id: String? = null,
    @SerialName("render_data")
    public val renderData: RenderData? = null,
    public val action: Action? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MessageKeyboard) return false

        if (id != other.id) return false
        if (renderData != other.renderData) return false
        if (action != other.action) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (renderData?.hashCode() ?: 0)
        result = 31 * result + (action?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String = "MessageKeyboard(id=$id, renderData=$renderData, action=$action)"

    //region data-class 兼容
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("id"))
    public operator fun component1(): String? = id

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("renderData"))
    public operator fun component2(): RenderData? = renderData

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("action"))
    public operator fun component3(): Action? = action

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
    public fun copy(
        id: String? = this.id,
        renderData: RenderData? = this.renderData,
        action: Action? = this.action,
    ): MessageKeyboard = MessageKeyboard(id, renderData, action)
    //endregion

    public companion object {
        /**
         * Create a [MessageKeyboard]
         */
        @JvmStatic
        public fun create(id: String): MessageKeyboard = MessageKeyboard(id = id)

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
            return QQ.DefaultJson.decodeFromString(serializer(), jsonString)
        }
    }

    /**
     * [MessageKeyboard.renderData].
     * 参考 [官方文档](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/message/trans/msg-btn.html)
     *
     * @property label 按钮上的文字
     * @property visitedLabel 点击后按钮上的文字
     * @property style 按钮样式：0 灰色线框，1 蓝色线框
     */
    @ApiModel
    @Serializable
    public class RenderData
    @ApiModelConstructor @JvmExposeBoxed
    internal constructor(
        @Suppress("unused", "ConstructorParameterNaming")
        @Deprecated("Unused", level = DeprecationLevel.HIDDEN)
        private val _deprecatedStyle: Int = 0,
        public val label: String,
        public val visitedLabel: String,
        @get:JvmExposeBoxed
        public val style: MessageKeyboardStyle,
    ) {
        /**
         * 兼容旧版 `Int` 类型的构造函数。
         */
        @Deprecated(
            DataClassCompatibilities.DEPRECATED_CONSTRUCTOR_MESSAGE,
            level = DeprecationLevel.ERROR,
        )
        @ApiModelConstructor
        public constructor(label: String, visitedLabel: String, style: Int) : this(
            _deprecatedStyle = style,
            label = label,
            visitedLabel = visitedLabel,
            style = MessageKeyboardStyle.of(style),
        )

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is RenderData) return false

            if (label != other.label) return false
            if (visitedLabel != other.visitedLabel) return false
            if (style != other.style) return false

            return true
        }

        override fun hashCode(): Int {
            var result = label.hashCode()
            result = 31 * result + visitedLabel.hashCode()
            result = 31 * result + style.hashCode()
            return result
        }

        override fun toString(): String =
            "RenderData(label='$label', visitedLabel='$visitedLabel', style=$style)"

        //region data-class 兼容
        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("label"))
        public operator fun component1(): String = label

        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("visitedLabel"))
        public operator fun component2(): String = visitedLabel

        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("style.value"))
        public operator fun component3(): Int = style.value

        @Suppress("DeprecatedCallableAddReplaceWith")
        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
        public fun copy(
            label: String = this.label,
            visitedLabel: String = this.visitedLabel,
            style: Int = this.style.value,
        ): RenderData = RenderData(
            label = label,
            visitedLabel = visitedLabel,
            style = MessageKeyboardStyle.of(style),
        )
        //endregion
    }

    /**
     * [Action.permission].
     * 参考 [官方文档](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/message/trans/msg-btn.html)
     *
     * @property type 参考 [MessageKeyboardActionPermissionType]
     * @property specifyUserIds 有权限的用户 id 的列表
     * @property specifyRoleIds 有权限的身份组 id 的列表（仅频道可用）
     */
    @ApiModel
    @Serializable
    public class ActionPermission internal constructor(
        @Suppress("unused", "ConstructorParameterNaming")
        @Deprecated("Unused", level = DeprecationLevel.HIDDEN)
        private val _deprecatedType: Int = 0,
        @get:JvmExposeBoxed
        public val type: MessageKeyboardActionPermissionType,
        @SerialName("specify_user_ids")
        public val specifyUserIds: List<String>? = null,
        @SerialName("specify_role_ids")
        public val specifyRoleIds: List<String>? = null,
    ) {
        /**
         * 兼容旧版 `Int` 类型的构造函数。
         */
        @Deprecated(
            DataClassCompatibilities.DEPRECATED_CONSTRUCTOR_MESSAGE,
            level = DeprecationLevel.ERROR,
        )
        @ApiModelConstructor
        public constructor(
            type: Int,
            specifyUserIds: List<String>? = null,
            specifyRoleIds: List<String>? = null,
        ) : this(
            _deprecatedType = type,
            type = MessageKeyboardActionPermissionType.of(type),
            specifyUserIds = specifyUserIds,
            specifyRoleIds = specifyRoleIds,
        )

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is ActionPermission) return false

            if (type != other.type) return false
            if (specifyUserIds != other.specifyUserIds) return false
            if (specifyRoleIds != other.specifyRoleIds) return false

            return true
        }

        override fun hashCode(): Int {
            var result = type.hashCode()
            result = 31 * result + specifyUserIds.hashCode()
            result = 31 * result + specifyRoleIds.hashCode()
            return result
        }

        override fun toString(): String {
            return "ActionPermission(" +
                "type=$type, " +
                "specifyUserIds=$specifyUserIds, " +
                "specifyRoleIds=$specifyRoleIds)"
        }

        //region data-class 兼容
        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("type.value"))
        public operator fun component1(): Int = type.value

        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("specifyUserIds"))
        public operator fun component2(): List<String>? = specifyUserIds

        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("specifyRoleIds"))
        public operator fun component3(): List<String>? = specifyRoleIds

        @Suppress("DeprecatedCallableAddReplaceWith")
        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
        public fun copy(
            type: Int = this.type.value,
            specifyUserIds: List<String>? = this.specifyUserIds,
            specifyRoleIds: List<String>? = this.specifyRoleIds,
        ): ActionPermission = ActionPermission(
            type = MessageKeyboardActionPermissionType.of(type),
            specifyUserIds = specifyUserIds,
            specifyRoleIds = specifyRoleIds,
        )
        //endregion

        public companion object {
            /**
             * 一个 type = 1 的 [ActionPermission]，表示仅管理者可操作。
             *
             * @since 4.4.0
             */
            @JvmStatic
            public val AdminOnly: ActionPermission = ActionPermission(
                type = MessageKeyboardActionPermissionType.AdminOnly,
            )

            /**
             * 一个 type = 2 的 [ActionPermission]，表示所有人可访问。
             *
             * @since 4.4.0
             */
            @JvmStatic
            public val AllAccessible: ActionPermission = ActionPermission(
                type = MessageKeyboardActionPermissionType.AllAccessible,
            )
        }
    }

    /**
     * [MessageKeyboard.action].
     * 参考 [官方文档](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/message/trans/msg-btn.html)
     *
     * @property permission 操作权限
     * @property data 操作相关的数据
     * @property reply 指令按钮可用，指令是否带引用回复本消息，默认 false。支持版本 8983
     * @property enter 指令按钮可用，点击按钮后直接自动发送 data，仅单聊可用，默认 false。支持版本 8983
     * @property anchor 本字段仅在指令按钮下有效，设置为 1 时点击按钮自动唤起手机 QQ 选图器
     * @property unsupportTips 客户端不支持本 action 时弹出的 toast 文案
     * @property type 0 跳转按钮，1 回调按钮，2 指令按钮
     */
    @ApiModel
    @Serializable
    public class Action
    @ApiModelConstructor @JvmExposeBoxed
    internal constructor(
        @Suppress("unused", "ConstructorParameterNaming")
        @Deprecated("Unused", level = DeprecationLevel.HIDDEN)
        private val _deprecatedType: Int = 0,
        public val permission: ActionPermission? = null,
        public val data: String?,
        public val reply: Boolean? = null,
        public val enter: Boolean? = null,
        @get:JvmExposeBoxed
        public val anchor: MessageKeyboardActionAnchor? = null,
        @SerialName("unsupport_tips")
        public val unsupportTips: String,
        @get:JvmExposeBoxed
        public val type: MessageKeyboardActionType = MessageKeyboardActionType.Command,
    ) {
        /**
         * 兼容旧版 `Int` 类型的构造函数。
         */
        @Deprecated(
            DataClassCompatibilities.DEPRECATED_CONSTRUCTOR_MESSAGE,
            level = DeprecationLevel.ERROR,
        )
        @ApiModelConstructor
        public constructor(
            permission: ActionPermission? = null,
            data: String?,
            reply: Boolean? = null,
            enter: Boolean? = null,
            anchor: Int? = null,
            unsupportTips: String,
            @IntroducedAt("4.2.3")
            type: Int = MessageKeyboardActionType.COMMAND_VALUE,
        ) : this(
            _deprecatedType = type,
            permission = permission,
            data = data,
            reply = reply,
            enter = enter,
            anchor = anchor?.let(MessageKeyboardActionAnchor::of),
            unsupportTips = unsupportTips,
            type = MessageKeyboardActionType.of(type),
        )

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Action) return false

            if (permission != other.permission) return false
            if (data != other.data) return false
            if (reply != other.reply) return false
            if (enter != other.enter) return false
            if (anchor != other.anchor) return false
            if (unsupportTips != other.unsupportTips) return false
            if (type != other.type) return false

            return true
        }

        override fun hashCode(): Int {
            var result = permission?.hashCode() ?: 0
            result = 31 * result + (data?.hashCode() ?: 0)
            result = 31 * result + (reply?.hashCode() ?: 0)
            result = 31 * result + (enter?.hashCode() ?: 0)
            result = 31 * result + (anchor?.hashCode() ?: 0)
            result = 31 * result + unsupportTips.hashCode()
            result = 31 * result + type.hashCode()
            return result
        }

        override fun toString(): String {
            return "Action(" +
                "permission=$permission, " +
                "data=$data, " +
                "reply=$reply, " +
                "enter=$enter, " +
                "anchor=$anchor, " +
                "unsupportTips='$unsupportTips', " +
                "type=$type)"
        }

        //region data-class 兼容
        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("permission"))
        public operator fun component1(): ActionPermission? = permission

        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("data"))
        public operator fun component2(): String? = data

        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("reply"))
        public operator fun component3(): Boolean? = reply

        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("enter"))
        public operator fun component4(): Boolean? = enter

        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("anchor"))
        public operator fun component5(): Int? = anchor?.value

        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("unsupportTips"))
        public operator fun component6(): String = unsupportTips

        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("type.value"))
        public operator fun component7(): Int = type.value

        @Suppress("DeprecatedCallableAddReplaceWith")
        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
        public fun copy(
            permission: ActionPermission? = this.permission,
            data: String? = this.data,
            reply: Boolean? = this.reply,
            enter: Boolean? = this.enter,
            anchor: Int? = this.anchor?.value,
            unsupportTips: String = this.unsupportTips,
            type: Int = this.type.value,
        ): Action = Action(
            permission = permission,
            data = data,
            reply = reply,
            enter = enter,
            anchor = anchor?.let(MessageKeyboardActionAnchor::of),
            unsupportTips = unsupportTips,
            type = MessageKeyboardActionType.of(type),
        )
        //endregion
    }
}
