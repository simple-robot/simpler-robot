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

import kotlinx.serialization.SerializationException
import love.forte.simbot.ability.DeleteOption
import love.forte.simbot.ability.DeleteSupport
import love.forte.simbot.annotations.ExperimentalSimbotAPI
import love.forte.simbot.common.collection.computeValue
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.IntID.Companion.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.component.kook.bot.KookBot
import love.forte.simbot.component.kook.message.KookAttachmentMessage.Companion.asMessage
import love.forte.simbot.component.kook.message.KookChannelMessageDetailsContent.Companion.toContent
import love.forte.simbot.component.kook.message.KookMessages.AT_TYPE_ROLE
import love.forte.simbot.component.kook.message.KookMessages.AT_TYPE_USER
import love.forte.simbot.component.kook.message.KookQuote.Companion.asMessage
import love.forte.simbot.component.kook.util.requestData
import love.forte.simbot.component.kook.util.requestDataBy
import love.forte.simbot.component.kook.util.requestResultBy
import love.forte.simbot.component.kook.util.walk
import love.forte.simbot.kook.api.ApiResponseException
import love.forte.simbot.kook.api.ApiResult
import love.forte.simbot.kook.api.ApiResultException
import love.forte.simbot.kook.api.message.DeleteChannelMessageApi
import love.forte.simbot.kook.api.message.DeleteDirectMessageApi
import love.forte.simbot.kook.api.message.GetChannelMessageViewApi
import love.forte.simbot.kook.api.message.GetDirectMessageViewApi
import love.forte.simbot.kook.api.userchat.CreateUserChatApi
import love.forte.simbot.kook.event.*
import love.forte.simbot.kook.messages.ChannelMessageDetails
import love.forte.simbot.kook.messages.DirectMessageDetails
import love.forte.simbot.kook.objects.card.CardMessage
import love.forte.simbot.logger.Logger
import love.forte.simbot.logger.LoggerFactory
import love.forte.simbot.message.*
import love.forte.simbot.suspendrunner.STP
import kotlin.experimental.and
import kotlin.experimental.or
import kotlin.jvm.JvmSynthetic

/**
 * KOOK 中的消息正文类型的抽象接口类型。
 *
 * 在 Kook 中，以接收到的消息事件为例，
 * 一个完整的接收消息所得到 [messages] 中的元素可能是经过拆解/解析/冗余、扩展的。
 * 如果你希望直接根据真实消息进行**复读**，则请直接使用 [KookReceiveMessageContent]
 * 本身作为消息发送的实体而不是获取其中的 [messages]；同样的原因，如果你需要先对消息进行处理，
 * 请注意消息链中的冗余消息。
 *
 * ### DeleteSupport
 *
 * 存在消息ID即可以被删除，因此 [KookMessageContent] 支持 [delete] 操作，
 * 不过可能需要考虑权限问题。
 *
 * @author ForteScarlet
 */
public interface KookMessageContent : MessageContent, DeleteSupport {
    /**
     * 消息ID。
     */
    override val id: ID

    /**
     * raw content
     */
    public val rawContent: String

    /**
     * KOOK 消息事件中所收到的消息列表。
     *
     * [messages] 会对接收到的事件中的内容进行解析，并转化为各符合内容的消息链。
     *
     * ## [Event.content] 的处理
     * 当 [Event.extra] 为 [KMarkdownEventExtra] 类型时，[Event.content] 会在被追加的时候进行 **处理**。
     * 所谓处理，就是指根据 [TextExtra] 中的 [mention][TextExtra.mention]、
     * [mentionRoles][TextExtra.mentionRoles]、[isMentionAll][TextExtra.isMentionAll]、
     * [isMentionHere][TextExtra.isMentionHere] 的信息，根据各信息出现数量依次移除 [Event.content]
     * 中出现的特殊字符串。
     *
     * 例如，一个消息为：
     * ```
     * @forliy hello
     * ```
     *
     * 则 [Event.content] 的实际内容为：
     * ```
     * (met)123456(met) hello
     * ```
     *
     * 而由于 [TextExtra.mention] 出现过一次上述的 `123456` 的信息，因此依次移除掉一个 `(met)123456(met)`，
     * 而最终的 [MessageContent.plainText] 结果则为：
     *
     * ```
     *  hello
     * ```
     *
     * 需要注意的是，这种处理不会移除或清理任何的**空字符**，所以你可能会发现上面处理结束后的 ` hello` 前是有一个空格的。
     *
     * 如果你希望能够得到最原始的 [Event.content]，那么请通过原始事件对象（如果有） 或 [rawContent] 获取，
     * 而不是通过 [MessageContent.plainText] 或 [messages] 中的 [PlainText] 集。
     *
     * ```kotlin
     * val event: KookMessageEvent = ... // 一个KOOK的消息事件
     * val rawContent = event.sourceEvent.content
     * // ...
     * ```
     *
     * ## 转化丢失
     * 将消息转化为一个消息链（尤其是转化kmarkdown类型的消息）的过程中有可能会丢失一部分原有的格式。
     * 因此当你直接通过 [messages] 重复发送消息时有可能会产生与收到的消息不一致的效果。
     *
     * ## 消息引用
     * 有关消息引用的信息需要通过 [reference] API 查询获取，不会被包含在 [messages] 中。
     *
     */
    override val messages: Messages

