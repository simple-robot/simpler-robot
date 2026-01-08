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

import love.forte.simbot.ability.DeleteOption
import love.forte.simbot.ability.StandardDeleteOption
import love.forte.simbot.common.id.ID
import love.forte.simbot.component.qguild.QQGuildComponent
import love.forte.simbot.message.*
import love.forte.simbot.qguild.message.ContentTextDecoder
import love.forte.simbot.qguild.model.Message
import love.forte.simbot.suspendrunner.STP
import kotlin.jvm.JvmSynthetic
import love.forte.simbot.qguild.model.Message as QGSourceMessage

/**
 * 接收到的事件消息内容。
 *
 * @author ForteScarlet
 */
public abstract class QGBaseMessageContent : MessageContent {
    /**
     * 此消息的ID
     */
    abstract override val id: ID


    /**
     * 通过原始消息转化后的消息元素链。
     *
     * 如果事件中的 `content`没有任何可匹配特殊的内嵌格式，
     * 则 [messages] 的第一个元素会直接根据 [content][QGSourceMessage.content] 拼接为 [QGContentText]，
     * 否则会解析 `content` 并将其中的内容**依次顺序地**根据类型转化为以下可能的类型：
     * - [Text]: 根据解析的 `content` 中非内嵌格式文本的[**解码**][ContentTextDecoder.decode]结果。
     * - [At]: 当存在提及用户的内嵌格式时（例如 `<@123456>`）。
     * 按理说会与 [sourceMessage.mentions][QGSourceMessage.mentions] 对应。
     * - [At(type=channel)][At]: 当存在提及频道的内嵌格式时（例如 `<#123456>`）。
     * 类型同样为 [At], 但是 [At.type] 的值为 [QQGuildComponent.AT_CHANNEL_TYPE]。
     * - [AtAll]: 当 [sourceMessage.mentionEveryone][QGSourceMessage.mentionEveryone] == true 时，
     * 会将所有的 `@everyone` 视为提及所有而被转化为 [AtAll]；而如果为 `false` 则不会转化并被视为普通的文本字符串。
     * - [Face]: 当 `content` 中存在系统表情时（例如 `<emoji:5>`）会被转化为 [Face] 类型。
     * 注意并不是转化为 [Emoji]，因为其代表的是**系统表情**。
     *
     * 上述解析结束后，会再根据原始消息中的其他可转化属性在结果后面继续追加如下可能的类型：
     * - [QGArk]: 来自于 [sourceMessage.ark][QGSourceMessage.ark]
     * - [QGAttachmentMessage]: 来自于 [sourceMessage.attachments][QGSourceMessage.attachments]，可能有多个
     * - [QGReference]: 来自于 [sourceMessage.messageReference][QGSourceMessage.messageReference]
     *
     */
    abstract override val messages: Messages

    // TODO attachment image 类型解析为 QGImage?

