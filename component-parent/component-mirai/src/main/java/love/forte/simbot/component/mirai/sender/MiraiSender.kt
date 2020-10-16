/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     MiraiSender.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.component.mirai.sender

import kotlinx.coroutines.runBlocking
import love.forte.simbot.component.mirai.message.*
import love.forte.simbot.component.mirai.utils.toMiraiMessageContent
import love.forte.simbot.core.api.message.MessageContent
import love.forte.simbot.core.api.message.MsgGet
import love.forte.simbot.core.api.message.assists.Flag
import love.forte.simbot.core.api.message.containers.AccountCodeContainer
import love.forte.simbot.core.api.message.containers.BotContainer
import love.forte.simbot.core.api.message.containers.GroupCodeContainer
import love.forte.simbot.core.api.message.events.GroupMsg
import love.forte.simbot.core.api.message.events.PrivateMsg
import love.forte.simbot.core.api.message.receipts.GroupNoticeReceipt
import love.forte.simbot.core.api.message.receipts.GroupSignReceipt
import love.forte.simbot.core.api.sender.ErrorSender
import love.forte.simbot.core.api.sender.Sender
import love.forte.simbot.core.api.sender.SenderFactory
import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.message.MessageReceipt


public object MiraiSenderFactory : SenderFactory {
    override fun getOnMsgSender(msg: MsgGet): Sender {
        return if (msg is MiraiMessageMsgGet<*>) {
            MiraiSender(Bot.getInstance(msg.botInfo.botCodeNumber), msg.subject)
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
    private val contact: Contact? = null
) : Sender {


    override fun sendGroupMsg(group: String, msg: String): Flag<GroupMsg.FlagContent> {
        TODO("Not yet implemented")
    }


    /**
     * 发送私聊消息。
     */
    private fun sendPrivateMsg0(code: Long, group: Long?, msg: MessageContent): Flag<MiraiPrivateFlagContent> {
        val miraiMsg = runBlocking { msg.toMiraiMessageContent() }

        val messageReceipt: MessageReceipt<Contact> = if(group != null) {
            runBlocking { bot.getGroupMember(group, code).run {
                sendMessage(miraiMsg.getMessage(this))
            } }
        } else {
            // 没有指定group, 则判断当前contact
            // 存在contact，且contact不是group且id=code，则说明就是发送给此contact的。
            if(contact !is Group && contact?.id == code) {
                runBlocking { contact.sendMessage(miraiMsg.getMessage(contact)) }
            } else {
                // 认为是发送给好友的
                val friend = bot.getFriend(code)
                runBlocking { friend.sendMessage(miraiMsg.getMessage(friend)) }
            }
        }

        return miraiMessageFlag<MiraiPrivateFlagContent> { MiraiPrivateFlagContent(messageReceipt.source) }
    }

    override fun sendPrivateMsg(code: String, group: String?, msg: String): Flag<MiraiPrivateFlagContent> =
        sendPrivateMsg0(code.toLong(), group?.toLong(), msg.toMiraiMessageContent())
    override fun sendPrivateMsg(code: Long, group: Long?, msg: String): Flag<PrivateMsg.FlagContent> =
        sendPrivateMsg0(code, group, msg.toMiraiMessageContent())
    override fun sendPrivateMsg(code: String, group: String?, msg: MessageContent): Flag<PrivateMsg.FlagContent> =
        sendPrivateMsg0(code.toLong(), group?.toLong(), msg)
    override fun sendPrivateMsg(code: Long, group: Long?, msg: MessageContent): Flag<PrivateMsg.FlagContent> =
        sendPrivateMsg0(code, group, msg)
    override fun sendPrivateMsg(
        code: AccountCodeContainer,
        group: GroupCodeContainer?,
        msg: MessageContent
    ): Flag<PrivateMsg.FlagContent> =
        sendPrivateMsg0(code.accountCodeNumber, group?.groupCodeNumber, msg)
    override fun sendPrivateMsg(
        code: AccountCodeContainer,
        group: GroupCodeContainer?,
        msg: String
    ): Flag<PrivateMsg.FlagContent> =
        sendPrivateMsg0(code.accountCodeNumber, group?.groupCodeNumber, msg.toMiraiMessageContent())


    /**
     * mirai 仅支持设置新成员入群公告。
     * 因此，只有当参数：[toNewMember] = `true` 的时候此api才可用，
     * 且除 [group]、[text] 以外的其他参数基本无效。
     * (mirai)
     *
     * @throws IllegalStateException [toNewMember] = `false` 时。
     *
     */
    override fun sendGroupNotice(
        group: String,
        title: String?,
        text: String?,
        popUp: Boolean,
        top: Boolean,
        toNewMember: Boolean,
        confirm: Boolean
    ): GroupNoticeReceipt {
        return if(toNewMember) {

            TODO()
        } else {
            throw IllegalStateException("Mirai only supports the announcement setting when `toNewMember(arg5)`=true.")
        }
    }


    /**
     * mirai 不支持群签到。
     * （mirai v1.3.2）
     */
    @Deprecated("mirai does not support api: group sign.")
    override fun sendGroupSign(groupCode: String, title: String, message: String): GroupSignReceipt {
        ErrorSender.sendGroupSign(groupCode, title, message)
    }
}