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

import love.forte.simbot.api.message.containers.AccountInfo
import love.forte.simbot.api.message.events.GroupMsgRecall
import love.forte.simbot.api.message.events.PrivateMsgRecall
import love.forte.simbot.component.mirai.message.MiraiFriendAccountInfo
import love.forte.simbot.component.mirai.message.MiraiMemberAccountInfo
import net.mamoe.mirai.event.events.MessageRecallEvent
import net.mamoe.mirai.event.events.author
import net.mamoe.mirai.event.events.operatorOrBot


/**
 * mirai的 消息撤回事件。
 */
public sealed class MiraiMsgRecall<E : MessageRecallEvent>(event: E) : AbstractMiraiMsgGet<E>(event) {
    override val id: String = "REC-${event.authorId}.${event.messageId}.${event.messageInternalId}"

    /** 群消息撤回。 */
    public class GroupRecall(event: MessageRecallEvent.GroupRecall) :
        MiraiMsgRecall<MessageRecallEvent.GroupRecall>(event), GroupMsgRecall {
        /** 有可能是bot自己。 */
        override val accountInfo: AccountInfo = MiraiMemberAccountInfo(event.author)

        /** 暂时不支持获取撤回掉的消息。 */
        override val text: String? get() = null

        /** 如果操作者与消息作者相同即为主动，否则为被动。 */
        override val groupRecallType: GroupMsgRecall.Type = if (event.operatorOrBot.id == event.authorId) {
            GroupMsgRecall.Type.PROACTIVE
        } else {
            GroupMsgRecall.Type.PASSIVE
        }

        /** 无法获取，即监听触发到的时间。 */
        override val recallTime: Long = System.currentTimeMillis()
    }

    /** 好友消息撤回 */
    public class FriendRecall(event: MessageRecallEvent.FriendRecall) :
        MiraiMsgRecall<MessageRecallEvent.FriendRecall>(event),
        PrivateMsgRecall {
        /** 撤回的操作人，就是好友。 */
        override val accountInfo: AccountInfo = MiraiFriendAccountInfo(event.bot.getFriend(event.operator))
        /** 暂时不支持获取消息原文。 */
        override val text: String? get() = null

        override val recallTime: Long = System.currentTimeMillis()
    }
}