    /**
     * 获取消息中的引用信息。
     * 会通过 API [GetChannelMessageViewApi]
     * 或 [GetDirectMessageViewApi] 发起请求并得到结果，
     * 因此 [reference] 会产生挂起。
     *
     * [reference] 的结果不会包含在 [messages] 中。
     *
     * 如果是私聊会话，会先查询会话code，然后查询消息引用。
     *
     * 查询结果不会被缓存，每次调用都会产生API请求。
     *
     * @throws SerializationException 由于服务端响应值类型不规范导致的异常。
     * 2024/8/6: 经测试，在私聊会话情况下，如果发送的消息内无引用，则响应的 `quote` 值为 **空字符串**，
     * 例如：
     * ```json
     * {"quote":""}
     * ```
     * 这会引发 [SerializationException]。而如果消息包含引用，则响应为 `Quote` 对象结构。
     * 虽然在内部做了写兼容性的处理，但是不能保证未来服务端的行为不会发生变化。
     *
     * @throws ApiResponseException 请求结果的状态码不是 200..300 之间
     * @throws ApiResultException 请求结果的 [ApiResult.code] 校验失败
     */
    @STP
    override suspend fun reference(): KookQuote?

    /**
     * 根据 [消息引用][reference] 查询其对应的消息原内容。
     */
    @STP
    override suspend fun referenceMessage(): KookMessageContent?

    /**
     * 尝试根据当前消息ID删除目标。
     *
     * @throws ApiResponseException 请求结果的状态码不是 200..300 之间
     */
    @JvmSynthetic
    override suspend fun delete(vararg options: DeleteOption)
}

/**
 * KOOK 中通过事件接收得到的消息正文类型的抽象类型。
 *
 * @see KookMessageContent
 */
public abstract class BaseKookReceiveMessageContent : KookMessageContent


private suspend fun referenceFromChannel(bot: KookBot, msgId: String): KookQuote? {
    val api = GetChannelMessageViewApi.create(msgId)
    val details = bot.requestData(api)
    return details.quote?.asMessage()
}

private suspend fun referenceFromDirect(bot: KookBot, msgId: String, authorId: String): KookQuote? {
    val chat = CreateUserChatApi.create(authorId).requestDataBy(bot)
    return referenceFromDirectWithChatCode(bot, msgId, chat.code)
}

private suspend fun referenceFromDirectWithChatCode(bot: KookBot, msgId: String, chatCode: String): KookQuote? {
    val details = GetDirectMessageViewApi.create(chatCode, msgId).requestDataBy(bot)
    return details.quote?.asMessage()
}

private suspend fun detailsFromChannel(bot: KookBot, msgId: String): ChannelMessageDetails {
    val api = GetChannelMessageViewApi.create(msgId)
    return bot.requestData(api)
}

private suspend fun detailsFromDirect(bot: KookBot, msgId: String, authorId: String): DirectMessageDetails {
    val chat = CreateUserChatApi.create(authorId).requestDataBy(bot)
    return detailsFromDirectWithChatCode(bot, msgId, chat.code)
}

private suspend fun detailsFromDirectWithChatCode(bot: KookBot, msgId: String, chatCode: String): DirectMessageDetails {
    return GetDirectMessageViewApi.create(chatCode, msgId).requestDataBy(bot)
}

/**
 * KOOK 消息事件所收到的消息正文类型。
 *
 * 更多有关消息正文的统一性描述参考 [KookMessageContent] 的说明。
 *
 * @see KookMessageContent
 *
 * @author ForteScarlet
 */
