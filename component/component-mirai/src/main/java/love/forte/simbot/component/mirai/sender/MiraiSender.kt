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
import love.forte.simbot.api.message.containers.AccountCodeContainer
import love.forte.simbot.api.message.containers.BotContainer
import love.forte.simbot.api.message.containers.GroupCodeContainer
import love.forte.simbot.api.message.events.MsgGet
import love.forte.simbot.api.message.results.Result
import love.forte.simbot.api.sender.AdditionalApi
import love.forte.simbot.api.sender.Sender
import love.forte.simbot.api.sender.SenderFactory
import love.forte.simbot.component.mirai.additional.MiraiSenderAdditionalApi
import love.forte.simbot.component.mirai.additional.SenderInfo
import love.forte.simbot.component.mirai.message.*
import love.forte.simbot.component.mirai.message.event.AbstractMiraiMsgGet
import love.forte.simbot.component.mirai.message.event.MiraiGroupFlagContent
import love.forte.simbot.component.mirai.message.event.MiraiMessageMsgGet
import love.forte.simbot.component.mirai.message.event.MiraiPrivateFlagContent
import love.forte.simbot.component.mirai.utils.toMiraiMessageContent
import love.forte.simbot.core.TypedCompLogger
import love.forte.simbot.processor.RemoteResourceInProcessor
import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.User
import net.mamoe.mirai.message.MessageReceipt
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.isContentEmpty


public class MiraiSenderFactory(
    private val cache: MiraiMessageCache,
    private val remoteResourceInProcessor: RemoteResourceInProcessor,
) : SenderFactory {
    override fun getOnMsgSender(msg: MsgGet, def: Sender.Def): Sender {
        return when (msg) {
            is MiraiMessageMsgGet<*> -> {
                MiraiSender(msg.event.bot, msg.subject, msg.message, def, cache, remoteResourceInProcessor)
            }
            is AbstractMiraiMsgGet<*> -> {
                MiraiSender(msg.event.bot,
                    defSender = def,
                    cache = cache,
                    remoteResourceInProcessor = remoteResourceInProcessor)
            }
            else -> {
                MiraiSender(Bot.getInstance(msg.botInfo.botCodeNumber),
                    defSender = def,
                    cache = cache,
                    remoteResourceInProcessor = remoteResourceInProcessor)
            }
        }
    }

    override fun getOnBotSender(bot: BotContainer, def: Sender.Def): Sender =
        MiraiSender(Bot.getInstance(bot.botInfo.botCodeNumber),
            defSender = def,
            cache = cache,
            remoteResourceInProcessor = remoteResourceInProcessor)
}


/**
 * mirai 对于 [送信器][Sender] 的实现。
 */
