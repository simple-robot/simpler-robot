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
import love.forte.simbot.api.message.containers.GroupInfo
import love.forte.simbot.api.message.events.GroupMsg
import love.forte.simbot.api.message.events.GroupMsgRecall
import love.forte.simbot.api.message.events.PrivateMsg
import love.forte.simbot.api.message.events.PrivateMsgRecall
import love.forte.simbot.component.mirai.message.MiraiFriendAccountInfo
import love.forte.simbot.component.mirai.message.MiraiMemberAccountInfo
import love.forte.simbot.component.mirai.message.MiraiMessageCache
import love.forte.simbot.component.mirai.message.result.MiraiGroupFullInfo
import love.forte.simbot.component.mirai.sender.friend
import net.mamoe.mirai.event.events.MessageRecallEvent
import net.mamoe.mirai.event.events.author
import net.mamoe.mirai.event.events.operatorOrBot


/**
 * mirai的 消息撤回事件。
 */
public sealed class MiraiMsgRecall<E : MessageRecallEvent>(event: E) : AbstractMiraiMsgGet<E>(event) {
    override val id: String = "REC-${event.authorId}.${event.messageIds.joinToString(",")}.${event.messageInternalIds.joinToString(",")}"


    /**
     * 群消息撤回。
     */
    public class GroupRecall(event: MessageRecallEvent.GroupRecall, private val cache: MiraiMessageCache) :
        MiraiMsgRecall<MessageRecallEvent.GroupRecall>(event), GroupMsgRecall {
        /** 有可能是bot自己。 */
        override val accountInfo: AccountInfo = MiraiMemberAccountInfo(event.author)

        override val groupInfo: GroupInfo = MiraiGroupFullInfo(event.group)

        private val cacheMsg: GroupMsg?
            get() = cache.getGroupMsg("${event.authorId}.${event.messageIds.joinToString(",")}.${event.messageInternalIds.joinToString(",")}")

        /**
         * 暂时不支持获取撤回掉的消息。
         */
        override val text: String? get() {
            return cacheMsg?.text
        }


        override val msgContent: MessageContent?
            get() = cacheMsg?.msgContent


        /** 如果操作者与消息作者相同即为主动，否则为被动。 */
        override val groupRecallType: GroupMsgRecall.Type = if (event.operatorOrBot.id == event.authorId) {
            GroupMsgRecall.Type.PROACTIVE
        } else {
            GroupMsgRecall.Type.PASSIVE
        }

        /** 无法获取，即监听触发到的时间。 */
        override val recallTime: Long = System.currentTimeMillis()
    }

    /**
     * 好友消息撤回
     */
    public class FriendRecall(event: MessageRecallEvent.FriendRecall, private val cache: MiraiMessageCache) :
        MiraiMsgRecall<MessageRecallEvent.FriendRecall>(event),
        PrivateMsgRecall {
        /** 撤回的操作人，就是好友。 */
        override val accountInfo: AccountInfo = MiraiFriendAccountInfo(event.bot.friend(event.operator))

        private val cacheMsg: PrivateMsg?
            get() = cache.getPrivateMsg("${event.operator}.${event.messageIds.joinToString(",")}.${event.messageInternalIds.joinToString(",")}")


        /**
         * 缓存中获取消息。
         */
        override val text: String? get() = cacheMsg?.text

        override val msgContent: MessageContent?
            get() = cacheMsg?.msgContent

        override val recallTime: Long = System.currentTimeMillis()
    }
}



