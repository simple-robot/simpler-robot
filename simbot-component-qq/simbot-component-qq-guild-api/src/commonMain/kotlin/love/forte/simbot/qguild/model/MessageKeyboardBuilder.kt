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

package love.forte.simbot.qguild.model

import love.forte.simbot.common.function.ConfigurerFunction
import love.forte.simbot.common.function.invokeBy
import kotlin.jvm.JvmOverloads

/**
 * 通过 DSL 构建 [MessageKeyboard]。
 *
 * @since 4.2.3
 */
public inline fun buildMessageKeyboard(block: MessageKeyboardBuilder.() -> Unit): MessageKeyboard =
    MessageKeyboardBuilder().also(block).build()

@Retention(AnnotationRetention.BINARY)
@DslMarker
internal annotation class MessageKeyboardBuilderDsl

/**
 * 用于更便捷构建 [MessageKeyboard] 的构建器，Java 中可用链式 API，Kotlin 额外提供一些 DSL API。
 *
 * @since 4.2.3
 * @author ForteScarlet
 */
@MessageKeyboardBuilderDsl
public class MessageKeyboardBuilder(public var id: String? = null) {
    public var renderData: MessageKeyboard.RenderData? = null
    public var action: MessageKeyboard.Action? = null

    /**
     * 基于现有 [keyboard] 填充当前构建器。
     */
    public fun from(keyboard: MessageKeyboard): MessageKeyboardBuilder = also {
        id = keyboard.id
        renderData = keyboard.renderData
        action = keyboard.action
    }

    /**
     * 设置按钮 ID。
     */
    public fun id(id: String?): MessageKeyboardBuilder = also {
        this.id = id
    }

    /**
     * 直接设置 [MessageKeyboard.RenderData]。
     */
    public fun renderData(renderData: MessageKeyboard.RenderData?): MessageKeyboardBuilder = also {
        this.renderData = renderData
    }

    /**
     * 通过参数构建并设置 [MessageKeyboard.RenderData]。
     */
    @JvmOverloads
    public fun renderData(
        label: String,
        visitedLabel: String = label,
        style: Int = 0
    ): MessageKeyboardBuilder = also {
        renderData = MessageKeyboard.RenderData(label, visitedLabel, style)
    }

    /**
     * 通过 DSL 构建并设置 [MessageKeyboard.RenderData]。
     */
    public fun renderData(block: ConfigurerFunction<MessageKeyboardRenderDataBuilder>): MessageKeyboardBuilder = also {
        renderData = MessageKeyboardRenderDataBuilder().invokeBy(block).build()
    }

    /**
     * 直接设置 [MessageKeyboard.Action]。
     */
    public fun action(action: MessageKeyboard.Action?): MessageKeyboardBuilder = also {
        this.action = action
    }

    /**
     * 通过参数构建并设置 [MessageKeyboard.Action]。
     */
    public fun action(
        data: String? = null,
        unsupportTips: String = "",
        type: Int = 2,
        permission: MessageKeyboard.ActionPermission? = null,
        reply: Boolean? = null,
        enter: Boolean? = null,
        anchor: Int? = null,
    ): MessageKeyboardBuilder = also {
        action = MessageKeyboard.Action(
            permission = permission,
            data = data,
            reply = reply,
            enter = enter,
            anchor = anchor,
            unsupportTips = unsupportTips,
            type = type
        )
    }

    /**
     * 通过 DSL 构建并设置 [MessageKeyboard.Action]。
     */
    public fun action(block: ConfigurerFunction<MessageKeyboardActionBuilder>): MessageKeyboardBuilder = also {
        action = MessageKeyboardActionBuilder().invokeBy(block).build()
    }

    /**
     * 构建 [MessageKeyboard]。
     */
    public fun build(): MessageKeyboard =
        MessageKeyboard(id = id, renderData = renderData, action = action)
}

@MessageKeyboardBuilderDsl
public class MessageKeyboardRenderDataBuilder {
    public lateinit var label: String

    public var visitedLabel: String? = null

    public var style: Int = 0

    /**
     * 基于现有 [renderData] 填充当前构建器。
     */
    public fun from(renderData: MessageKeyboard.RenderData): MessageKeyboardRenderDataBuilder = also {
        label = renderData.label
        visitedLabel = renderData.visitedLabel
        style = renderData.style
    }

    /**
     * 设置按钮显示文本。
     */
    public fun label(label: String): MessageKeyboardRenderDataBuilder = also {
        this.label = label
    }

    /**
     * 设置按钮点击后的文本。
     */
    public fun visitedLabel(visitedLabel: String?): MessageKeyboardRenderDataBuilder = also {
        this.visitedLabel = visitedLabel
    }

    /**
     * 设置按钮样式。
     */
    public fun style(style: Int): MessageKeyboardRenderDataBuilder = also {
        this.style = style
    }

    /**
     * 构建 [MessageKeyboard.RenderData]。
     */
    public fun build(): MessageKeyboard.RenderData =
        MessageKeyboard.RenderData(
            label = label,
            visitedLabel = visitedLabel ?: label,
            style = style
        )
}

/**
 * [MessageKeyboard.Action] 的构建器。
 *
 * @since 4.2.3
 * @see MessageKeyboardBuilder
 *
 */
