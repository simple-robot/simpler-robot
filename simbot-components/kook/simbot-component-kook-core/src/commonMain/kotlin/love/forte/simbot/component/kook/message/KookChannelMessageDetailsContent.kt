/*
 *     Copyright (c) 2022-2026. ForteScarlet.
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

package love.forte.simbot.component.kook.message

import io.ktor.http.*
import love.forte.simbot.ability.DeleteFailureException
import love.forte.simbot.ability.DeleteOption
import love.forte.simbot.ability.StandardDeleteOption.Companion.standardAnalysis
import love.forte.simbot.ability.isIgnoreOnFailure
import love.forte.simbot.ability.isIgnoreOnNoSuchTarget
import love.forte.simbot.common.exception.initExceptionCause
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.component.kook.bot.KookBot
import love.forte.simbot.component.kook.message.KookAttachmentMessage.Companion.asMessage
import love.forte.simbot.component.kook.message.KookQuote.Companion.asMessage
import love.forte.simbot.component.kook.util.requestDataBy
import love.forte.simbot.component.kook.util.requestResultBy
import love.forte.simbot.kook.api.ApiResponseException
import love.forte.simbot.kook.api.message.DeleteChannelMessageApi
import love.forte.simbot.kook.api.message.GetChannelMessageViewApi
import love.forte.simbot.kook.messages.ChannelMessageDetails
import love.forte.simbot.kook.messages.MessageType
import love.forte.simbot.logger.LoggerFactory
import love.forte.simbot.logger.logger
import love.forte.simbot.message.Messages
import love.forte.simbot.message.PlainText
import love.forte.simbot.message.toText
import kotlin.jvm.JvmStatic
import kotlin.jvm.JvmSynthetic

/**
 * 将 [ChannelMessageDetails] 作为消息正文实现。
 *
 * @see ChannelMessageDetails
 * @see GetChannelMessageViewApi
 * @author ForteScarlet
 */
@ConsistentCopyVisibility
public data class KookChannelMessageDetailsContent internal constructor(
    internal val details: ChannelMessageDetails,
    private val bot: KookBot,
) : KookMessageContent {
    /**
     * 消息ID。
     */
    override val id: ID
        get() = details.id.ID

    /**
     * 得到当前消息正文中的原始 [ChannelMessageDetails] 信息。
     */
    public val sourceDetails: ChannelMessageDetails
        get() = details

    /**
     * 得到消息详情原始的正文信息。
     * @see sourceDetails
     * @see ChannelMessageDetails.content
     */
    override val rawContent: String
        get() = sourceDetails.content


    /**
     * Kook 消息事件中所收到的消息链。
     *
     * _此消息链的约束、规则与各项注意事项与 [KookReceiveMessageContent.messages] 基本一致，参考其文档说明来了解更多。_
     *
     */
    override val messages: Messages by lazy(LazyThreadSafetyMode.PUBLICATION) { details.toMessages() }

    /**
     * [messages] 中所有的 [PlainText] 的合并结果
     */
    override val plainText: String by lazy(LazyThreadSafetyMode.PUBLICATION) {
        messages.filterIsInstance<PlainText>().joinToString("") { it.text }
    }

    @JvmSynthetic
    override suspend fun reference(): KookQuote? {
        val details = GetChannelMessageViewApi.create(details.id).requestDataBy(bot)
        return details.quote?.asMessage()
    }

    @JvmSynthetic
    override suspend fun referenceMessage(): KookMessageContent? {
        val quote = details.quote ?: return null
        val view = GetChannelMessageViewApi.create(quote.id).requestDataBy(bot)
        return view.toContent(bot)
    }

    /**
     * 删除当前的频道消息。
     *
     * 通过 [DeleteChannelMessageApi] 删除消息。
     * 会抛出请求 [DeleteChannelMessageApi] 过程中可能出现的任何异常。
     *
     */
    @JvmSynthetic
    override suspend fun delete(vararg options: DeleteOption) {
        val stdOpts = options.standardAnalysis()
        val result = try {
            DeleteChannelMessageApi.create(details.id).requestResultBy(bot)
        } catch (respEx: ApiResponseException) {
            val httpStatus = respEx.response.status
            if (httpStatus.value == HttpStatusCode.NotFound.value) {
                if (stdOpts.isIgnoreOnNoSuchTarget) {
                    return
                }

                throw NoSuchElementException("Delete channel message (details.id=${details.id}) not found: HTTP response status 404").also {
                    it.initExceptionCause(respEx)
                }
            }

            // other ex
            if (stdOpts.isIgnoreOnFailure) {
                return
            }

            throw DeleteFailureException("Delete channel message(details.id=${details.id}) on failure: ${respEx.message}", respEx)
        }

        if (result.isSuccess || stdOpts.isIgnoreOnFailure) {
            return
        }

        throw DeleteFailureException("Delete channel message(details.id=${details.id}) on failure with result: $result")
    }

    override fun toString(): String = "KookChannelMessageDetailsContent(details=$details)"

    public companion object {
        private val logger = LoggerFactory.logger<KookChannelMessageDetailsContent>()

        /**
         * 使用消息事件并将其中的消息内容转化为 [Messages].
         */
        @JvmStatic
        public fun ChannelMessageDetails.toMessages(): Messages {
            val initialList = when (type) {
                MessageType.IMAGE.type, MessageType.VIDEO.type, MessageType.FILE.type -> {
                    attachments?.asMessage()?.let { listOf(it) } ?: emptyList()
                }

                MessageType.KMARKDOWN.type -> {
                    return toMessagesByKMarkdown(content, mention, mentionRoles, isMentionAll, isMentionHere)
                }

                MessageType.CARD.type -> {
                    tryDecodeCardContent(content, logger)
                }

                else -> listOf(content.toText())
            }

            return toMessages(
                initialList,
                mention, mentionRoles, isMentionAll, isMentionHere
            )
        }

        /**
         * 使用消息事件并将其中的消息内容转化为 [KookChannelMessageDetailsContent].
         */
        @JvmStatic
        public fun ChannelMessageDetails.toContent(bot: KookBot): KookChannelMessageDetailsContent =
            KookChannelMessageDetailsContent(this, bot)
    }

}

