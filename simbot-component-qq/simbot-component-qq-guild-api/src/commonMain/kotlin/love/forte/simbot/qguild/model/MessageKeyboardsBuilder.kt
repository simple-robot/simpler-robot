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

/**
 * 通过 DSL 构建 [MessageKeyboards]。
 *
 * @since 4.4.0
 */
public inline fun MessageKeyboards(block: MessageKeyboardsBuilder.() -> Unit): MessageKeyboards =
    MessageKeyboardsBuilder().also(block).build()

@Retention(AnnotationRetention.BINARY)
@DslMarker
internal annotation class MessageKeyboardsBuilderDsl

/**
 * 用于更便捷构建 [MessageKeyboards] 的构建器，Java 中可用链式 API，Kotlin 额外提供一些 DSL API。
 *
 * @since 4.4.0
 * @author ForteScarlet
 */
@MessageKeyboardsBuilderDsl
public class MessageKeyboardsBuilder {
    private var content: MessageKeyboards.Content = EMPTY_CONTENT

    /**
     * 直接设置 [MessageKeyboards.Content]。
     */
    public fun content(content: MessageKeyboards.Content): MessageKeyboardsBuilder = also {
        this.content = content
    }

    /**
     * 通过 DSL 构建并设置 [MessageKeyboards.Content]。
     */
    public fun content(builder: MessageKeyboardsContentBuilder.() -> Unit): MessageKeyboardsBuilder = also {
        this.content = MessageKeyboardsContentBuilder().apply(builder).build()
    }

    /**
     * 构建 [MessageKeyboards]。
     */
    public fun build(): MessageKeyboards = MessageKeyboards(content)

    public companion object {
        private val EMPTY_CONTENT = MessageKeyboards.Content(rows = emptyList())
    }
}

/**
 * 用于构建 [MessageKeyboards.Content] 的构建器。
 *
 * @since 4.4.0
 * @author ForteScarlet
 */
@MessageKeyboardsBuilderDsl
public class MessageKeyboardsContentBuilder {
    private val rows = mutableListOf<MessageKeyboards.ContentRow>()

    /**
     * 添加一行按钮内容。
     */
    public fun addRow(contentRow: MessageKeyboards.ContentRow): MessageKeyboardsContentBuilder = also {
        rows.add(contentRow)
    }

    /**
     * 批量添加多行按钮内容。
     */
    public fun addRows(contentRows: List<MessageKeyboards.ContentRow>): MessageKeyboardsContentBuilder = also {
        rows.addAll(contentRows)
    }

    /**
     * 批量添加多行按钮内容。
     */
    public fun addRows(vararg contentRows: MessageKeyboards.ContentRow): MessageKeyboardsContentBuilder = also {
        rows.addAll(contentRows)
    }

    /**
     * 清空已添加的所有行。
     */
    public fun clearRows(): MessageKeyboardsContentBuilder = also {
        rows.clear()
    }

    /**
     * 通过 DSL 构建并添加一行按钮内容。
     */
    public fun row(builder: MessageKeyboardsContentRowBuilder.() -> Unit): MessageKeyboardsContentBuilder = also {
        rows.add(MessageKeyboardsContentRowBuilder().apply(builder).build())
    }

    /**
     * 构建 [MessageKeyboards.Content]。
     */
    public fun build(): MessageKeyboards.Content = MessageKeyboards.Content(rows = rows.toList())
}

/**
 * 用于构建 [MessageKeyboards.ContentRow] 的构建器。
 *
 * @since 4.4.0
 * @author ForteScarlet
 */
@MessageKeyboardsBuilderDsl
public class MessageKeyboardsContentRowBuilder {
    private val buttons = mutableListOf<MessageKeyboardButton>()

    /**
     * 添加一个按钮。
     */
    public fun addButton(button: MessageKeyboardButton): MessageKeyboardsContentRowBuilder = also {
        buttons.add(button)
    }

    /**
     * 批量添加多个按钮。
     */
    public fun addButtons(buttons: List<MessageKeyboardButton>): MessageKeyboardsContentRowBuilder = also {
        this.buttons.addAll(buttons)
    }

    /**
     * 批量添加多个按钮。
     */
    public fun addButtons(vararg buttons: MessageKeyboardButton): MessageKeyboardsContentRowBuilder = also {
        this.buttons.addAll(buttons)
    }

    /**
     * 清空已添加的所有按钮。
     */
    public fun clearButtons(): MessageKeyboardsContentRowBuilder = also {
        buttons.clear()
    }

    /**
     * 通过 DSL 构建并添加一个按钮。
     */
    public fun button(builder: MessageKeyboardBuilder.() -> Unit): MessageKeyboardsContentRowBuilder = also {
        buttons.add(MessageKeyboardBuilder().apply(builder).build())
    }

    /**
     * 构建 [MessageKeyboards.ContentRow]。
     */
    public fun build(): MessageKeyboards.ContentRow = MessageKeyboards.ContentRow(buttons = buttons.toList())
}
