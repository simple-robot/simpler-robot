/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     Sender.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.api.sender

import love.forte.simbot.core.api.message.MessageContent
import love.forte.simbot.core.api.message.assists.Flag
import love.forte.simbot.core.api.message.containers.AccountCodeContainer
import love.forte.simbot.core.api.message.containers.AccountContainer
import love.forte.simbot.core.api.message.containers.GroupCodeContainer
import love.forte.simbot.core.api.message.containers.GroupContainer
import love.forte.simbot.core.api.message.events.GroupMsg
import love.forte.simbot.core.api.message.events.PrivateMsg
import love.forte.simbot.core.api.message.receipts.*

/**
 *
 * 消息发送器。
 * 一般用来发送消息，例如私聊、群聊等。
 *
 * 一般来讲，消息发送后都会有一个方法对应的 [送信器回执][SenderReceipt]。
 *
 * @author ForteScarlet <ForteScarlet@163.com>
 * @date 2020/9/2
 * @since
 */
public interface Sender {

    /**
     * 发送一条群消息。
     * @param group String 群号
     * @param msg String   消息正文
     * @return GroupMsgReceipts 发出的消息的标识，可用于消息撤回。
     */
    fun sendGroupMsg(group: String, msg: String): Flag<GroupMsg.FlagContent>
    /* 下面都是重载。 */
    @JvmDefault
    fun sendGroupMsg(group: Long, msg: String): Flag<GroupMsg.FlagContent> =
        sendGroupMsg(group.toString(), msg)
    @JvmDefault
    fun sendGroupMsg(group: String, msg: MessageContent): Flag<GroupMsg.FlagContent> =
        sendGroupMsg(group, msg.msg ?: throw IllegalArgumentException("msg is Empty."))
    @JvmDefault
    fun sendGroupMsg(group: Long, msg: MessageContent): Flag<GroupMsg.FlagContent> =
        sendGroupMsg(group.toString(), msg)
    @JvmDefault
    fun sendGroupMsg(group: GroupCodeContainer, msg: String): Flag<GroupMsg.FlagContent> =
        sendGroupMsg(group.groupCode, msg)
    @JvmDefault
    fun sendGroupMsg(group: GroupContainer, msg: String): Flag<GroupMsg.FlagContent> =
        sendGroupMsg(group.groupInfo, msg)
    @JvmDefault
    fun sendGroupMsg(group: GroupCodeContainer, msg: MessageContent): Flag<GroupMsg.FlagContent> =
        sendGroupMsg(group.groupCode, msg)
    @JvmDefault
    fun sendGroupMsg(group: GroupContainer, msg: MessageContent): Flag<GroupMsg.FlagContent> =
        sendGroupMsg(group.groupInfo, msg)


