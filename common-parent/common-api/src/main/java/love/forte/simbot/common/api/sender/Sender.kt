/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot-S
 *  * File     Sender.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *  *
 *  *
 *
 */

package love.forte.simbot.common.api.sender

import love.forte.simbot.common.api.messages.receipts.GroupMsgReceipts
import love.forte.simbot.common.api.messages.receipts.PrivateMsgReceipts

/**
 *
 * 消息发送用的送信器。一般用来发送消息，例如 私聊、群聊等
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
     * @return GroupMsgReceipts 消息回执
     */
    fun sendGroupMsg(group: String, msg: String): GroupMsgReceipts


    /**
     * 发送一条私聊消息
     * @param code String 好友账号或者接收人账号
     * @param group String? 如果你发送的是一个群临时会话，此参数代表为群号。可以为null。
     * @param msg String  消息正文
     * @return PrivateMsgReceipts 私聊回执
     */
    fun sendPrivateMsg(code: String, group: String?, msg: String): PrivateMsgReceipts


    /**
     * 发布群公告
     * 目前，top、toNewMember、confirm参数是无效的
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
        popUp: Boolean = false,
        top: Boolean = false,
        toNewMember: Boolean = false,
        confirm: Boolean = false
    ): Boolean


}