/*
 * Copyright (c) 2022-2024. ForteScarlet.
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

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.literal
import love.forte.simbot.message.At
import love.forte.simbot.message.AtAll
import love.forte.simbot.message.Message
import love.forte.simbot.message.Messages

/**
 * 在QQ频道中AT（提及）一个子频道
 *
 * Deprecated: 直接使用 [At] 并使其 [type][At.type] 为 [`"channel"`][love.forte.simbot.component.qguild.QQGuildComponent.AT_CHANNEL_TYPE] 即可。
 *
 */
@SerialName("qg.atChannel")
@Serializable
@Deprecated(
    "Just use At(target, type = QQGuildComponent.AT_CHANNEL_TYPE)",
    replaceWith = ReplaceWith(
        "At(target, type = QQGuildComponent.AT_CHANNEL_TYPE)",
        "love.forte.simbot.message.At", "love.forte.simbot.component.qguild.QQGuildComponent"
    )
)
@QGSendOnly
public data class QGAtChannel(public val target: ID) : QGMessageElement

internal object MentionParser : SendingMessageParser {

    @Suppress("DEPRECATION")
    override suspend fun invoke(
        index: Int,
        element: Message.Element,
        messages: Messages?,
        builderContext: SendingMessageParser.BuilderContext
    ) {
        // https://bot.q.qq.com/wiki/develop/api/openapi/message/message_format.html

        /*
         *  解析为 #子频道 标签，点击可以跳转至子频道，仅支持当前频道内的子频道
         */
        fun atChannel(id: ID) {
            builderContext.builder.appendContent("<#$id>")
        }

        when (element) {
            is At -> {
                if (element.type == "channel") {
                    atChannel(element.target)
                } else {
                    // 解析为 @用户 标签
                    // builderContext.builder.appendContent("<@${element.target}>")
                    // 嵌入文本使用格式：<qqbot-at-user id="" /> 协议：<@userid>即将弃用，请使用上述最新格式。
                    // see https://bot.q.qq.com/wiki/develop/api-v2/server-inter/message/trans/text-chain.html
                    // TODO 2024-07-13 经测试，新格式尚无法使用
//                    builderContext.builder.appendContent(toQQBotAtUser(element.target.literal))
                    builderContext.builder.appendContent(toOldQQBotAtUser(element.target.literal))
                }
            }

            is AtAll -> {
                // 解析为 @所有人 标签，需要机器人拥有发送 @所有人 消息的权限
                // builderContext.builder.appendContent("@everyone")
                // 嵌入文本使用格式：<qqbot-at-everyone /> 协议：@everyone即将弃用，请使用上述最新格式。
                // see https://bot.q.qq.com/wiki/develop/api-v2/server-inter/message/trans/text-chain.html
                // TODO 2024-07-13 经测试，新格式尚无法使用
//                builderContext.builder.appendContent(QQ_BOT_AT_EVERYONE)
                builderContext.builder.appendContent(OLD_QQ_BOT_AT_EVERYONE)
            }

            is QGAtChannel -> {
                atChannel(element.target)
            }
        }
    }

    override suspend fun invoke(
        index: Int,
        element: Message.Element,
        messages: Messages?,
        builderContext: SendingMessageParser.GroupAndC2CBuilderContext
    ) {
        when (element) {
            is At -> {
                // 解析为 @用户 标签
                // builderContext.builder.appendContent("<@${element.target}>")
                // 嵌入文本使用格式：<qqbot-at-user id="" /> 协议：<@userid>即将弃用，请使用上述最新格式。
                // see https://bot.q.qq.com/wiki/develop/api-v2/server-inter/message/trans/text-chain.html
                // TODO 2024-07-13 群聊似乎不能解析at
                builderContext.builder.content += toQQBotAtUser(element.target.literal)
            }

            is AtAll -> {
                // 解析为 @所有人 标签，需要机器人拥有发送 @所有人 消息的权限
                // builderContext.builder.appendContent("@everyone")
                // 嵌入文本使用格式：<qqbot-at-everyone /> 协议：@everyone即将弃用，请使用上述最新格式。
                // see https://bot.q.qq.com/wiki/develop/api-v2/server-inter/message/trans/text-chain.html
                // TODO 2024-07-13 群聊似乎不能解析at
                builderContext.builder.content += QQ_BOT_AT_EVERYONE
            }
        }
    }
}