public class MiraiSender(
    private val bot: Bot,
    /** 当前收到的消息的实例。如果是一个botSender则会为null。 */
    private val contact: Contact? = null,
    /** 当前收到的消息。如果是一个botSender则会为null。 */
    private val message: MessageChain? = null,
    /** 默认送信器。 */
    private val defSender: Sender,

    private val cache: MiraiMessageCache,

    private val remoteResourceInProcessor: RemoteResourceInProcessor,
) : Sender {

    private companion object : TypedCompLogger(MiraiSender::class.java)


    private val senderInfo = SenderInfo(bot, contact, message, cache)


    /**
     * 发送群聊消息。
     */
    private fun sendGroupMsg0(group: Long, msg: MessageContent): Carrier<MiraiGroupMsgFlag> {
        logger.debug("Group 1 -> {} in {}", Thread.currentThread().name, group)
        return runBlocking {
            logger.debug("Group 2 -> {} in {}", Thread.currentThread().name, group)
            val miraiMsg = msg.toMiraiMessageContent(message, cache, remoteResourceInProcessor)
            // get group.
            val g: Group = bot.group(group)
            val message: Message = miraiMsg.getMessage(g)
            val messageReceipt = if (message.isNotEmptyMsg()) {
                g.sendMessage(message)
            } else null

            messageReceipt?.let {
                miraiGroupFlag { MiraiGroupFlagContent(it.source) }
            }.toCarrier()
        }
    }

    override fun sendGroupMsg(group: String, msg: String) =
        sendGroupMsg0(group.toLong(), msg.toMiraiMessageContent(message, cache, remoteResourceInProcessor))

    override fun sendGroupMsg(group: String, msg: MessageContent) =
        sendGroupMsg0(group.toLong(), msg)

    override fun sendGroupMsg(group: Long, msg: String) =
        sendGroupMsg0(group, msg.toMiraiMessageContent(message, cache, remoteResourceInProcessor))

    override fun sendGroupMsg(group: Long, msg: MessageContent) =
        sendGroupMsg0(group, msg)

    override fun sendGroupMsg(group: GroupCodeContainer, msg: MessageContent) =
        sendGroupMsg0(group.groupCodeNumber, msg)

    override fun sendGroupMsg(group: GroupCodeContainer, msg: String) =
        sendGroupMsg0(group.groupCodeNumber, msg.toMiraiMessageContent(message, cache, remoteResourceInProcessor))

    /**
     * 发送私聊消息。
     */
    private fun sendPrivateMsg0(code: Long, group: Long?, msg: MessageContent): Carrier<MiraiPrivateMsgFlag> {
        logger.debug("Private 1 -> {} in {}", Thread.currentThread().name, code)
        return runBlocking {
            logger.debug("Private 2 -> {} in {}", Thread.currentThread().name, code)
            val miraiMsg = msg.toMiraiMessageContent(message, cache, remoteResourceInProcessor)
            val messageReceipt: MessageReceipt<Contact>? = if (group != null) {
                bot.member(group, code).run {
                    sendMessage(miraiMsg.getMessage(this))
                }
            } else {
                // 没有指定group, 则判断当前contact
                // 存在contact，且contact不是group且id=code，则说明就是发送给此contact的。
                if (contact !is Group && contact?.id == code) {
                    val message: Message = miraiMsg.getMessage(contact)
                    if (!message.isContentEmpty()) {
                        contact.sendMessage(message)
                    } else null
                } else {

                    // 认为是发送给好友的
                    val friend: User = bot.friendOrNull(code)
                        ?: (if (contact is Group) {
                            contact.memberOrNull(code)
                        } else null)
                        ?: bot.getStranger(code)
                        ?: throw NoSuchElementException("User($code)${if (contact is Group) " or Member on Group(${contact.id})" else ""}")
                    val message: Message = miraiMsg.getMessage(friend)
                    if (message.isNotEmptyMsg()) {
                        friend.sendMessage(message)
                    } else null
                }
            }

            messageReceipt?.let {
                miraiPrivateFlag { MiraiPrivateFlagContent(it.source) }
            }.toCarrier()
        }

    }

    override fun sendPrivateMsg(code: String, group: String?, msg: String) =
        sendPrivateMsg0(code.toLong(),
            group?.toLong(),
            msg.toMiraiMessageContent(message, remoteResourceInProcessor = remoteResourceInProcessor))

    override fun sendPrivateMsg(code: Long, group: Long?, msg: String) =
        sendPrivateMsg0(code,
            group,
            msg.toMiraiMessageContent(message, remoteResourceInProcessor = remoteResourceInProcessor))

    override fun sendPrivateMsg(
        code: String,
        group: String?,
        msg: MessageContent,
    ) =
        sendPrivateMsg0(code.toLong(), group?.toLong(), msg)

    override fun sendPrivateMsg(code: Long, group: Long?, msg: MessageContent) =
        sendPrivateMsg0(code, group, msg)

    override fun sendPrivateMsg(
        code: AccountCodeContainer,
        group: GroupCodeContainer?,
        msg: MessageContent,
    ) =
        sendPrivateMsg0(code.accountCodeNumber, group?.groupCodeNumber, msg)

    override fun sendPrivateMsg(
        code: AccountCodeContainer,
        group: GroupCodeContainer?,
        msg: String,
    ) =
        sendPrivateMsg0(code.accountCodeNumber,
            group?.groupCodeNumber,
            msg.toMiraiMessageContent(message, remoteResourceInProcessor = remoteResourceInProcessor))


    /**
     * mirai 仅支持设置新成员入群公告。
     * 且除 [group]、[title]、[text] 以外的其他参数基本无效。
     * (mirai)
     */
    private fun setGroupNewMemberNotice0(
        group: Long,
        title: String?,
        text: String?,
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
        confirm: Boolean,
    ): Carrier<Boolean> =
        if (toNewMember) setGroupNewMemberNotice0(group, title, text)
        else defSender.sendGroupNotice(group, title, text, popUp, top, toNewMember, confirm)
    // false.toCarrier()

    override fun sendGroupNotice(
        group: String,
        title: String?,
        text: String?,
        popUp: Boolean,
        top: Boolean,
        toNewMember: Boolean,
        confirm: Boolean,
    ): Carrier<Boolean> = sendGroupNotice(group.toLong(), title, text, popUp, top, toNewMember, confirm)

    override fun sendGroupNotice(
        group: GroupCodeContainer,
        title: String?,
        text: String?,
        popUp: Boolean,
        top: Boolean,
        toNewMember: Boolean,
        confirm: Boolean,
    ): Carrier<Boolean> = sendGroupNotice(group.groupCodeNumber, title, text, popUp, top, toNewMember, confirm)


    /**
     * mirai 不支持群签到。
     * （mirai v1.3.2）
     */
    @Deprecated("mirai does not support api: group sign.")
    override fun sendGroupSign(group: String, title: String, message: String): Carrier<Boolean> {
        return defSender.sendGroupSign(group, title, message)
    }

    /**
     * mirai 不支持群签到。
     * （mirai v1.3.2）
     */
    @Deprecated("mirai does not support api: group sign.")
    override fun sendGroupSign(group: Long, title: String, message: String): Carrier<Boolean> {
        return defSender.sendGroupSign(group, title, message)
    }

    override fun <R : Result> additionalExecute(additionalApi: AdditionalApi<R>): R {
        if (additionalApi is MiraiSenderAdditionalApi) {
            return additionalApi.execute(senderInfo)
        }
        return super.additionalExecute(additionalApi)
    }

}