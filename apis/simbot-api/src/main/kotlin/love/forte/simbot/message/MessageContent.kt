/*
 *  Copyright (c) 2021-2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simbot.message

import love.forte.simbot.ID

/**
 * 一个消息内容，其中存在一个 [消息元数据][Message.Metadata] 和一个[消息链][Messages]。
 *
 * 对于一个事件，使用 [接收的消息内容][ReceivedMessageContent].
 *
 * @see RemoteMessageContent
 *
 */
public abstract class MessageContent {

    /**
     * 消息内容ID。一般可以通过 `metadata` 获取。
     */
    public abstract val messageId: ID

    /**
     * 接收到的消息链
     */
    public abstract val messages: Messages

    /**
     * 得到这串消息中的所有 [纯文本][PlainText] 消息的拼接字符串。
     */
    public open val plainText: String by lazy(LazyThreadSafetyMode.PUBLICATION) {
        messages.filterIsInstance<PlainText<*>>().joinToString(separator = "", transform = PlainText<*>::text)
    }
}

/**
 * 一个远端消息主体，一般代表通过事件或者查询而得的事件主体。
 */
public sealed class RemoteMessageContent : MessageContent() {

    /**
     * 接收到的消息的 [元数据][Message.Metadata].
     */
    public abstract val metadata: Message.Metadata

    /**
     * [metadata] 中的 [id][Message.Metadata.id]。
     */
    override val messageId: ID get() = metadata.id
}


/**
 * 一个接收到的 [MessageContent], 即事件中的 [MessageContent], 代表一个被动获取的消息。
 */
public abstract class ReceivedMessageContent : RemoteMessageContent()


/**
 * 一个获得的 [MessageContent], 即主动获取的消息，常见于历史消息获取。
 */
public abstract class ObtainedMessageContent : RemoteMessageContent()


/**
 * 一个本地的消息实体，一般代表本地构建的非远端存在的消息。
 *
 * 本地构建的消息也许不存在 metadata
 *
 */
public abstract class LocalMessageContent : MessageContent()