@MessageKeyboardBuilderDsl
public class MessageKeyboardActionBuilder {
    public var permission: MessageKeyboard.ActionPermission? = null
    public var data: String? = null
    public var reply: Boolean? = null
    public var enter: Boolean? = null
    public var anchor: Int? = null
    public var unsupportTips: String = ""
    public var type: Int = 2

    /**
     * 基于现有 [action] 填充当前构建器。
     */
    public fun from(action: MessageKeyboard.Action): MessageKeyboardActionBuilder = also {
        permission = action.permission
        data = action.data
        reply = action.reply
        enter = action.enter
        anchor = action.anchor
        unsupportTips = action.unsupportTips
        type = action.type
    }

    /**
     * 直接设置操作权限。
     */
    public fun permission(permission: MessageKeyboard.ActionPermission?): MessageKeyboardActionBuilder = also {
        this.permission = permission
    }

    /**
     * 通过 DSL 构建并设置操作权限。
     */
    public fun permission(
        block: ConfigurerFunction<MessageKeyboardActionPermissionBuilder>
    ): MessageKeyboardActionBuilder = also {
        permission = MessageKeyboardActionPermissionBuilder().invokeBy(block).build()
    }

    /**
     * 设置操作数据。
     */
    public fun data(data: String?): MessageKeyboardActionBuilder = also {
        this.data = data
    }

    /**
     * 设置是否引用回复原消息。
     */
    public fun reply(reply: Boolean?): MessageKeyboardActionBuilder = also {
        this.reply = reply
    }

    /**
     * 设置是否自动发送数据。
     */
    public fun enter(enter: Boolean?): MessageKeyboardActionBuilder = also {
        this.enter = enter
    }

    /**
     * 设置锚点值。
     */
    public fun anchor(anchor: Int?): MessageKeyboardActionBuilder = also {
        this.anchor = anchor
    }

    /**
     * 设置不支持时的提示文案。
     */
    public fun unsupportTips(unsupportTips: String): MessageKeyboardActionBuilder = also {
        this.unsupportTips = unsupportTips
    }

    /**
     * 设置操作类型。
     */
    public fun type(type: Int): MessageKeyboardActionBuilder = also {
        this.type = type
    }

    /**
     * 构建 [MessageKeyboard.Action]。
     */
    public fun build(): MessageKeyboard.Action =
        MessageKeyboard.Action(
            permission = permission,
            data = data,
            reply = reply,
            enter = enter,
            anchor = anchor,
            unsupportTips = unsupportTips,
            type = type
        )
}

/**
 * [MessageKeyboard.ActionPermission] 的构建器。
 * @since 4.2.3
 */
@MessageKeyboardBuilderDsl
public class MessageKeyboardActionPermissionBuilder {
    public var type: Int = 2

    public var specifyUserIds: MutableList<String> = mutableListOf()

    public var specifyRoleIds: MutableList<String> = mutableListOf()

    /**
     * 基于现有 [permission] 填充当前构建器。
     */
    public fun from(permission: MessageKeyboard.ActionPermission): MessageKeyboardActionPermissionBuilder = also {
        type = permission.type
        specifyUserIds = permission.specifyUserIds?.toMutableList() ?: mutableListOf()
        specifyRoleIds = permission.specifyRoleIds?.toMutableList() ?: mutableListOf()
    }

    /**
     * 设置权限类型。
     */
    public fun type(type: Int): MessageKeyboardActionPermissionBuilder = also {
        this.type = type
    }

    /**
     * 添加一个可操作用户 ID。
     */
    public fun addSpecifyUserId(userId: String): MessageKeyboardActionPermissionBuilder = also {
        specifyUserIds.add(userId)
    }

    /**
     * 添加一个可操作身份组 ID。
     */
    public fun addSpecifyRoleId(roleId: String): MessageKeyboardActionPermissionBuilder = also {
        specifyRoleIds.add(roleId)
    }

    /**
     * 批量添加可操作用户 ID。
     */
    public fun addSpecifyUserIds(vararg userIds: String): MessageKeyboardActionPermissionBuilder = also {
        specifyUserIds.addAll(userIds)
    }

    /**
     * 批量添加可操作身份组 ID。
     */
    public fun addSpecifyRoleIds(vararg roleIds: String): MessageKeyboardActionPermissionBuilder = also {
        specifyRoleIds.addAll(roleIds)
    }

    /**
     * 批量添加可操作用户 ID。
     */
    public fun addSpecifyUserIds(userIds: List<String>): MessageKeyboardActionPermissionBuilder = also {
        specifyUserIds.addAll(userIds)
    }

    /**
     * 批量添加可操作身份组 ID。
     */
    public fun addSpecifyRoleIds(roleIds: List<String>): MessageKeyboardActionPermissionBuilder = also {
        specifyRoleIds.addAll(roleIds)
    }

    /**
     * 构建 [MessageKeyboard.ActionPermission]。
     */
    public fun build(): MessageKeyboard.ActionPermission =
        MessageKeyboard.ActionPermission(
            type = type,
            specifyUserIds = specifyUserIds.ifEmpty { null },
            specifyRoleIds = specifyRoleIds.ifEmpty { null }
        )
}
