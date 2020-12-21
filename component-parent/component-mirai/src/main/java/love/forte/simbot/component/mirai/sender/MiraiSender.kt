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
package love.forte.simbot.component.mirai.sender

import kotlinx.coroutines.runBlocking
import love.forte.common.utils.Carrier
import love.forte.common.utils.toCarrier
import love.forte.simbot.api.message.MessageContent
import love.forte.simbot.api.message.assists.Flag
import love.forte.simbot.api.message.containers.AccountCodeContainer
import love.forte.simbot.api.message.containers.BotContainer
import love.forte.simbot.api.message.containers.GroupCodeContainer
import love.forte.simbot.api.message.events.MsgGet
import love.forte.simbot.api.sender.ErrorSender
import love.forte.simbot.api.sender.Sender
import love.forte.simbot.api.sender.SenderFactory
import love.forte.simbot.component.mirai.message.event.MiraiGroupFlagContent
import love.forte.simbot.component.mirai.message.event.MiraiMessageMsgGet
import love.forte.simbot.component.mirai.message.event.MiraiPrivateFlagContent
import love.forte.simbot.component.mirai.message.miraiMessageFlag
import love.forte.simbot.component.mirai.utils.toMiraiMessageContent
import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Friend
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.NormalMember
import net.mamoe.mirai.message.MessageReceipt
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.MessageChain


public object MiraiSenderFactory : SenderFactory {
    override fun getOnMsgSender(msg: MsgGet): Sender {
        return if (msg is MiraiMessageMsgGet<*>) {
            MiraiSender(Bot.getInstance(msg.botInfo.botCodeNumber), msg.subject, msg.message)
        } else {
            MiraiSender(Bot.getInstance(msg.botInfo.botCodeNumber))
        }
    }

    override fun getOnBotSender(bot: BotContainer): Sender = MiraiSender(Bot.getInstance(bot.botInfo.botCodeNumber))
}


/**
 * mirai 对于 [送信器][Sender] 的实现。
 */
