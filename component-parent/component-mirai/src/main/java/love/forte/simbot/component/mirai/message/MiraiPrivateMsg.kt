/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     MessageEvent.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.component.mirai.message

import love.forte.simbot.component.mirai.utils.toMiraiMessageContent
import love.forte.simbot.core.api.message.MessageContent
import love.forte.simbot.core.api.message.TextMessageContent
import love.forte.simbot.core.api.message.assists.Flag
import love.forte.simbot.core.api.message.assists.flag
import love.forte.simbot.core.api.message.containers.AccountInfo
import love.forte.simbot.core.api.message.events.PrivateMsg
import net.mamoe.mirai.message.FriendMessageEvent
import net.mamoe.mirai.message.data.EmptyMessageChain
import net.mamoe.mirai.message.data.MessageSource
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.asMessageChain


/**
 * mirai的私聊消息事件。
 */
public class MiraiPrivateMsg(event: FriendMessageEvent) :
    MiraiMessageMsgGet<FriendMessageEvent>(event), PrivateMsg {

    /**
     * 账号的信息。
     */
    override val accountInfo: AccountInfo = MiraiFriendAccountInfo(event.friend)

    /**
     * 获取私聊消息类型，好友类型。
     */
    override val privateMsgType: PrivateMsg.Type = PrivateMsg.Type.FRIEND



    private var _msgContent: MessageContent = MiraiMessageContent(message)

    /**
     *  消息事件的消息正文文本。
     *  使用 [MiraiMessageContent]作为最终消息载体。
     */
    override var msgContent: MessageContent
        get() = _msgContent
        set(value) {
            _msgContent = value.toMiraiMessageContent()
        }

    /**
     * 提供一个简单的方法来获取/设置 [msgContent] 中的文本内容
     */
    override var msg: String?
        get() = msgContent.msg
        set(value) {
            msgContent = value?.let { TextMessageContent(it) }
                ?: MiraiMessageContent(EmptyMessageChain)
        }

    /**
     * 私聊消息标识，
     * 非线程安全的懒加载。
     */
    override val flag: Flag<FlagContent> by lazy(LazyThreadSafetyMode.NONE) {
        miraiMessageFlag(FlagContent())
    }

    /**
     * id 直接使用 [标识][flagId] 获取。
     */
    override val id: String
        get() = flag.flagId


    /** inner flag content. */
    inner class FlagContent : MiraiMessageSourceFlagContent(), PrivateMsg.FlagContent {
        override val source: MessageSource get() = event.source
    }
}