    /**
     *
     * 接收到的被替换/移除所有 **特殊文本（[内嵌格式](https://bot.q.qq.com/wiki/develop/api/openapi/message/message_format.html)）** 内容后的纯文本消息内容。
     *
     * ### `mentions` 替换
     *
     * [plainText] 会按照 [mentions][QGSourceMessage.mentions] 逐次替换对应的内嵌消息字符串。
     *
     * 例如，发送的消息为：
     * ```text
     * @张三 你好
     * ```
     * 此时收到的消息中的 `content` 为
     * ```text
     * <qqbot-at-user id="123456" /> 你好
     * ```
     * 那么就会根据 `mentions` 进行替换，最终的 [plainText] 的值为：
     * ```text
     *  你好
     * ```
     * _**⚠️ 注意！此处的 `" 你好"` 前面是大概率有空格的，因为在默认情况下不会对消息有过多的操作，而替换后的前后空格也将会被保留。
     * 如果有需要，请注意在判断之前先进行 `trim` 等操作来消除空格，或者使用例如 `@ContentTrim` 等相关功能。**_
     *
     * 被替换掉的 `mention` 会被解析在 [messages] 中作为 [At]。
     *
     * ### `mention everyone` 替换
     *
     * 当 [mentionEveryone][QGSourceMessage.mentionEveryone] 为 `true` 时，
     * [plainText] 会尝试清理文本中**第一个** `<qqbot-at-everyone />`。
     *
     * 如果是群消息（即没有 [QGSourceMessage] 类型，只有 `content`），则会全部进行转化。
     *
     * 被替换的 `<qqbot-at-everyone />` 会被解析于 [messages] 中作为 [AtAll]。
     *
     * ### `mention channel` 替换
     *
     * 消息内嵌格式中提及一个子频道的格式如下：
     *
     * ```text
     * <#channel_id>
     * ```
     *
     * [plainText] 会寻找并清除这些类型的消息。例如一个消息如下：
     *
     * ```text
     * 这个频道 #频道01 快去看看吧！
     * ```
     *
     * 那么收到的消息则为
     *
     * ```text
     * 这个频道 <#654321> 快去看看吧！
     * ```
     *
     * 而 [plainText] 进行转化后的结果为
     *
     * ```text
     * 这个频道  快去看看吧！
     * ```
     *
     * 被替换掉的 `channel mention` 会在 [messages] 中被转化为 [At] (且 [At.type] 为 `channel` )。
     *
     * ### `emoji` 替换
     *
     * 消息内嵌格式中发送一个 [系统表情](https://bot.q.qq.com/wiki/develop/api/openapi/emoji/model.html#emoji-%E5%88%97%E8%A1%A8) 时的格式为
     *
     * ```text
     * <emoji:id>
     * ```
     *
     * 它们在 [plainText] 中也同样会被解析掉。被解析掉的 `emoji` 会在 [messages] 中被作为 [Face]。
     *
     * ### 原始Content
     *
     * 如果你想要得到本次消息最原始的 `content`，直接使用 [sourceContent] 获取
     *
     * ```kotlin
     * val contentText = receiveContent.sourceMessage.content
     * ```
     *
     * @see messages
     *
     */
    abstract override val plainText: String

    /**
     * 事件中原始的 `content` 内容。
     *
     */
    public abstract val sourceContent: String

    /**
     * 从 [messages] 中寻找并获取第一个 [QGReference] 类型的元素。
     * 不会发生挂起行为。
     */
    @STP
    override suspend fun reference(): QGReference? =
        messages.firstNotNullOfOrNull { it as? QGReference }

    /**
     * 根据 [消息引用][reference] 获取或查询对应的消息正文内容。
     *
     * 文字子频道的消息会通过API查询，C2C或群聊的消息则始终得到 `null`, 无法获取。
     */
    @STP
    override suspend fun referenceMessage(): QGBaseMessageContent? =
        queryReferenceMessage()

    protected abstract suspend fun queryReferenceMessage(): QGBaseMessageContent?
}

/**
 * 文字子频道中接收到的事件消息内容。
 *
 * @author ForteScarlet
 */
public abstract class QGMessageContent : QGBaseMessageContent() {
    /**
     * 事件接收到的原始的消息对象 [Message][QGSourceMessage]
     */
    public abstract val sourceMessage: QGSourceMessage

    /**
     * 撤回此文字子频道的消息。
     *
     * @throws RuntimeException 撤回过程中可能产生的异常，例如无权限、没有对应的消息等。
     * 可以被 [StandardDeleteOption.IGNORE_ON_FAILURE] 选项忽略。
     */
    @JvmSynthetic
    abstract override suspend fun delete(vararg options: DeleteOption)


    abstract override suspend fun queryReferenceMessage(): QGBaseMessageContent?
}

/**
 * 群聊或c2c单聊接收到的事件消息内容。
 * @author ForteScarlet
 */
public abstract class QGGroupAndC2CMessageContent : QGBaseMessageContent() {
    /**
     * 原始的消息正文内容。
     */
    abstract override val sourceContent: String

    /**
     * 原始的消息事件内的 attachements
     */
    public abstract val attachments: List<Message.Attachment>

    /**
     * 暂时不支持消息撤回。
     * 如果 [options] 中不包含
     * [StandardDeleteOption.IGNORE_ON_UNSUPPORTED]
     * 则抛出 [UnsupportedOperationException]
     */
    @JvmSynthetic
    override suspend fun delete(vararg options: DeleteOption) {
        if (options.none { it == StandardDeleteOption.IGNORE_ON_UNSUPPORTED }) {
            throw UnsupportedOperationException("QGGroupAndC2CMessageContent.delete")
        }
    }

    /**
     * C2C单聊消息暂不支持查询消息详情。
     */
    override suspend fun queryReferenceMessage(): QGBaseMessageContent? = null
}
