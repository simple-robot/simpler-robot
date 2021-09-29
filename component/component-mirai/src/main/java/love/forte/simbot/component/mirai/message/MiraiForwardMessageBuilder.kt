/*
 *
 *  * Copyright (c) 2021. ForteScarlet All rights reserved.
 *  * Project  simple-robot
 *  * File     MiraiAvatar.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *
 */

@file:JvmName("MiraiForwardMessageUtil")

package love.forte.simbot.component.mirai.message

import love.forte.simbot.api.message.MessageContent
import love.forte.simbot.api.message.containers.AccountCodeContainer
import love.forte.simbot.api.message.containers.AccountContainer
import love.forte.simbot.api.message.containers.AccountInfo
import love.forte.simbot.api.message.containers.AccountNameContainer
import love.forte.simbot.api.message.events.MessageGet
import love.forte.simbot.component.mirai.message.event.MiraiMessageMsgGet
import love.forte.simbot.component.mirai.utils.toMiraiMessageContent
import love.forte.simbot.processor.RemoteResourceInProcessor
import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.message.data.*
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
private inline val nowSecond
    get() = Duration.milliseconds(System.currentTimeMillis()).inWholeSeconds.toInt()

/**
 *
 * @author ForteScarlet
 */
public class MiraiForwardMessageBuilder(
    private val cache: MiraiMessageCache? = null,
    private val remoteResourceInProcessor: RemoteResourceInProcessor,
) {
    private val nodes = mutableListOf<suspend (Contact) -> ForwardMessage.Node>()

    private val b = ForwardMessageBuilder(Bot.instances[0].asFriend)

    @JvmSynthetic
    public fun add(nodeBlock: suspend (Contact) -> ForwardMessage.Node): MiraiForwardMessageBuilder = also {
        nodes.add(nodeBlock)
    }

    public fun add(node: ForwardMessage.Node): MiraiForwardMessageBuilder = also {
        nodes.add { node }
    }

    public fun addAll(nodes: Collection<ForwardMessage.Node>): MiraiForwardMessageBuilder = also {
        this.nodes.addAll(nodes.map { n -> { n } })
    }


    /**
     * 添加一条消息。
     */
    @OptIn(ExperimentalTime::class)
    @JvmSynthetic
    public fun add(
        senderId: Long,
        senderName: String,
        time: Int = nowSecond,
        messageGetter: suspend (Contact) -> Message,
    ): MiraiForwardMessageBuilder = also {
        add { c ->
            ForwardMessage.Node(
                senderId = senderId,
                senderName = senderName,
                time = time,
                message = messageGetter(c)
            )
        }
    }

    @JvmOverloads
    public fun add(
        senderId: Long,
        senderName: String,
        time: Int = nowSecond,
        message: Message,
    ): MiraiForwardMessageBuilder =
        add(senderId, senderName, time) { message }

    @JvmOverloads
    public fun add(
        senderId: Long,
        senderName: String,
        time: Int = nowSecond,
        message: MessageContent,
    ): MiraiForwardMessageBuilder = also {
        when (message) {
            is MiraiMessageChainContent -> {
                val forwardMessage = message.message.toForwardMessage(senderId, senderName, time)
                add(senderId, senderName, time) { forwardMessage }
            }
            is MiraiMessageContent -> {
                add(senderId, senderName, time, message::getMessage)
            }
            else -> {
                add(senderId,
                    senderName,
                    time,
                    message.toMiraiMessageContent(null, cache, remoteResourceInProcessor)::getMessage
                )

            }
        }

    }


    @OptIn(ExperimentalTime::class)
    @JvmOverloads
    public fun add(
        senderId: Long? = null,
        senderName: String? = null,
        time: Int? = null,
        event: MessageGet,
    ): MiraiForwardMessageBuilder {
        val messageGetter: suspend (Contact) -> Message

        if (event is MiraiMessageMsgGet<*>) {
            return add {
                val e = event.event
                ForwardMessage.Node(
                    e.sender.id,
                    e.time,
                    e.senderName,
                    e.message
                )
            }
        } else {
            // Not mirai event. maybe... convert it?
            val content = event.msgContent

            messageGetter = if (content is MiraiMessageContent) {
                content::getMessage
            } else {
                { c ->
                    content.cats.map { n ->
                        n.toMiraiMessageContent(null, cache, remoteResourceInProcessor).getMessage(c)
                    }.toMessageChain()
                }
            }
        }

        val senderId0 = senderId ?: event.accountInfo.accountCodeNumber
        val senderName0 = senderName ?: event.accountInfo.accountRemarkOrNickname ?: " "
        val time0 = time ?: Duration.milliseconds(event.time).inWholeSeconds.toInt()

        return add(senderId0, senderName0, time0, messageGetter)
    }


    @JvmOverloads
    @Suppress("unused", "FunctionName")
    @JvmName("addLazy")
    public fun _add(
        senderId: Long,
        senderName: String,
        time: Int = nowSecond,
        messageGetter: (Contact) -> Message,
    ): MiraiForwardMessageBuilder =
        add(senderId, senderName, time, messageGetter::invoke)



    /**
     * 添加一条消息。
     */
    @JvmOverloads
    public fun add(
        senderId: Long,
        senderName: String,
        time: Int = nowSecond,
        message: String,
    ) =
        add(senderId, senderName, time, message.toMiraiMessageContent(null, cache, remoteResourceInProcessor))



    /**
     * 添加一条消息。
     */
    @JvmOverloads
    public fun add(
        senderId: AccountCodeContainer,
        senderName: AccountNameContainer,
        time: Int = nowSecond,
        message: String,
    ) =
        add(senderId, senderName, time, message.toMiraiMessageContent(null, cache, remoteResourceInProcessor))


    /**
     * 添加一条消息
     */
    @JvmOverloads
    public fun add(
        senderId: AccountCodeContainer,
        senderName: AccountNameContainer,
        time: Int = nowSecond,
        message: MessageContent,
    ): MiraiForwardMessageBuilder =
        add(
            senderId.accountCodeNumber, senderName.accountRemarkOrNickname ?: " ",
            time,
            message
        )


    /**
     * 添加一条消息
     */
    @JvmOverloads
    public fun add(
        senderId: AccountCodeContainer,
        senderName: AccountNameContainer,
        time: Int? = null,
        message: MessageGet,
    ): MiraiForwardMessageBuilder =
        add(senderId.accountCodeNumber, senderName.accountRemarkOrNickname, time, message)


    /**
     * 添加一条消息。
     */
    @JvmOverloads
    public fun add(
        sender: AccountInfo,
        time: Int = nowSecond,
        message: String,
    ) = add(sender, sender, time, message)


    /**
     * 添加一条消息
     */
    @JvmOverloads
    public fun add(
        sender: AccountInfo,
        time: Int = nowSecond,
        message: MessageContent,
    ) = add(sender, sender, time, message)


    /**
     * 添加一条消息
     */
    @JvmOverloads
    public fun add(
        sender: AccountInfo,
        time: Int? = null,
        message: MessageGet,
    ) = add(sender, sender, time, message)


    /**
     * 添加一条消息。
     */
    @JvmOverloads
    public fun add(
        sender: AccountContainer,
        time: Int = nowSecond,
        message: String,
    ) = add(sender.accountInfo, time, message)


    /**
     * 添加一条消息
     */
    @JvmOverloads
    public fun add(
        sender: AccountContainer,
        time: Int = nowSecond,
        message: MessageContent,
    ) = add(sender.accountInfo, time, message)

    /**
     * 添加一条消息
     */
    @JvmOverloads
    public fun add(
        sender: AccountContainer,
        time: Int? = null,
        message: MessageGet,
    ) = add(sender.accountInfo, time, message)


    fun build(): MiraiForwardMessage = MiraiForwardMessage(nodes)

}


