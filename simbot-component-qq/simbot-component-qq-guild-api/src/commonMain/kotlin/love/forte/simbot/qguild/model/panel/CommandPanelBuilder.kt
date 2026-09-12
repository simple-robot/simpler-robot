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

import kotlin.jvm.JvmExposeBoxed


/**
 * 通过 DSL 构建 [CommandPanel]。
 *
 * 构建器不校验场景、元素类型与字段组合，由 QQ 服务端决定其有效性。
 *
 * @since 5.0
 */
public inline fun CommandPanel(block: CommandPanelBuilder.() -> Unit): CommandPanel =
    CommandPanelBuilder().also(block).build()

@Retention(AnnotationRetention.BINARY)
@DslMarker
internal annotation class CommandPanelBuilderDsl

/**
 * 用于构建 [CommandPanel] 的构建器。
 *
 * @since 5.0
 */
@CommandPanelBuilderDsl
public class CommandPanelBuilder {
    /**
     *  开发者备注。
     */
    public var remark: String? = null

    /**
     * 面板配置版本。
     */
    public var version: Int? = null

    private val items: MutableList<CommandPanel.Item> = mutableListOf()

    /**
     * 设置开发者备注。
     */
    public fun remark(remark: String?): CommandPanelBuilder = also {
        this.remark = remark
    }

    /**
     * 设置面板配置版本。
     */
    public fun version(version: Int?): CommandPanelBuilder = also {
        this.version = version
    }

    /**
     * 添加一个面板元素。
     */
    public fun addItem(item: CommandPanel.Item): CommandPanelBuilder = also {
        items.add(item)
    }

    /**
     * 批量添加面板元素。
     */
    public fun addItems(items: Iterable<CommandPanel.Item>): CommandPanelBuilder = also {
        this.items.addAll(items)
    }

    /**
     * 清空已添加的面板元素。
     */
    public fun clearItems(): CommandPanelBuilder = also {
        items.clear()
    }

    /**
     * 通过 DSL 构建并添加一个面板元素。
     */
    public fun item(block: CommandPanelItemBuilder.() -> Unit): CommandPanelBuilder = also {
        items.add(CommandPanelItemBuilder().apply(block).build())
    }

    /**
     * 构建 [CommandPanel]。
     */
    public fun build(): CommandPanel = CommandPanel(
        items = items.toList(),
        remark = remark,
        version = version,
    )
}

/**
 * 用于构建 [CommandPanel.Item] 的构建器。
 *
 * @since 5.0
 */
@OptIn(ExperimentalStdlibApi::class)
@CommandPanelBuilderDsl
public class CommandPanelItemBuilder {
    /**
     * 元素名称。
     */
    public var name: String? = null

    /**
     * 元素描述。
     */
    public var desc: String? = null

    /**
     * 元素类型。
     *
     * @since 5.0
     */
    @get:JvmExposeBoxed
    @set:JvmExposeBoxed
    public var type: CommandPanelItemType? = null

    /**
     * 是否仅管理员可操作。
     */
    public var onlyAdmin: Boolean? = null

    /**
     * 链接元素的跳转链接。
     */
    public var link: String? = null

    /**
     * 设置元素名称。
     */
    public fun name(name: String?): CommandPanelItemBuilder = also {
        this.name = name
    }

    /**
     * 设置元素描述。
     */
    public fun desc(desc: String?): CommandPanelItemBuilder = also {
        this.desc = desc
    }

    /**
     * 设置元素类型。
     *
     * @since 5.0
     */
    @JvmExposeBoxed
    public fun type(type: CommandPanelItemType?): CommandPanelItemBuilder = also {
        this.type = type
    }

    /**
     * 设置是否仅管理员可操作。
     */
    public fun onlyAdmin(onlyAdmin: Boolean?): CommandPanelItemBuilder = also {
        this.onlyAdmin = onlyAdmin
    }

    /**
     * 设置链接元素的跳转链接。
     */
    public fun link(link: String?): CommandPanelItemBuilder = also {
        this.link = link
    }

    /**
     * 构建 [CommandPanel.Item]。
     */
    public fun build(): CommandPanel.Item = CommandPanel.Item(
        name = name,
        desc = desc,
        type = type,
        onlyAdmin = onlyAdmin,
        link = link,
    )
}

/**
 * 用于构建 [CommandPanelCreate] 的构建器。
 *
 * @since 5.0
 */
@OptIn(ExperimentalStdlibApi::class)
@CommandPanelBuilderDsl
public class CommandPanelCreateBuilder {
    /**
     * 面板生效场景。
     *
     * @since 5.0
     */
    @get:JvmExposeBoxed
    @set:JvmExposeBoxed
    public var scope: CommandPanelScope? = null

    /**
     * 面板生效范围。
     *
     * @since 5.0
     */
    @get:JvmExposeBoxed
    @set:JvmExposeBoxed
    public var targetType: CommandPanelTargetType? = null

    /**
     * 面板配置。
     */
    public var panel: CommandPanel? = null

    private var userOpenids: MutableList<String> = mutableListOf()
    private var groupOpenids: MutableList<String> = mutableListOf()

    /**
     * 设置面板生效场景。
     *
     * @since 5.0
     */
    @JvmExposeBoxed
    public fun scope(scope: CommandPanelScope?): CommandPanelCreateBuilder = also {
        this.scope = scope
    }

