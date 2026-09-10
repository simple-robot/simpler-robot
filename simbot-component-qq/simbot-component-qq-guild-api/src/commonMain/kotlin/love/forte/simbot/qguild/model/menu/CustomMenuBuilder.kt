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
 * 通过 DSL 构建 [CustomMenu]。
 *
 * 构建器不校验菜单类型与字段组合，由 QQ 服务端决定其有效性。
 *
 * @since 4.7.0
 */
public inline fun CustomMenu(block: CustomMenuBuilder.() -> Unit): CustomMenu =
    CustomMenuBuilder().also(block).build()

@Retention(AnnotationRetention.BINARY)
@DslMarker
internal annotation class CustomMenuBuilderDsl

/**
 * 用于构建 [CustomMenu] 的构建器。
 *
 * @since 4.7.0
 */
@CustomMenuBuilderDsl
public class CustomMenuBuilder {
    private val items: MutableList<CustomMenu.Item> = mutableListOf()

    /**
     * 添加一个菜单项。
     */
    public fun addItem(item: CustomMenu.Item): CustomMenuBuilder = also {
        items.add(item)
    }

    /**
     * 批量添加菜单项。
     */
    public fun addItems(items: Iterable<CustomMenu.Item>): CustomMenuBuilder = also {
        this.items.addAll(items)
    }

    /**
     * 清空已添加的菜单项。
     */
    public fun clearItems(): CustomMenuBuilder = also {
        items.clear()
    }

    /**
     * 通过 DSL 构建并添加一个菜单项。
     */
    public fun item(block: CustomMenuItemBuilder.() -> Unit): CustomMenuBuilder = also {
        items.add(CustomMenuItemBuilder().apply(block).build())
    }

    /**
     * 构建 [CustomMenu]。
     */
    public fun build(): CustomMenu = CustomMenu(items = items.toList())
}

/**
 * 用于构建 [CustomMenu.Item] 的构建器。
 *
 * @since 4.7.0
 */
@CustomMenuBuilderDsl
public class CustomMenuItemBuilder {
    /**
     * 菜单项名称。
     */
    public var name: String? = null

    /**
     * 菜单项类型。
     */
    public var type: String? = null

    /**
     * 发送消息菜单项的消息内容。
     */
    public var sendMessage: String? = null

    /**
     * 链接菜单项的链接。
     */
    public var link: String? = null

    /**
     * 开关菜单项的开关配置。
     */
    public var switch: CustomMenu.Switch? = null

    private var subMenuItems: MutableList<CustomMenu.SubItem> = mutableListOf()

    /**
     * 设置菜单项名称。
     */
    public fun name(name: String?): CustomMenuItemBuilder = also {
        this.name = name
    }

    /**
     * 设置菜单项类型。
     */
    public fun type(type: String?): CustomMenuItemBuilder = also {
        this.type = type
    }

    /**
     * 设置发送消息菜单项的消息内容。
     */
    public fun sendMessage(sendMessage: String?): CustomMenuItemBuilder = also {
        this.sendMessage = sendMessage
    }

    /**
     * 设置链接菜单项的链接。
     */
    public fun link(link: String?): CustomMenuItemBuilder = also {
        this.link = link
    }

    /**
     * 直接设置开关配置。
     */
    public fun switch(switch: CustomMenu.Switch?): CustomMenuItemBuilder = also {
        this.switch = switch
    }

    /**
     * 通过参数设置开关配置。
     */
    public fun switch(switchId: String? = null, defaultValue: Boolean? = null): CustomMenuItemBuilder = also {
        switch = CustomMenu.Switch(switchId = switchId, defaultValue = defaultValue)
    }

    /**
     * 通过 DSL 设置开关配置。
     */
    public fun switch(block: CustomMenuSwitchBuilder.() -> Unit): CustomMenuItemBuilder = also {
        switch = CustomMenuSwitchBuilder().apply(block).build()
    }

    /**
     * 直接设置二级菜单项。
     */
    public fun addSubMenuItems(subMenuItems: Iterable<CustomMenu.SubItem>): CustomMenuItemBuilder = also {
        this.subMenuItems.addAll(subMenuItems)
    }

    /**
     * 添加一个二级菜单项。
     */
    public fun addSubMenuItem(item: CustomMenu.SubItem): CustomMenuItemBuilder = also {
        subMenuItems.add(item)
    }

    /**
     * 通过 DSL 构建并添加一个二级菜单项。
     */
    public fun subMenuItem(block: CustomMenuSubItemBuilder.() -> Unit): CustomMenuItemBuilder = also {
        subMenuItems.add(CustomMenuSubItemBuilder().apply(block).build())
    }

    /**
     * 清空二级菜单项并保留内部空列表。
     * 构建结果中的 `subMenu` 为 `null`，而不是空数组；使用省略空值的序列化配置时，该字段会被省略。
     */
    public fun clearSubMenuItems(): CustomMenuItemBuilder = also {
        subMenuItems.clear()
    }

    /**
     * 构建 [CustomMenu.Item]。
     */
    public fun build(): CustomMenu.Item = CustomMenu.Item(
        name = name,
        type = type,
        subMenuItems = subMenuItems.takeIf { it.isNotEmpty() }?.toList(),
        sendMessage = sendMessage,
        link = link,
        switch = switch,
    )
}

/**
 * 用于构建 [CustomMenu.SubItem] 的构建器。
 *
 * @since 4.7.0
 */
@CustomMenuBuilderDsl
public class CustomMenuSubItemBuilder {
    /**
     * 二级菜单项名称。
     */
    public var name: String? = null

    /**
     * 二级菜单项类型。
     */
    public var type: String? = null

    /**
     * 发送消息菜单项的消息内容。
     */
    public var sendMessage: String? = null

    /**
     * 链接菜单项的链接。
     */
    public var link: String? = null

    /**
     * 设置二级菜单项名称。
     */
    public fun name(name: String?): CustomMenuSubItemBuilder = also {
        this.name = name
    }

    /**
     * 设置二级菜单项类型。
     */
    public fun type(type: String?): CustomMenuSubItemBuilder = also {
        this.type = type
    }

    /**
     * 设置发送消息菜单项的消息内容。
     */
    public fun sendMessage(sendMessage: String?): CustomMenuSubItemBuilder = also {
        this.sendMessage = sendMessage
    }

    /**
     * 设置链接菜单项的链接。
     */
    public fun link(link: String?): CustomMenuSubItemBuilder = also {
        this.link = link
    }

    /**
     * 构建 [CustomMenu.SubItem]。
     */
    public fun build(): CustomMenu.SubItem = CustomMenu.SubItem(
        name = name,
        type = type,
        sendMessage = sendMessage,
        link = link,
    )
}

/**
 * 用于构建 [CustomMenu.Switch] 的构建器。
 *
 * @since 4.7.0
 */
@CustomMenuBuilderDsl
public class CustomMenuSwitchBuilder {
    /**
     * 开关唯一标识。
     */
    public var switchId: String? = null

    /**
     * 开关初始状态。
     */
    public var defaultValue: Boolean? = null

    /**
     * 设置开关唯一标识。
     */
    public fun switchId(switchId: String?): CustomMenuSwitchBuilder = also {
        this.switchId = switchId
    }

    /**
     * 设置开关初始状态。
     */
    public fun defaultValue(defaultValue: Boolean?): CustomMenuSwitchBuilder = also {
        this.defaultValue = defaultValue
    }

    /**
     * 构建 [CustomMenu.Switch]。
     */
    public fun build(): CustomMenu.Switch = CustomMenu.Switch(
        switchId = switchId,
        defaultValue = defaultValue,
    )
}
