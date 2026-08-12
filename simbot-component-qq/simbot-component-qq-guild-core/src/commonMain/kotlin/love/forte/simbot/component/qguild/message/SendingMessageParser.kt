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

package love.forte.simbot.component.qguild.message

import love.forte.simbot.component.qguild.ExperimentalQGApi
import love.forte.simbot.component.qguild.bot.QGBot
import love.forte.simbot.message.Messages
import love.forte.simbot.message.emptyMessages
import love.forte.simbot.qguild.QGInternalApi
import love.forte.simbot.qguild.api.message.GroupAndC2CSendBody
import love.forte.simbot.qguild.api.message.MessageSendApi
import love.forte.simbot.qguild.api.message.isEmpty
import love.forte.simbot.qguild.model.Message
import love.forte.simbot.qguild.stdlib.BotConfiguration
import love.forte.simbot.qguild.stdlib.MessageDestination
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmSynthetic
import love.forte.simbot.message.Message as SimbotMessage


/**
 * 通过消息体和message builder, 以责任链的形式构建消息体。
 */
public fun interface SendingMessageParser {
    /**
     * 将 [SimbotMessage.Element] 拼接到 [MessageSendApi.Body.Builder] 中。
     *
     * @param index 当前消息链中的数据.
     */
    public suspend fun invoke(
        index: Int,
        element: SimbotMessage.Element,
        messages: Messages?,
        builderContext: BuilderContext,
    )

    /**
     * 将 [SimbotMessage.Element] 拼接到 [GroupAndC2CSendBody] 中。
     *
     * @param index 当前消息链中的数据.
     */
    public suspend fun invoke(
        index: Int,
        element: SimbotMessage.Element,
        messages: Messages?,
        builderContext: GroupAndC2CBuilderContext,
    ) {
    }

    public enum class GroupBuilderType {
        GROUP, C2C
    }

    public abstract class AbstractBuilderContext<B>(
        public val builderFactory: () -> B
    ) {
        public val builders: ArrayDeque<B> = ArrayDeque()
        public abstract val configuration: BotConfiguration?
        internal abstract val destination: MessageDestination?

        internal val contentAsMarkdown: Boolean
            get() = destination?.let { configuration?.contentAsMarkdown?.get(it) } == true

        /**
         * 标记下一次再获取 builder 时必须新建。
         */
        public open var nextIsNew: Boolean = false
            protected set

        @PublishedApi
        internal open fun nextMustBeNew(value: Boolean = true) {
            nextIsNew = value
        }

        public open val builder: B
            get() {
                if (nextIsNew) {
                    return newBuilder().also {
                        nextMustBeNew(false)
                    }
                }

                if (builders.isEmpty()) {
                    return newBuilder()
                }

                return builders.last()
            }

        public open fun newBuilder(): B {
            return builderFactory().also { builders.addLast(it) }
        }


        /**
         * 如果符合 [check] 的条件，得到 [builder], 否则使用 [newBuilder] 构建一个新的builder。
         *
         * 如果尚未初始化，直接返回一个新的值，不做检测。
         *
         * 如果 [nextIsNew] 被标记为 `true`, 则本次不会调用 [check] 且必然得到新的 builder.
         *
         */
        public inline fun builderOrNew(check: (B) -> Boolean): B {
            if (nextIsNew) {
                return newBuilder().also {
                    nextMustBeNew(false)
                }
            }

            if (builders.isEmpty()) {
                return newBuilder()
            }

            return builder.takeIf(check) ?: newBuilder()
        }

    }

    public class BuilderContext @PublishedApi internal constructor(
        public val bot: QGBot?,
        internal override val destination: MessageDestination?,
        builderFactory: () -> MessageSendApi.Body.Builder,
    ) : AbstractBuilderContext<MessageSendApi.Body.Builder>(builderFactory) {
        @Deprecated("新增参数的兼容性次级构造", level = DeprecationLevel.ERROR)
        public constructor(builderFactory: () -> MessageSendApi.Body.Builder) : this(null, null, builderFactory)

        override val configuration: BotConfiguration?
            get() = bot?.source?.configuration
    }

    public class GroupAndC2CBuilderContext @QGInternalApi constructor(
        public val bot: QGBot,
        public val type: GroupBuilderType,
        public val targetOpenid: String,
        builderFactory: () -> GroupAndC2CSendBody
    ) : AbstractBuilderContext<GroupAndC2CSendBody>(builderFactory) {
        override val configuration: BotConfiguration
            get() = bot.source.configuration

        override val destination: MessageDestination
            get() = when (type) {
                GroupBuilderType.GROUP -> MessageDestination.GROUP
                GroupBuilderType.C2C -> MessageDestination.USER
            }
    }
}