public class MiraiSender(
    private val bot: Bot,
    /** 当前收到的消息的实例。如果是一个botSender则会为null。 */
    private val contact: Contact? = null,
    /** 当前收到的消息。如果是一个botSender则会为null。 */
    private val message: MessageChain? = null
) : Sender {


    /**
     * 发送群聊消息。
     */
    private fun sendGroupMsg0(group: Long, msg: MessageContent): Carrier<Flag<MiraiGroupFlagContent>> {
        val miraiMsg = runBlocking { msg.toMiraiMessageContent(message) }
        // get group.
        val g: Group = bot.group(group)
        val messageReceipt = runBlocking {
            val message: Message = miraiMsg.getMessage(g)
            if (message.isNotEmptyMsg()) {
                g.sendMessage(message)
            } else {
                null
            }
        }
        return messageReceipt?.let {
            miraiMessageFlag<MiraiGroupFlagContent> { MiraiGroupFlagContent(it.source) }
        }.toCarrier()
    }

    override fun sendGroupMsg(group: String, msg: String): Carrier<Flag<MiraiGroupFlagContent>> =
        sendGroupMsg0(group.toLong(), msg.toMiraiMessageContent(message))

    override fun sendGroupMsg(group: String, msg: MessageContent): Carrier<Flag<MiraiGroupFlagContent>> =
        sendGroupMsg0(group.toLong(), msg)

    override fun sendGroupMsg(group: Long, msg: String): Carrier<Flag<MiraiGroupFlagContent>> =
        sendGroupMsg0(group, msg.toMiraiMessageContent(message))

    override fun sendGroupMsg(group: Long, msg: MessageContent): Carrier<Flag<MiraiGroupFlagContent>> =
        sendGroupMsg0(group, msg)

    override fun sendGroupMsg(group: GroupCodeContainer, msg: MessageContent): Carrier<Flag<MiraiGroupFlagContent>> =
        sendGroupMsg0(group.groupCodeNumber, msg)

    override fun sendGroupMsg(group: GroupCodeContainer, msg: String): Carrier<Flag<MiraiGroupFlagContent>> =
        sendGroupMsg0(group.groupCodeNumber, msg.toMiraiMessageContent(message))

    /**
     * 发送私聊消息。
     */
    private fun sendPrivateMsg0(code: Long, group: Long?, msg: MessageContent): Carrier<Flag<MiraiPrivateFlagContent>> {
        val miraiMsg = runBlocking { msg.toMiraiMessageContent(message) }

        val messageReceipt: MessageReceipt<Contact>? = if (group != null) {
            runBlocking {
                bot.member(group, code).run {
                    if (this is NormalMember) {
                        @Suppress("USELESS_CAST")
                        (this as NormalMember).sendMessage(miraiMsg.getMessage(this))
                    } else {
                        throw IllegalStateException("Only NormalMember supports sendMessage.")
                    }
                }
            }
        } else {
            // 没有指定group, 则判断当前contact
            // 存在contact，且contact不是group且id=code，则说明就是发送给此contact的。
            if (contact !is Group && contact?.id == code) {
                runBlocking {
                    val message: Message = miraiMsg.getMessage(contact)
                    if (message.isNotEmptyMsg()) {
                        contact.sendMessage(message)
                    } else null
                }
            } else {
                // 认为是发送给好友的
                val friend: Friend = bot.friend(code)
                runBlocking {
                    val message: Message = miraiMsg.getMessage(friend)
                    if (message.isNotEmptyMsg()) {
                        friend.sendMessage(message)
                    } else null
                }
            }
        }

        return messageReceipt?.let {
            miraiMessageFlag<MiraiPrivateFlagContent> { MiraiPrivateFlagContent(it.source) }
        }.toCarrier()
    }

    override fun sendPrivateMsg(code: String, group: String?, msg: String): Carrier<Flag<MiraiPrivateFlagContent>> =
        sendPrivateMsg0(code.toLong(), group?.toLong(), msg.toMiraiMessageContent(message))

    override fun sendPrivateMsg(code: Long, group: Long?, msg: String): Carrier<Flag<MiraiPrivateFlagContent>> =
        sendPrivateMsg0(code, group, msg.toMiraiMessageContent(message))

    override fun sendPrivateMsg(
        code: String,
        group: String?,
        msg: MessageContent
    ): Carrier<Flag<MiraiPrivateFlagContent>> =
        sendPrivateMsg0(code.toLong(), group?.toLong(), msg)

    override fun sendPrivateMsg(code: Long, group: Long?, msg: MessageContent): Carrier<Flag<MiraiPrivateFlagContent>> =
        sendPrivateMsg0(code, group, msg)

    override fun sendPrivateMsg(
        code: AccountCodeContainer,
        group: GroupCodeContainer?,
        msg: MessageContent
    ): Carrier<Flag<MiraiPrivateFlagContent>> =
        sendPrivateMsg0(code.accountCodeNumber, group?.groupCodeNumber, msg)

    override fun sendPrivateMsg(
        code: AccountCodeContainer,
        group: GroupCodeContainer?,
        msg: String
    ): Carrier<Flag<MiraiPrivateFlagContent>> =
        sendPrivateMsg0(code.accountCodeNumber, group?.groupCodeNumber, msg.toMiraiMessageContent(message))


    /**
     * mirai 仅支持设置新成员入群公告。
     * 且除 [group]、[title]、[text] 以外的其他参数基本无效。
     * (mirai)
     */
    private fun setGroupNewMemberNotice0(
        group: Long,
        title: String?,
        text: String?
    ): Carrier<Boolean> {
        val builder = StringBuilder()
        title?.let { builder.append(it).appendLine().appendLine() }
        text?.let { builder.append(it).appendLine() }
        val noticeText: String = builder.toString()
        bot.group(group).settings.entranceAnnouncement = noticeText
        return true.toCarrier()
    }

    override fun sendGroupNotice(
        group: Long,
        title: String?,
        text: String?,
        popUp: Boolean,
        top: Boolean,
        toNewMember: Boolean,
        confirm: Boolean
    ): Carrier<Boolean> =
        if (toNewMember) setGroupNewMemberNotice0(group, title, text)
        else false.toCarrier()
    override fun sendGroupNotice(
        group: String,
        title: String?,
        text: String?,
        popUp: Boolean,
        top: Boolean,
        toNewMember: Boolean,
        confirm: Boolean
    ): Carrier<Boolean> = sendGroupNotice(group.toLong(), title, text, popUp, top, toNewMember, confirm)
    override fun sendGroupNotice(
        group: GroupCodeContainer,
        title: String?,
        text: String?,
        popUp: Boolean,
        top: Boolean,
        toNewMember: Boolean,
        confirm: Boolean
    ): Carrier<Boolean> = sendGroupNotice(group.groupCodeNumber, title, text, popUp, top, toNewMember, confirm)


    /**
     * mirai 不支持群签到。
     * （mirai v1.3.2）
     */
    @Deprecated("mirai does not support api: group sign.")
    override fun sendGroupSign(group: String, title: String, message: String): Carrier<Boolean> {
        ErrorSender.sendGroupSign(group, title, message)
    }
    /**
     * mirai 不支持群签到。
     * （mirai v1.3.2）
     */
    @Deprecated("mirai does not support api: group sign.")
    override fun sendGroupSign(group: Long, title: String, message: String): Carrier<Boolean> {
        ErrorSender.sendGroupSign(group, title, message)
        return false.toCarrier()
    }
}