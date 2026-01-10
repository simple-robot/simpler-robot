/*
 * Copyright (c) 2024-2025. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.component.qguild.message

import kotlinx.serialization.Serializable
import love.forte.simbot.message.Messages
import love.forte.simbot.qguild.api.message.GroupAndC2CSendBody
import love.forte.simbot.qguild.model.Message
import love.forte.simbot.qguild.model.Message.Markdown.Param
import love.forte.simbot.qguild.model.Message.Markdown.Params
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic


/**
 * Markdown 消息内容。
 *
 * @author ForteScarlet
 */
@Serializable
@ConsistentCopyVisibility
public data class QGMarkdown internal constructor(
    public val markdown: Message.Markdown
) : QGMessageElement {
    public companion object {
        /**
         * 使用 [markdown] 直接包装构建。
         */
        @JvmStatic
        public fun byMarkdown(markdown: Message.Markdown): QGMarkdown =
            QGMarkdown(markdown)

        /**
         * 使用 [content] 构建一个 [QGMarkdown]。
         */
        @JvmStatic
        public fun create(content: String): QGMarkdown =
            byMarkdown(Message.Markdown(content = content))

        /**
         * 使用 `templateId` 构建一个 [QGMarkdown]。
         *
         * @see Message.Markdown.createByTemplateId
         */
        @JvmStatic
        @Deprecated(
            "Use createByTemplateId(templateId, Param(...))",
            replaceWith = ReplaceWith(
                "createByTemplateId(templateId, params?.let { Param(it.key, it.values) })",
                "love.forte.simbot.qguild.model.Message.Markdown.Param"
            )
        )
        @Suppress("DEPRECATION")
        public fun createByTemplateId(templateId: Int, params: Params? = null): QGMarkdown =
            createByTemplateId(templateId, params?.let { Param(it.key, it.values) })

        /**
         * 使用 `templateId` 构建一个 [QGMarkdown]。
         *
         * @see Message.Markdown.createByTemplateId
         */
        @JvmStatic
        @JvmOverloads
        public fun createByTemplateId(templateId: Int, param: Param? = null): QGMarkdown =
            byMarkdown(Message.Markdown.createByTemplateId(templateId, param?.let { listOf(it) }))

        /**
         * 使用 `templateId` 构建一个 [QGMarkdown]。
         *
         * @see Message.Markdown.createByTemplateId
         */
        @JvmStatic
        public fun createByTemplateId(templateId: Int, params: List<Param>? = null): QGMarkdown =
            byMarkdown(Message.Markdown.createByTemplateId(templateId, params))

        /**
         * 使用 `customTemplateId` 构建一个 [QGMarkdown]。
         *
         * @see Message.Markdown.createByCustomTemplateId
         */
        @JvmStatic
        @Deprecated(
            "Use createByCustomTemplateId(customTemplateId, Param(...))",
            replaceWith = ReplaceWith(
                "createByCustomTemplateId(customTemplateId, " +
                        "params?.let { Param(it.key, it.values) })",
                "love.forte.simbot.qguild.model.Message.Markdown.Param"
            )
        )
        @Suppress("DEPRECATION")
        public fun createByCustomTemplateId(customTemplateId: String, params: Params? = null): QGMarkdown =
            createByCustomTemplateId(
                customTemplateId,
                param = params?.let { Param(it.key, it.values) }
            )

        /**
         * 使用 `customTemplateId` 构建一个 [QGMarkdown]。
         *
         * @see Message.Markdown.createByCustomTemplateId
         */
        @JvmStatic
        @JvmOverloads
        public fun createByCustomTemplateId(customTemplateId: String, param: Param? = null): QGMarkdown =
            byMarkdown(Message.Markdown.createByCustomTemplateId(
                customTemplateId,
                param?.let { listOf(it) }
            ))

        /**
         * 使用 `customTemplateId` 构建一个 [QGMarkdown]。
         *
         * @see Message.Markdown.createByCustomTemplateId
         */
        @JvmStatic
        public fun createByCustomTemplateId(customTemplateId: String, params: List<Param>? = null): QGMarkdown =
            byMarkdown(Message.Markdown.createByCustomTemplateId(customTemplateId, params))
    }
}

internal object MarkdownParser : SendingMessageParser {
    // TODO 连续Markdown content可叠加？

    override suspend fun invoke(
        index: Int,
        element: love.forte.simbot.message.Message.Element,
        messages: Messages?,
        builderContext: SendingMessageParser.BuilderContext
    ) {
        if (element is QGMarkdown) {
            val builder = builderContext.builderOrNew {
                it.markdown == null
            }
            builder.markdown = element.markdown
        }
    }

    private fun isTextOrMarkdown(type: Int): Boolean {
        return type == GroupAndC2CSendBody.MSG_TYPE_TEXT
                || type == GroupAndC2CSendBody.MSG_TYPE_MARKDOWN
    }

    override suspend fun invoke(
        index: Int,
        element: love.forte.simbot.message.Message.Element,
        messages: Messages?,
        builderContext: SendingMessageParser.GroupAndC2CBuilderContext
    ) {
        if (element is QGMarkdown) {
            val builder = builderContext.builderOrNew {
                isTextOrMarkdown(it.msgType) && it.markdown == null
            }
            builder.msgType = GroupAndC2CSendBody.MSG_TYPE_MARKDOWN
            builder.markdown = element.markdown
        }
    }
}