    /**
     * 发送一条私聊消息
     * @param code String 好友账号或者接收人账号
     * @param group String? 如果你发送的是一个群临时会话，此参数代表为群号。可以为null。
     * @param msg String  消息正文
     * @return PrivateMsgReceipts 私聊回执
     */
    fun sendPrivateMsg(code: String, group: String?, msg: String): Flag<PrivateMsg.FlagContent>
    /* 下面都是重载。 */
    @JvmDefault
    fun sendPrivateMsg(code: Long, group: Long?, msg: String): Flag<PrivateMsg.FlagContent> =
        sendPrivateMsg(code.toString(), group?.toString(), msg)
    @JvmDefault
    fun sendPrivateMsg(code: String, group: String?, msg: MessageContent): Flag<PrivateMsg.FlagContent> =
        sendPrivateMsg(code, group, msg.msg ?: throw IllegalArgumentException("msg is Empty."))
    @JvmDefault
    fun sendPrivateMsg(code: Long, group: Long?, msg: MessageContent): Flag<PrivateMsg.FlagContent> =
        sendPrivateMsg(code.toString(), group?.toString(), msg)
    @JvmDefault
    fun sendPrivateMsg(code: AccountCodeContainer, group: GroupCodeContainer?, msg: String): Flag<PrivateMsg.FlagContent> =
        sendPrivateMsg(code.accountCode, group?.groupCode, msg)
    @JvmDefault
    fun sendPrivateMsg(code: AccountContainer, group: GroupContainer?, msg: String): Flag<PrivateMsg.FlagContent> =
        sendPrivateMsg(code.accountInfo, group?.groupInfo, msg)
    @JvmDefault
    fun sendPrivateMsg(code: AccountCodeContainer, group: GroupCodeContainer?, msg: MessageContent): Flag<PrivateMsg.FlagContent> =
        sendPrivateMsg(code.accountCode, group?.groupCode, msg)
    @JvmDefault
    fun sendPrivateMsg(code: AccountContainer, group: GroupContainer?, msg: MessageContent): Flag<PrivateMsg.FlagContent> =
        sendPrivateMsg(code.accountInfo, group?.groupInfo, msg)
    /* group 为null的重载。 */
    @JvmDefault
    fun sendPrivateMsg(code: String, msg: String): Flag<PrivateMsg.FlagContent> =
        sendPrivateMsg(code, null, msg)
    @JvmDefault
    fun sendPrivateMsg(code: Long, msg: String): Flag<PrivateMsg.FlagContent> =
        sendPrivateMsg(code.toString(), null, msg)
    @JvmDefault
    fun sendPrivateMsg(code: String, msg: MessageContent): Flag<PrivateMsg.FlagContent> =
        sendPrivateMsg(code, null, msg.msg ?: throw IllegalArgumentException("msg is Empty."))
    @JvmDefault
    fun sendPrivateMsg(code: Long, msg: MessageContent): Flag<PrivateMsg.FlagContent> =
        sendPrivateMsg(code.toString(), null, msg)
    @JvmDefault
    fun sendPrivateMsg(code: AccountCodeContainer, msg: String): Flag<PrivateMsg.FlagContent> =
        sendPrivateMsg(code.accountCode, null, msg)
    @JvmDefault
    fun sendPrivateMsg(code: AccountContainer, msg: String): Flag<PrivateMsg.FlagContent> =
        sendPrivateMsg(code.accountInfo, null, msg)
    @JvmDefault
    fun sendPrivateMsg(code: AccountCodeContainer, msg: MessageContent): Flag<PrivateMsg.FlagContent> =
        sendPrivateMsg(code.accountCode, null, msg)
    @JvmDefault
    fun sendPrivateMsg(code: AccountContainer, msg: MessageContent): Flag<PrivateMsg.FlagContent> =
        sendPrivateMsg(code.accountInfo, null, msg)

    /**
     * 发布群公告
     * @param group 群号
     * @param title 标题
     * @param text   正文
     * @param popUp  是否弹出窗口提醒，默认应为false
     * @param top    是否置顶，默认应为false
     * @param toNewMember 是否发给新成员 默认应为false
     * @param confirm 是否需要确认 默认应为false
     * @return 是否发布成功
     */
    fun sendGroupNotice(
        group: String,
        title: String?,
        text: String?,
        popUp: Boolean,
        top: Boolean,
        toNewMember: Boolean,
        confirm: Boolean
    ): GroupNoticeReceipt

    /**
     * 发布群公告
     * @param group 群号
     * @param title 标题
     * @param text   正文
     * @return 是否发布成功
     */
    @JvmDefault
    fun sendGroupNotice(
        group: String,
        title: String?,
        text: String?
    ): GroupNoticeReceipt = sendGroupNotice(
        group, title, text,
        popUp = false,
        top = false,
        toNewMember = false,
        confirm = false
    )

    /**
     * 设置群签到
     *
     * @param groupCode 群号
     * @param title     签到内容标题
     * @param message   签到内容文本
     */
    fun sendGroupSign(groupCode: String, title: String, message: String): GroupSignReceipt

}