public class KookReceiveMessageContent internal constructor(
    private val isDirect: Boolean,
    internal val source: Event<TextExtra>,
    private val bot: KookBot,
) : BaseKookReceiveMessageContent() {
    override val id: ID
        get() = source.msgId.ID

    /**
     * 消息接收到的原始消息内容。
     */
    @Suppress("MemberVisibilityCanBePrivate")
    public val sourceEvent: Event<TextExtra> get() = source

    /**
     * 得到消息的原始正文信息。同 `sourceEvent.content`。
     *
     * @see sourceEvent
     * @see Event.content
     */
    @Suppress("MemberVisibilityCanBePrivate")
    override val rawContent: String get() = source.content

    override val messages: Messages by lazy(LazyThreadSafetyMode.PUBLICATION) { source.toMessages() }

    override val plainText: String by lazy(LazyThreadSafetyMode.PUBLICATION) {
        messages.filterIsInstance<PlainText>().joinToString("") { it.text }
    }

    @JvmSynthetic
    override suspend fun reference(): KookQuote? {
        return if (isDirect) {
            referenceFromDirect(bot, source.msgId, source.authorId)
        } else {
            referenceFromChannel(bot, source.msgId)
        }
    }

    @JvmSynthetic
    override suspend fun referenceMessage(): KookMessageContent? {
        return if (isDirect) {
            val ref = referenceFromDirect(bot, source.msgId, source.authorId) ?: return null
            val details = detailsFromDirect(bot, ref.quote.id, source.authorId)
            KookUpdatedMessageContent(
                bot = bot,
                isDirect = false,
                chatCode = null,
                rawContent = details.content,
                msgId = details.id,
                mention = emptyList(),
                mentionRoles = emptyList(),
                isMentionAll = false,
                isMentionHere = false
            )
        } else {
            val ref = referenceFromChannel(bot, source.msgId) ?: return null
            val details = detailsFromChannel(bot, ref.quote.id)
            details.toContent(bot)
        }
    }

    @JvmSynthetic
    override suspend fun delete(vararg options: DeleteOption) {
        // TODO options
        val api = if (isDirect) {
            DeleteDirectMessageApi.create(source.msgId)
        } else {
            DeleteChannelMessageApi.create(source.msgId)
        }

        // TODO delete failed
        api.requestResultBy(bot).isSuccess
    }

    override fun toString(): String {
        return "KookReceiveMessageContent(sourceEvent=$sourceEvent)"
    }
}

/**
 * KOOK 中消息更新等非消息事件推送得到的消息正文。
 *
 * 更多有关消息正文的统一性描述参考 [KookMessageContent] 的说明。
 *
 * @see KookMessageContent
 */
public class KookUpdatedMessageContent internal constructor(
    private val bot: KookBot,
    private val isDirect: Boolean,
    private val chatCode: String? = null,
    override val rawContent: String,
    private val msgId: String,
    private val mention: List<String>,
    private val mentionRoles: List<Int>,
    private val isMentionAll: Boolean,
    private val isMentionHere: Boolean,
) : BaseKookReceiveMessageContent() {
    init {
        check(!isDirect || chatCode != null) {
            "If is direct, chatCode can not be null"
        }
    }

    override val id: ID get() = msgId.ID

    override val messages: Messages by lazy(LazyThreadSafetyMode.PUBLICATION) {
        toMessagesByKMarkdown(rawContent, mention, mentionRoles, isMentionAll, isMentionHere)
    }

    override val plainText: String by lazy(LazyThreadSafetyMode.PUBLICATION) {
        messages.filterIsInstance<PlainText>().joinToString("") { it.text }
    }

    @JvmSynthetic
    override suspend fun reference(): KookQuote? {
        return if (isDirect) {
            referenceFromDirectWithChatCode(bot, msgId, chatCode!!)
        } else {
            referenceFromChannel(bot, msgId)
        }
    }

    @JvmSynthetic
    override suspend fun referenceMessage(): KookMessageContent? {
        return if (isDirect) {
            val chatCode = chatCode!!
            val ref = referenceFromDirectWithChatCode(bot, msgId, chatCode) ?: return null
            val details = detailsFromDirectWithChatCode(bot, ref.quote.id, chatCode)
            KookUpdatedMessageContent(
                bot = bot,
                isDirect = false,
                chatCode = null,
                rawContent = details.content,
                msgId = details.id,
                mention = emptyList(),
                mentionRoles = emptyList(),
                isMentionAll = false,
                isMentionHere = false
            )
        } else {
            val ref = referenceFromChannel(bot, msgId) ?: return null
            val details = detailsFromChannel(bot, ref.quote.id)
            details.toContent(bot)
        }
    }

    @JvmSynthetic
    override suspend fun delete(vararg options: DeleteOption) {
        // TODO options
        val api = if (isDirect) {
            DeleteDirectMessageApi.create(msgId)
        } else {
            DeleteChannelMessageApi.create(msgId)
        }

        // TODO delete failed
        api.requestResultBy(bot).isSuccess
    }
}


