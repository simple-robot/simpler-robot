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

import love.forte.simbot.core.api.message.receipts.GroupMsgReceipt
import love.forte.simbot.core.api.message.receipts.GroupNoticeReceipt
import love.forte.simbot.core.api.message.receipts.GroupSignReceipt
import love.forte.simbot.core.api.message.receipts.PrivateMsgReceipt
import love.forte.simbot.core.api.sender.ErrorSender
import love.forte.simbot.core.api.sender.Sender
import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.Contact


/**
 * mirai 对于 [送信器][Sender] 的实现。
 */
public class MiraiSender(
    private val bot: Bot,
    /** 当前收到的消息的实例。如果是一个botSender则会为null。 */
    private val contact: Contact? = null
) : Sender {


    override fun sendGroupMsg(group: String, msg: String): GroupMsgReceipt {
        TODO("Not yet implemented")
    }


    override fun sendPrivateMsg(code: String, group: String?, msg: String): PrivateMsgReceipt {
        TODO("Not yet implemented")
    }


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