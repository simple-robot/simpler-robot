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

package love.forte.simbot.component.kaiheila.event.message

import kotlinx.serialization.Transient
import love.forte.simbot.api.message.MessageContent
import love.forte.simbot.api.message.containers.AccountInfo
import love.forte.simbot.api.message.containers.BotInfo
import love.forte.simbot.api.message.events.MessageGet
import love.forte.simbot.component.kaiheila.KhlBot
import love.forte.simbot.component.kaiheila.event.BotInitialized
import love.forte.simbot.component.kaiheila.event.Event
import love.forte.simbot.component.kaiheila.event.EventLocator
import love.forte.simbot.component.kaiheila.objects.Attachments
import love.forte.simbot.component.kaiheila.objects.Channel


/**
 * [消息相关事件列表](https://developer.kaiheila.cn/doc/event/message)
 *
 * 由于消息相关事件中没有系统事件，因此 [Event.extra] 全部都是 [Event.Extra.Text] 类型。
 *
 */
public interface MessageEventExtra : Event.Extra.Text

/**
 * 与资源相关的extra.
 *
 */
public interface AttachmentsMessageEventExtra<A : Attachments> : MessageEventExtra {
    val attachments: A
}


/**
 * 消息相关事件接口
 *
 */
public interface MessageEvent<E : MessageEventExtra> : Event<E>, MessageGet, BotInitialized {
    override var bot: KhlBot
    override val channelType: Channel.Type
    override val type: Event.Type
    override val targetId: String
    override val authorId: String
    override val content: String
    override val msgId: String
    override val msgTimestamp: Long
    override val nonce: String
    override val extra: E
    override val originalData: String
    override val msgContent: MessageContent
    override val flag: MessageGet.MessageFlag<MessageGet.MessageFlagContent>
    override val accountInfo: AccountInfo


    override val botInfo: BotInfo
        get() = bot

    override val id: String
        get() = msgId

    override val time: Long
        get() = msgTimestamp

}


public abstract class AbstractMessageEvent<E : MessageEventExtra> : MessageEvent<E> {
    @Transient
    override lateinit var bot: KhlBot

    private lateinit var _msgContent: MessageContent
    override val msgContent: MessageContent
        get() {
            if (!::_msgContent.isInitialized) {
                _msgContent = initMessageContent()
            }
            return _msgContent
        }

    override val accountInfo: AccountInfo
        get() = extra.author

    protected abstract fun initMessageContent(): MessageContent
    override val originalData: String get() = toString()
}


internal class MessageFlag<F : MessageGet.MessageFlagContent>(override val flag: F) : MessageGet.MessageFlag<F>


public fun EventLocator.registerCoordinates() {
    TextEventImpl.run {
        registerCoordinates()
    }

    ImageEventImpl.run {
        registerCoordinates()
    }

    VideoEventImpl.run {
        registerCoordinates()
    }



    TODO("Register message event coordinates")

}


//region External interface
//

public interface MessageEventExternal

/**
 * 纯文本消息事件。
 */
public interface TextEvent : MessageEvent<TextEventExtra>, MessageEventExternal

/**
 * 图片消息事件。
 */
public interface ImageEvent : MessageEvent<ImageEventExtra>, MessageEventExternal

/**
 * 文件消息事件。
 */
public interface FileEvent : MessageEvent<FileEventExtra>, MessageEventExternal

/**
 * 视频消息事件。
 */
public interface VideoEvent : MessageEvent<VideoEventExtra>, MessageEventExternal

/**
 * 卡片消息事件。
 */
public interface CardEvent : MessageEvent<CardEventExtra>, MessageEventExternal

/**
 * `KMarkdown` 消息事件。
 */
public interface KMarkdownEvent : MessageEvent<KMarkdownEventExtra>, MessageEventExternal

//endregion