private val logger = LoggerFactory.getLogger("love.forte.simbot.component.kook.message.ReceiveMessageContent")

/**
 * 将消息事件相关内容转化为 [Messages].
 *
 * | Type of [EventExtra] | 转化规则 |
 * | --- | --- |
 * | [TextEventExtra] | 将 [Event.content] 转化为 [Text] |
 * | [ImageEventExtra] | 将 [ImageEventExtra.attachments] 转化为 [KookAttachmentMessage] |
 * | [VideoEventExtra] | 将 [VideoEventExtra.attachments] 转化为 [KookAttachmentMessage] |
 * | [CardEventExtra] | 尝试将 [Event.content] 转化为 [KookCardMessage]，如果失败则会输出相关日志并将 [Event.content] 转化为 [Text] |
 * | [KMarkdownEventExtra] | 将 [Event.content] 转化为 [Text]（会对特殊内容进行适当的移除）；<br /> 追加其他由 [mention][TextExtra.mention]、[mentionRoles][TextExtra.mentionRoles]、[isMentionAll][TextExtra.isMentionAll]、[isMentionHere][TextExtra.isMentionHere] 等信息而解析出来的 [At]、[AtAll]、[KookAtAllHere] 等信息。其中，[AtAll]、[KookAtAllHere] 至多只会各自出现一次。 |
 * | 其他 | 将 [Event.content] 转化为 [Text] |
 *
 */
public fun Event<TextExtra>.toMessages(): Messages {
    return when (val extra = extra) {
        is TextEventExtra -> {
            extra.toMessages { listOf(content.toText()) }
        }

        is ImageEventExtra -> {
            extra.toMessages { listOf(extra.attachments.asMessage()) }
        }

        is VideoEventExtra -> {
            extra.toMessages { listOf(extra.attachments.asMessage()) }
        }

        is KMarkdownEventExtra -> {
            toMessagesByKMarkdown(content, extra.mention, extra.mentionRoles, extra.isMentionAll, extra.isMentionHere)
        }

        // 文件消息已转为卡片消息，详情请直接参考卡片消息
        is CardEventExtra -> {
            // try to decode
            extra.toMessages { tryDecodeCardContent(content, logger) }
        }
    }
}

private inline fun TextExtra.toMessages(contentElement: () -> List<Message.Element>): Messages {
    return toMessages(
        contentElement(),
        mention,
        mentionRoles,
        isMentionAll,
        isMentionHere
    )
}

/**
 * 使用消息事件并将其中的消息内容转化为 [KookChannelMessageDetailsContent].
 */
public fun Event<TextExtra>.toContent(isDirect: Boolean, bot: KookBot): KookReceiveMessageContent =
    KookReceiveMessageContent(isDirect, this, bot)

@OptIn(ExperimentalSimbotAPI::class)
internal fun tryDecodeCardContent(content: String, logger: Logger): List<Message.Element> {
    return runCatching {
        listOf(KookCardMessage(CardMessage.decode(content)))
    }.getOrElse { ex ->
        logger.warn("Cannot decode card message content to CardMessage, as text.", ex)
        logger.debug("Cannot decode card message content [{}] to CardMessage: {}", content, ex.message, ex)
        listOf(content.toText())
    }
}

private const val MET_NAME = "metId"
private const val ROL_NAME = "rolId"
private const val MET_REGEX_VALUE = "\\(met\\)(?<$MET_NAME>([a-zA-Z\\d]+|here|all))\\(met\\)"
private const val ROL_REGEX_VALUE = "\\(rol\\)(?<$ROL_NAME>\\d+)\\(rol\\)"

private val matchRegex = Regex("($MET_REGEX_VALUE)|($ROL_REGEX_VALUE)")


internal data class MentionCount(val id: String, var count: Int)
internal data class RoleMentionCount(val id: Int, var count: Int)

internal fun Collection<String>.toMentionCount(): MutableMap<String, MentionCount> {
    val map = mutableMapOf<String, MentionCount>()
    this.forEach { id ->
        map.computeValue(id) { _, current ->
            current?.also { it.count++ } ?: MentionCount(id, 1)
        }
    }
    return map
}

internal fun Collection<Int>.toMentionRoleCount(): MutableMap<Int, RoleMentionCount> {
    val map = mutableMapOf<Int, RoleMentionCount>()
    this.forEach { id ->
        map.computeValue(id) { _, current ->
            current?.also { it.count++ } ?: RoleMentionCount(id, 1)
        }
    }
    return map
}

