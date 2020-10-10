/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     MiraiMessageContent.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.component.mirai.message

import love.forte.simbot.core.api.message.MessageContent
import net.mamoe.mirai.message.data.EmptyMessageChain
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.asMessageChain

/**
 *
 * mirai的消息载体。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
public open class MiraiMessageContent(var messageChain: MessageChain) : MessageContent {
    /**
     * 消息字符串文本。一般来讲，如果存在一些特殊消息，
     * 那么他们会作为 **CQ码** 字符串存在于消息中。
     */
    override val msg: String?
        get() = messageChain.toString()
        // set(value) {
        //     messageChain = value?.let { PlainText(it).asMessageChain() } ?: EmptyMessageChain
        // }
    // /**
    //  * 除了使用字符串文本的 [setMsg][msg] 以外，你也可以通过 [setMsg] 来将一些可能为非字符串的内容设置到一个 [MessageContent] 中。
    //  * @param msgContent MessageContent
    //  */
    // override fun setMsg(msgContent: MessageContent) {
    //     messageChain = if(msgContent is MiraiMessageContent) msgContent.messageChain
    //     else msgContent.msg?.let { PlainText(it) }?.asMessageChain() ?: EmptyMessageChain
    // }

    /**
     * 一个 [消息正文][MessageContent] 应当可以与其他消息进行拼接，并得到一个新的 [MessageContent]
     * @param msgContent MessageContent
     * @return MessageContent
     */
    override fun plus(msgContent: MessageContent): MessageContent {
        val plusChain = if(msgContent is MiraiMessageContent) msgContent.messageChain
        else msgContent.msg?.let { PlainText(it) }?.asMessageChain() ?: EmptyMessageChain
        return MiraiMessageContent(messageChain + plusChain)
    }
}