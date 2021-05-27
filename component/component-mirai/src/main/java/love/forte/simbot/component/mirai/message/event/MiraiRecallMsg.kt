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
import love.forte.simbot.api.message.containers.*
import love.forte.simbot.api.message.events.GroupMsg
import love.forte.simbot.api.message.events.GroupMsgRecall
import love.forte.simbot.api.message.events.PrivateMsg
import love.forte.simbot.api.message.events.PrivateMsgRecall
import love.forte.simbot.component.mirai.message.MiraiFriendAccountInfo
import love.forte.simbot.component.mirai.message.MiraiMemberAccountInfo
import love.forte.simbot.component.mirai.message.MiraiMessageCache
import love.forte.simbot.component.mirai.message.cacheId
import love.forte.simbot.component.mirai.message.result.MiraiGroupFullInfo
import net.mamoe.mirai.event.events.MessageRecallEvent
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
        /**
         * 被撤回消息的发送者。有可能是bot自己。
         */
        override val accountInfo: AccountInfo = MiraiMemberAccountInfo(event.authorId, event.author)

        override val groupInfo: GroupInfo = MiraiGroupFullInfo(event.group)

        /**
         * 操作者。代表撤回这条消息的人。
         */
        override val operatorInfo: OperatorInfo = with(event.operatorOrBot) { MiraiMemberAccountInfo(id, this) }.asOperator()

        /**
         * 被操作者。代表被撤回消息的人，也是消息的原作者。
         */
        override val beOperatorInfo: BeOperatorInfo = accountInfo.asBeOperator()


        private var _cacheMsg: GroupMsg? = null

        private val cacheMsg: GroupMsg? by lazy(LazyThreadSafetyMode.PUBLICATION) {
            cache.getGroupMsg(event.cacheId)
        }

        /**
         * 通过缓存获取撤回的消息内容。
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

        /** 撤回的操作人，也应该是消息的原作者。是好友。 */
        override val accountInfo: AccountInfo = MiraiFriendAccountInfo(event.operatorId, event.operator)

        private val cacheMsg: PrivateMsg? by lazy(LazyThreadSafetyMode.PUBLICATION) {
            cache.getPrivateMsg(event.cacheId)
        }

        /**
         * 操作者。代表撤回这条消息的人。
         */
        override val operatorInfo: OperatorInfo = accountInfo.asOperator()

        /**
         * 被操作者。代表被撤回消息的人。
         */
        override val beOperatorInfo: BeOperatorInfo = accountInfo.asBeOperator()

        /**
         * 缓存中获取消息。
         */
        override val text: String? get() = cacheMsg?.text

        override val msgContent: MessageContent?
            get() = cacheMsg?.msgContent

        override val recallTime: Long = System.currentTimeMillis()
    }
}