@JvmOverloads
@OptIn(ExperimentalTime::class)
public fun MessageGet.toForwardMessage(
    cache: MiraiMessageCache? = null,
    remoteResourceInProcessor: RemoteResourceInProcessor = RemoteResourceInProcessor.Default,
): MessageContent {
    val messageGetter: suspend (Contact) -> Message

    if (this is MiraiMessageMsgGet<*>) {
        val event = this.event
        return MiraiForwardMessage(listOf {
            ForwardMessage.Node(
                event.sender.id,
                event.time,
                event.senderName,
                event.message
            )
        })
    } else {
        // Not mirai event. maybe... convert it?
        val content = this.msgContent

        messageGetter = if (content is MiraiMessageContent) {
            content::getMessage
        } else {
            { c ->
                content.cats.map { n ->
                    n.toMiraiMessageContent(null, cache, remoteResourceInProcessor).getMessage(c)
                }.toMessageChain()
            }
        }
    }

    val senderId = this.accountInfo.accountCodeNumber
    val senderName = this.accountInfo.accountRemarkOrNickname ?: " "
    val time = Duration.milliseconds(time).inWholeSeconds.toInt()

    return MiraiForwardMessage(listOf { c ->
        ForwardMessage.Node(
            senderId = senderId,
            senderName = senderName,
            time = time,
            message = messageGetter(c)
        )
    })
}


/**
 * 将 [MessageContent] 转化为一个mirai的合并转发消息。
 *
 */
@JvmOverloads
public fun MessageContent.toForwardMessage(
    sender: AccountInfo,
    time: Int = nowSecond,
    cache: MiraiMessageCache? = null,
    remoteResourceInProcessor: RemoteResourceInProcessor = RemoteResourceInProcessor.Default,
): MessageContent {
    if (this is MiraiForwardMessage || this is MiraiRawForwardMessage) {
        return this
    }
    if (this is MiraiMessageChainContent) {
        val forwardMessage = this.message.toForwardMessage(
            sender.accountCodeNumber,
            sender.accountRemarkOrNickname ?: " ",
            time
        )
        return MiraiRawForwardMessage(forwardMessage)
    }

    fun MiraiMessageContent.toForward() = MiraiForwardMessage(listOf { c ->
        ForwardMessage.Node(
            senderId = sender.accountCodeNumber,
            senderName = sender.accountRemarkOrNickname ?: " ",
            time = time,
            message = getMessage(c)
        )
    })

    if (this is MiraiMessageContent) {
        return toForward()
    }

    return toMiraiMessageContent(null, cache, remoteResourceInProcessor).toForward()

}


@JvmSynthetic
fun buildForwardMessageContent(block: suspend ForwardMessageBuilder.(Contact) -> Unit): MessageContent {
    return MiraiForwardMessage(block)
}

@JvmName("buildForwardMessageContent")
fun buildForwardMessageContentBlocking(block: ForwardMessageBuilderWithContactFunction): MessageContent {
    return MiraiForwardMessage { c ->
        block.accept(this, c)
    }
}


public fun interface ForwardMessageBuilderWithContactFunction {
    fun accept(builder: ForwardMessageBuilder, contact: Contact)
}