    /**
     * 设置面板生效范围。
     *
     * @since 5.0
     */
    @JvmExposeBoxed
    public fun targetType(targetType: CommandPanelTargetType?): CommandPanelCreateBuilder = also {
        this.targetType = targetType
    }

    /**
     * 直接设置面板配置。
     */
    public fun panel(panel: CommandPanel?): CommandPanelCreateBuilder = also {
        this.panel = panel
    }

    /**
     * 通过 DSL 构建并设置面板配置。
     */
    public fun panel(block: CommandPanelBuilder.() -> Unit): CommandPanelCreateBuilder = also {
        panel = CommandPanelBuilder().apply(block).build()
    }

    /**
     * 设置 C2C 用户 OpenID 列表。
     */
    public fun addUserOpenids(userOpenids: Iterable<String>): CommandPanelCreateBuilder = also {
        this.userOpenids.addAll(userOpenids)
    }

    /**
     * 添加一个 C2C 用户 OpenID。
     */
    public fun addUserOpenid(userOpenid: String): CommandPanelCreateBuilder = also {
        userOpenids.add(userOpenid)
    }

    /**
     * 清空 C2C 用户 OpenID 并保留内部空列表。
     * 构建结果中的 `userOpenids` 为 `null`，而不是空数组；使用省略空值的序列化配置时，该字段会被省略。
     */
    public fun clearUserOpenids(): CommandPanelCreateBuilder = also {
        userOpenids.clear()
    }

    /**
     * 设置群 OpenID 列表。
     */
    public fun addGroupOpenids(groupOpenids: Iterable<String>): CommandPanelCreateBuilder = also {
        this.groupOpenids.addAll(groupOpenids)
    }

    /**
     * 添加一个群 OpenID。
     */
    public fun addGroupOpenid(groupOpenid: String): CommandPanelCreateBuilder = also {
        groupOpenids.add(groupOpenid)
    }

    /**
     * 清空群 OpenID 并保留内部空列表。
     * 构建结果中的 `groupOpenids` 为 `null`，而不是空数组；使用省略空值的序列化配置时，该字段会被省略。
     */
    public fun clearGroupOpenids(): CommandPanelCreateBuilder = also {
        groupOpenids.clear()
    }

    /**
     * 构建 [CommandPanelCreate]。
     */
    public fun build(): CommandPanelCreate = CommandPanelCreate(
        scope = scope,
        targetType = targetType,
        userOpenids = userOpenids.takeIf { it.isNotEmpty() }?.toList(),
        groupOpenids = groupOpenids.takeIf { it.isNotEmpty() }?.toList(),
        panel = panel,
    )
}

/**
 * 用于构建 [CommandPanelTargetUpdate] 的构建器。
 *
 * @since 5.0
 */
@OptIn(ExperimentalStdlibApi::class)
@CommandPanelBuilderDsl
public class CommandPanelTargetUpdateBuilder {
    /**
     * 关联操作类型。
     *
     * @since 5.0
     */
    @get:JvmExposeBoxed
    @set:JvmExposeBoxed
    public var op: CommandPanelTargetUpdateOp? = null

    private var userOpenids: MutableList<String> = mutableListOf()
    private var groupOpenids: MutableList<String> = mutableListOf()

    /**
     * 设置关联操作类型。
     *
     * @since 5.0
     */
    @JvmExposeBoxed
    public fun op(op: CommandPanelTargetUpdateOp?): CommandPanelTargetUpdateBuilder = also {
        this.op = op
    }

    /**
     * 设置 C2C 用户 OpenID 列表。
     */
    public fun addUserOpenids(userOpenids: Collection<String>): CommandPanelTargetUpdateBuilder = also {
        this.userOpenids.addAll(userOpenids)
    }

    /**
     * 添加一个 C2C 用户 OpenID。
     */
    public fun addUserOpenid(userOpenid: String): CommandPanelTargetUpdateBuilder = also {
        userOpenids.add(userOpenid)
    }

    /**
     * 清空 C2C 用户 OpenID 并保留内部空列表。
     * 构建结果中的 `userOpenids` 为 `null`，而不是空数组；使用省略空值的序列化配置时，该字段会被省略。
     */
    public fun clearUserOpenids(): CommandPanelTargetUpdateBuilder = also {
        userOpenids.clear()
    }

    /**
     * 设置群 OpenID 列表。
     */
    public fun addGroupOpenids(groupOpenids: Iterable<String>): CommandPanelTargetUpdateBuilder = also {
        this.groupOpenids.addAll(groupOpenids)
    }

    /**
     * 添加一个群 OpenID。
     */
    public fun addGroupOpenid(groupOpenid: String): CommandPanelTargetUpdateBuilder = also {
        groupOpenids.add(groupOpenid)
    }

    /**
     * 清空群 OpenID 并保留内部空列表。
     * 构建结果中的 `groupOpenids` 为 `null`，而不是空数组；使用省略空值的序列化配置时，该字段会被省略。
     */
    public fun clearGroupOpenids(): CommandPanelTargetUpdateBuilder = also {
        groupOpenids.clear()
    }

    /**
     * 构建 [CommandPanelTargetUpdate]。
     */
    public fun build(): CommandPanelTargetUpdate = CommandPanelTargetUpdate(
        op = op,
        userOpenids = userOpenids.takeIf { it.isNotEmpty() }?.toList(),
        groupOpenids = groupOpenids.takeIf { it.isNotEmpty() }?.toList(),
    )
}
