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
import love.forte.simbot.api.message.containers.BotContainer
import love.forte.simbot.api.message.events.FriendAddRequest
import love.forte.simbot.api.message.events.GroupAddRequest
import love.forte.simbot.api.message.events.MessageGet
import love.forte.simbot.api.message.events.MsgGet
import java.util.concurrent.TimeUnit

/**
 * [Setter] 的无效化实现，返回失败或空的默认值。
 */
public object FailedSetter : Setter {
    override fun setFriendAddRequest(
        flag: Flag<FriendAddRequest.FlagContent>,
        friendRemark: String?,
        agree: Boolean,
        blackList: Boolean,
    ): Carrier<Boolean> = FalseCarrier

    override fun setGroupAddRequest(
        flag: Flag<GroupAddRequest.FlagContent>,
        agree: Boolean,
        blackList: Boolean,
        why: String?,
    ): Carrier<Boolean> = FalseCarrier

    override fun setGroupAdmin(groupCode: String, memberCode: String, promotion: Boolean): Carrier<Boolean> =
        FalseCarrier

    override fun setGroupAnonymous(group: String, agree: Boolean): Carrier<Boolean> = FalseCarrier

    override fun setGroupBan(groupCode: String, memberCode: String, time: Long, timeUnit: TimeUnit): Carrier<Boolean> =
        FalseCarrier

    override fun setGroupWholeBan(groupCode: String, ban: Boolean): Carrier<Boolean> = FalseCarrier

    override fun setGroupRemark(groupCode: String, memberCode: String, remark: String?): Carrier<String> =
        Carrier.empty()

    override fun setGroupQuit(groupCode: String, forcibly: Boolean): Carrier<Boolean> = FalseCarrier

    override fun setGroupMemberKick(
        groupCode: String,
        memberCode: String,
        why: String?,
        blackList: Boolean,
    ): Carrier<Boolean> = FalseCarrier

    override fun setGroupMemberSpecialTitle(groupCode: String, memberCode: String, title: String?): Carrier<String> =
        Carrier.empty()

    override fun setMsgRecall(flag: Flag<MessageGet.MessageFlagContent>): Carrier<Boolean> = FalseCarrier

    override fun setGroupName(groupCode: String, name: String): Carrier<String> = Carrier.empty()

    /**
     * 删除好友
     */
    override fun setFriendDelete(friend: String): Carrier<Boolean> = FalseCarrier
}



/**
 * 获取一个总是返回失败默认值的实现类。
 */
@get:JvmName("getFailedSetterFactory")
public val FailedSetterFactory : SetterFactory = object: SetterFactory {
    override fun getOnMsgSetter(msg: MsgGet): Setter = FailedSetter
    override fun getOnBotSetter(bot: BotContainer): Setter = FailedSetter
}