/**
 * 将一个 [Message] 转化为 [Messages].
 */
public interface ReceivingMessageParser {

    /**
     * 解析一个 [Message], 并将其内部信息并入 [Context] 中。
     */
    public operator fun invoke(qgMessage: Message, context: Context): Context

    /**
     * 解析一个 `content`, 并将其内部信息并入 [Context] 中。
     */
    public operator fun invoke(qgContent: String, context: Context): Context

    /**
     * 消息链和正文文本内容的容器，用于 [ReceivingMessageParser.invoke] 中进行传递解析。
     *
     */
    public data class Context(
        public var messages: Messages,
        public var plainTextBuilder: StringBuilder,
        public var attachments: List<Message.Attachment>? = null,
    )
}


/**
 * 将一个 [Message.Element][SimbotMessage.Element] 拼接到 [MessageSendApi.Body.Builder] 中。
 */
public object MessageParsers {
    @ExperimentalQGApi
    public val sendingParsers: List<SendingMessageParser> = buildList {
        add(ContentParser)
        add(FaceParser)
        add(MentionParser)
        add(ArkParser)
        add(EmbedParser)
        add(AttachmentParser)
        add(ReplyToParser)
        add(ImageParser)
        add(ReferenceParser)
        add(MediaParser)
        add(MarkdownParser)
        add(KeyboardParser)
        add(KeyboardsParser)
    }

    @Deprecated(
        message = "请明确提供 `bot` 参数",
        level = DeprecationLevel.ERROR,
        replaceWith = ReplaceWith("parse(bot = bot, destination = destination, message = message)")
    )
    public suspend inline fun parse(
        message: SimbotMessage,
        crossinline onEachPre: MessageSendApi.Body.Builder.() -> Unit = {},
        onEachPost: MessageSendApi.Body.Builder.() -> Unit = {},
    ): List<MessageSendApi.Body.Builder> = parse(null, null, message, onEachPre, onEachPost)

    @ExperimentalQGApi
    public val receivingParsers: List<ReceivingMessageParser> = buildList {
        add(QGMessageParser)
    }

    /**
     * 将 [message] 解析为一个或多个 [MessageSendApi.Body.Builder]。
     *
     * ## 消息拆分
     *
     * 当按照顺序解析且出现无法被叠加的消息元素时，
     * 例如：
     * - [ark][MessageSendApi.Body.ark]
     * - [messageReference][MessageSendApi.Body.messageReference]
     * - [embed][MessageSendApi.Body.embed]
     * - [image][MessageSendApi.Body.image] / [fileImage][[MessageSendApi.Body.fileImage]
     * - 使用模板的 [markdown][MessageSendApi.Body.markdown]
     *
     * 会先当前已经解析完毕的内容构建一个 [MessageSendApi.Body.Builder], 然后再构建一个新的继续解析，
     * 直到结果全部解析完毕。
     *
     * 部分可叠加的消息元素，例如：
     * - [content][MessageSendApi.Body.content]
     * - 使用 [Markdown.content][Message.Markdown.content] 的 [markdown][MessageSendApi.Body.markdown]
     *
     * 会尝试直接拼接/叠加而不是构建新的 builder。
     *
     * ## 注意避免
     *
     * 但是虽然说可以尝试着进行 Builder 的拆分，但是仍请尽可能确保使用的消息内容不会出现冲突。
     * 因为拆分后的消息体并不能保证可以进行发送，它们有可能会因为缺失部分属性而导致发送失败、进而导致预期外的行为或异常。
     *
     * 举个例子，当解析的消息链中存在 [QGReplyTo] 时，其会尝试将当前已经解析出来的所有结果的 [msgId][MessageSendApi.Body.msgId]
     * 进行覆盖，但是它因无法影响到后续**可能**会继续被拆分出来的新的消息体而存在隐患。
     *
     * @return 解析结果的 [MessageSendApi.Body.Builder] 序列。
     */
    @OptIn(ExperimentalQGApi::class)
    @JvmSynthetic
    public suspend inline fun parse(
        bot: QGBot?,
        destination: MessageDestination? = null,
        message: SimbotMessage,
        crossinline onEachPre: MessageSendApi.Body.Builder.() -> Unit = {},
        onEachPost: MessageSendApi.Body.Builder.() -> Unit = {},
    ): List<MessageSendApi.Body.Builder> {
        val context = SendingMessageParser.BuilderContext(bot, destination) {
            MessageSendApi.Body.Builder().also(onEachPre)
        }

        when (message) {
            is SimbotMessage.Element -> {
                for (parser in sendingParsers) {
                    parser.invoke(0, message, null, context)
                }
            }

            is Messages -> {
                message.forEachIndexed { index, element ->
                    for (parser in sendingParsers) {
                        parser.invoke(index, element, message, context)
                    }
                }
            }
        }

        val builders = context.builders
        if (builders.size <= 1) {
            return listOf(builders.first().also(onEachPost))
        }

        return builders.mapTo(ArrayList(builders.size)) { it.also(onEachPost) }
    }