internal fun toMessages(
    contentMessage: List<Message.Element>,
    mention: Collection<String>, mentionRoles: Collection<Int>,
    isMentionAll: Boolean, isMentionHere: Boolean,
): Messages {
    if (mention.isEmpty() && mentionRoles.isEmpty() && !isMentionAll && !isMentionHere) {
        return contentMessage.toMessages()
    }
    val messages = buildList(contentMessage.size + mention.size + mentionRoles.size + 3) {
        addAll(contentMessage)

        if (isMentionAll) {
            add(AtAll)
        }

        if (isMentionHere) {
            add(KookAtAllHere)
        }

        for (mentionId in mention) {
            add(At(mentionId.ID, type = AT_TYPE_USER))
        }

        for (mentionRoleId in mentionRoles) {
            add(At(mentionRoleId.ID, type = AT_TYPE_ROLE))
        }
    }

    return messages.toMessages()
}

internal fun toMessagesByKMarkdown(
    content: String,
    mention: Collection<String>, mentionRoles: Collection<Int>,
    isMentionAll: Boolean, isMentionHere: Boolean,
): Messages {
    if (mention.isEmpty() && mentionRoles.isEmpty() && !isMentionAll && !isMentionHere) {
        return messagesOf(content.toText())
    }

    var metAll = isMentionAll
    var metHere = isMentionHere
    val metMap = mention.toMentionCount()
    val metRoleMap = mentionRoles.toMentionRoleCount()

    val zeroMark: Byte = 0
    var status: Byte = 0
    if (metAll) {
        status = status or 0b1000
    }
    if (metHere) {
        status = status or 0b0100
    }
    if (metMap.isNotEmpty()) {
        status = status or 0b0010
    }
    if (metRoleMap.isNotEmpty()) {
        status = status or 0b0001
    }

    return buildMessages {
        var textBuffer: StringBuilder? = StringBuilder()
        fun addText(value: CharSequence, start: Int, end: Int) {
            val tb = textBuffer ?: StringBuilder().also { textBuffer = it }
            tb.append(value, start, end)
        }

        fun addText(value: String) {
            val tb = textBuffer ?: StringBuilder().also { textBuffer = it }
            tb.append(value)
        }

        fun addElement(element: Message.Element) {
            val tb = textBuffer?.toString()
            if (tb?.isNotEmpty() == true) {
                add(tb)
                textBuffer = null
            }

            add(element)
        }

        fun clearBuffer() {
            textBuffer?.toString()?.takeIf { it.isNotEmpty() }?.let { t ->
                add(t)
            }
        }

        val matched = matchRegex.walk(content, { c, s, e ->
            addText(c, s, e)
        }) { result ->
            if (status == zeroMark) {
                addText(result.value)
            }

            if (status and 0b1110 > 0) {
                val metId = result.groups[MET_NAME]
                if (metId != null) {
                    val id = metId.value
                    var skipText = false
                    when {
                        metAll && id == "all" -> {
                            metAll = false
                            status = status and 0b0111
                            addElement(AtAll)
                            skipText = true
                        }

                        metHere && id == "here" -> {
                            metHere = false
                            status = status and 0b1011
                            addElement(KookAtAllHere)
                            skipText = true
                        }

                        else -> {
                            if (metMap.isEmpty()) {
                                status = status and 0b1101
                            } else {
                                val met = metMap[id]
                                if (met != null) {
                                    met.count--
                                    if (met.count <= 0) {
                                        metMap.remove(id)
                                    }
                                    // mention user
                                    addElement(At(met.id.ID, type = AT_TYPE_USER))
                                    skipText = true
                                }
                            }
                        }
                    }
                    // end.
                    if (!skipText) {
                        addText(result.value)
                    }
                }
            }


            if (status and 0b0001 > 0) {
                val roleId = result.groups[ROL_NAME]
                if (roleId != null) {
                    var skipText = false
                    val id = roleId.value.toInt()
                    if (metRoleMap.isEmpty()) {
                        status = status and 0b1110
                    } else {
                        val met = metRoleMap[id]
                        if (met != null) {
                            met.count--
                            if (met.count <= 0) {
                                metRoleMap.remove(id)
                            }
                            // mention role
                            addElement(At(met.id.ID, type = AT_TYPE_ROLE))
                            skipText = true
                        }
                    }
                    if (!skipText) {
                        addText(result.value)
                    }
                }
            }
        }

        if (!matched) {
            addText(content)
        }

        clearBuffer()
    }
}



