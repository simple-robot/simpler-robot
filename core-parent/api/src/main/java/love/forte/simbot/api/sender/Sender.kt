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

package love.forte.simbot.api.sender

import love.forte.common.utils.Carrier
import love.forte.simbot.api.message.assists.Flag
import love.forte.simbot.api.message.containers.AccountCodeContainer
import love.forte.simbot.api.message.containers.AccountContainer
import love.forte.simbot.api.message.containers.GroupCodeContainer
import love.forte.simbot.api.message.containers.GroupContainer
import love.forte.simbot.api.message.events.GroupMsg
import love.forte.simbot.api.message.MessageContent
import love.forte.simbot.api.message.events.PrivateMsg
import love.forte.simbot.api.message.receipts.SenderReceipt

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
     * @return Carrier<Flag<GroupMsg.FlagContent>>. Flag为发出的消息的标识，可用于消息撤回。但是有可能为null（例如发送的消息无法撤回，比如戳一戳之类的，或者压根不支持撤回的）。
     */
    fun sendGroupMsg(group: String, msg: String): Carrier<out Flag<GroupMsg.FlagContent>>

    /* 下面都是重载。 */
    @JvmDefault
    fun sendGroupMsg(group: Long, msg: String): Carrier<out Flag<GroupMsg.FlagContent>> =
        sendGroupMsg(group.toString(), msg)

    @JvmDefault
    fun sendGroupMsg(group: String, msg: MessageContent): Carrier<out Flag<GroupMsg.FlagContent>> =
        sendGroupMsg(group, msg.msg ?: throw IllegalArgumentException("msg is Empty."))

    @JvmDefault
    fun sendGroupMsg(group: Long, msg: MessageContent): Carrier<out Flag<GroupMsg.FlagContent>> =
        sendGroupMsg(group.toString(), msg)

    @JvmDefault
    fun sendGroupMsg(group: GroupCodeContainer, msg: String): Carrier<out Flag<GroupMsg.FlagContent>> =
        sendGroupMsg(group.groupCode, msg)

    @JvmDefault
    fun sendGroupMsg(group: GroupContainer, msg: String): Carrier<out Flag<GroupMsg.FlagContent>> =
        sendGroupMsg(group.groupInfo, msg)

    @JvmDefault
    fun sendGroupMsg(group: GroupCodeContainer, msg: MessageContent): Carrier<out Flag<GroupMsg.FlagContent>> =
        sendGroupMsg(group.groupCode, msg)

    @JvmDefault
    fun sendGroupMsg(group: GroupContainer, msg: MessageContent): Carrier<out Flag<GroupMsg.FlagContent>> =
        sendGroupMsg(group.groupInfo, msg)


    /**
     * 发送一条私聊消息
     * @param code String 好友账号或者接收人账号
     * @param group String? 如果你发送的是一个群临时会话，此参数代表为群号。可以为null。
     * @param msg String  消息正文
     * @return PrivateMsgReceipts 私聊回执
     */
    fun sendPrivateMsg(code: String, group: String?, msg: String): Carrier<out Flag<PrivateMsg.FlagContent>>

    /* 下面都是重载。 */
    @JvmDefault
    fun sendPrivateMsg(code: Long, group: Long?, msg: String): Carrier<out Flag<PrivateMsg.FlagContent>> =
        sendPrivateMsg(code.toString(), group?.toString(), msg)

    @JvmDefault
    fun sendPrivateMsg(code: String, group: String?, msg: MessageContent): Carrier<out Flag<PrivateMsg.FlagContent>> =
        sendPrivateMsg(code, group, msg.msg ?: throw IllegalArgumentException("msg is Empty."))

    @JvmDefault
    fun sendPrivateMsg(code: Long, group: Long?, msg: MessageContent): Carrier<out Flag<PrivateMsg.FlagContent>> =
        sendPrivateMsg(code.toString(), group?.toString(), msg)

    @JvmDefault
    fun sendPrivateMsg(
        code: AccountCodeContainer,
        group: GroupCodeContainer?,
        msg: String
    ): Carrier<out Flag<PrivateMsg.FlagContent>> =
        sendPrivateMsg(code.accountCode, group?.groupCode, msg)

    @JvmDefault
    fun sendPrivateMsg(
        code: AccountContainer,
        group: GroupContainer?,
        msg: String
    ): Carrier<out Flag<PrivateMsg.FlagContent>> =
        sendPrivateMsg(code.accountInfo, group?.groupInfo, msg)

    @JvmDefault
    fun sendPrivateMsg(
        code: AccountCodeContainer,
        group: GroupCodeContainer?,
        msg: MessageContent
    ): Carrier<out Flag<PrivateMsg.FlagContent>> =
        sendPrivateMsg(code.accountCode, group?.groupCode, msg)

    @JvmDefault
    fun sendPrivateMsg(
        code: AccountContainer,
        group: GroupContainer?,
        msg: MessageContent
    ): Carrier<out Flag<PrivateMsg.FlagContent>> =
        sendPrivateMsg(code.accountInfo, group?.groupInfo, msg)

    /* group 为null的重载。 */
    @JvmDefault
    fun sendPrivateMsg(code: String, msg: String): Carrier<out Flag<PrivateMsg.FlagContent>> =
        sendPrivateMsg(code, null, msg)

    @JvmDefault
    fun sendPrivateMsg(code: Long, msg: String): Carrier<out Flag<PrivateMsg.FlagContent>> =
        sendPrivateMsg(code.toString(), null, msg)

    @JvmDefault
    fun sendPrivateMsg(code: String, msg: MessageContent): Carrier<out Flag<PrivateMsg.FlagContent>> =
        sendPrivateMsg(code, null, msg.msg ?: throw IllegalArgumentException("msg is Empty."))

    @JvmDefault
    fun sendPrivateMsg(code: Long, msg: MessageContent): Carrier<out Flag<PrivateMsg.FlagContent>> =
        sendPrivateMsg(code.toString(), null, msg)

    @JvmDefault
    fun sendPrivateMsg(code: AccountCodeContainer, msg: String): Carrier<out Flag<PrivateMsg.FlagContent>> =
        sendPrivateMsg(code.accountCode, null, msg)

    @JvmDefault
    fun sendPrivateMsg(code: AccountContainer, msg: String): Carrier<out Flag<PrivateMsg.FlagContent>> =
        sendPrivateMsg(code.accountInfo, null, msg)

    @JvmDefault
    fun sendPrivateMsg(code: AccountCodeContainer, msg: MessageContent): Carrier<out Flag<PrivateMsg.FlagContent>> =
        sendPrivateMsg(code.accountCode, null, msg)

    @JvmDefault
    fun sendPrivateMsg(code: AccountContainer, msg: MessageContent): Carrier<out Flag<PrivateMsg.FlagContent>> =
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
    ): Carrier<Boolean>

    @JvmDefault
    fun sendGroupNotice(
        group: Long,
        title: String?,
        text: String?,
        popUp: Boolean,
        top: Boolean,
        toNewMember: Boolean,
        confirm: Boolean
    ): Carrier<Boolean> = sendGroupNotice(group.toString(), title, text, popUp, top, toNewMember, confirm)
    @JvmDefault
    fun sendGroupNotice(
        group: GroupCodeContainer,
        title: String?,
        text: String?,
        popUp: Boolean,
        top: Boolean,
        toNewMember: Boolean,
        confirm: Boolean
    ): Carrier<Boolean> = sendGroupNotice(group.groupCode, title, text, popUp, top, toNewMember, confirm)
    @JvmDefault
    fun sendGroupNotice(
        group: GroupContainer,
        title: String?,
        text: String?,
        popUp: Boolean,
        top: Boolean,
        toNewMember: Boolean,
        confirm: Boolean
    ): Carrier<Boolean> = sendGroupNotice(group.groupInfo, title, text, popUp, top, toNewMember, confirm)


    /**
     * 设置群签到
     *
     * @param group 群号
     * @param title     签到内容标题
     * @param message   签到内容文本
     */
    fun sendGroupSign(group: String, title: String, message: String): Carrier<Boolean>
    @JvmDefault
    fun sendGroupSign(group: Long, title: String, message: String): Carrier<Boolean> =
        sendGroupSign(group.toString(), title, message)
    @JvmDefault
    fun sendGroupSign(group: GroupCodeContainer, title: String, message: String): Carrier<Boolean> =
        sendGroupSign(group.groupCode, title, message)
    @JvmDefault
    fun sendGroupSign(group: GroupContainer, title: String, message: String): Carrier<Boolean> =
        sendGroupSign(group.groupInfo, title, message)

}