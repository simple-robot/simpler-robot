/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
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

package love.forte.simbot.component.mirai.message.event

import love.forte.simbot.api.message.MessageContent
import love.forte.simbot.api.message.containers.AccountInfo
import love.forte.simbot.api.message.events.PrivateMsg
import love.forte.simbot.component.mirai.message.MiraiBotAccountInfo
import love.forte.simbot.component.mirai.message.MiraiMessageChainContent
import love.forte.simbot.component.mirai.message.MiraiPrivateMsgFlag
import love.forte.simbot.component.mirai.message.miraiPrivateFlag
import net.mamoe.mirai.contact.OtherClient
import net.mamoe.mirai.event.events.OtherClientMessageEvent
import net.mamoe.mirai.message.data.source


/**
 * mirai的 [其他客户端发送给bot消息事件][OtherClientMessageEvent]。
 * 此事件会被处理为一个 [私聊消息][PrivateMsg]。
 * @author ForteScarlet
 */
public interface MiraiOtherClientMessage :
    PrivateMsg,
    MiraiOtherClientContainer,
    MiraiSpecialEvent<OtherClientMessageEvent>





public class MiraiOtherClientMessageImpl(event: OtherClientMessageEvent) :
    MiraiMessageMsgGet<OtherClientMessageEvent>(event),
    MiraiOtherClientMessage {

    /**
     * 设备信息。
     * @see OtherClient
     */
    override val client: OtherClient
        get() = event.client


    /** 当前监听事件消息的ID。一般情况下应当是一个唯一ID。 */
    override val id: String
        get() = "MICM-${event.hashCode()}"

    /**
     * 账号的信息。即bot自己的账号信息。
     */
    override val accountInfo: AccountInfo = MiraiBotAccountInfo(event.bot)

    /**
     *  消息事件的消息正文文本。
     *
     * [消息正文][msgContent] 不允许为`null`，但是其中的 [msg][MessageContent.msg] 则就不保证了。
     */
    override val msgContent: MessageContent = MiraiMessageChainContent(message)

    /**
     * 获取私聊消息类型，固定为自己。
     */
    override val privateMsgType: PrivateMsg.Type
        get() = PrivateMsg.Type.SELF

    /**
     * 私聊消息标识。
     */
    override val flag: MiraiPrivateMsgFlag = miraiPrivateFlag { MiraiPrivateFlagContent(message.source) }
}