    @ExperimentalQGApi
    @JvmSynthetic
    public suspend inline fun parseToGroupAndC2C(
        bot: QGBot,
        message: SimbotMessage,
        builderType: SendingMessageParser.GroupBuilderType,
        targetOpenid: String,
        crossinline factory: () -> GroupAndC2CSendBody,
        crossinline onEachPre: GroupAndC2CSendBody.() -> Unit = {},
        onEachPost: GroupAndC2CSendBody.() -> Unit = {},
    ): List<GroupAndC2CSendBody> {
        val context = SendingMessageParser.GroupAndC2CBuilderContext(
            bot,
            builderType,
            targetOpenid
        ) {
            factory().also(onEachPre)
        }

        when (message) {
            is SimbotMessage.Element -> {
                for (parser in sendingParsers) {
                    parser.invoke(0, message, null, context)
                }
            }

            is Messages -> {
                message.forEachIndexed { index, element ->
                    for (parser in sendingParsers) {
                        parser.invoke(index, element, message, context)
                    }
                }
            }
        }

        val builders = context.builders
        if (builders.size <= 1) {
            return listOf(builders.first().also(onEachPost))
        }

        return builders.mapNotNullTo(ArrayList(builders.size)) {
            it.also(onEachPost).takeUnless { b -> b.isEmpty() }
        }
    }


    @OptIn(ExperimentalQGApi::class)
    @JvmOverloads
    public fun parse(
        message: Message,
        messagesInit: Messages = emptyMessages(),
    ): ReceivingMessageParser.Context {
        return receivingParsers.fold(
            ReceivingMessageParser.Context(
                messagesInit,
                StringBuilder(message.content.length),
                message.attachments,
            )
        ) { context, parser ->
            parser.invoke(message, context)
        }
    }


    @OptIn(ExperimentalQGApi::class)
    @JvmOverloads
    public fun parse(
        content: String,
        attachments: List<Message.Attachment>? = null,
        messagesInit: Messages = emptyMessages(),
    ): ReceivingMessageParser.Context {
        return receivingParsers.fold(
            ReceivingMessageParser.Context(
                messagesInit,
                StringBuilder(content.length),
                attachments
            )
        ) { context, parser ->
            parser.invoke(content, context)
        }
    }

}

@TmfsDsl
public class QGMessageForSendingForParse internal constructor() {
    public var sendBodyBuilder: MessageSendApi.Body.Builder = MessageSendApi.Body.Builder()

    public inline fun forSending(block: MessageSendApi.Body.Builder.() -> Unit) {
        sendBodyBuilder.block()
    }

    public fun contentAppend(contentText: String) {
        forSending {
            if (content == null) {
                content = contentText
            } else {
                content += contentText
            }
        }
    }

}

@Retention(AnnotationRetention.BINARY)
@DslMarker
internal annotation class TmfsDsl // TencentMessageForSendingBuilderDsl
