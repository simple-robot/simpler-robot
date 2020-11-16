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

@file:JvmName("ErrorSetterFactories")

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
 * [Setter] 的 无效化实现，所有的方法均会抛出异常。
 */
object ErrorSetter : Setter {
    override fun setFriendAddRequest(
        flag: Flag<FriendAddRequest.FlagContent>,
        friendRemark: String?,
        agree: Boolean,
        blackList: Boolean
    ): Carrier<Boolean> =
        NO("Setter.setFriendAddRequest")

    override fun setGroupAddRequest(flag: Flag<GroupAddRequest.FlagContent>, agree: Boolean, blackList: Boolean, why: String?): Nothing  =
        NO("Setter.setGroupAddRequest")

    override fun setGroupAdmin(groupCode: String, memberCode: String, promotion: Boolean): Nothing =
        NO("Setter.setGroupAdmin")

    override fun setGroupAnonymous(group: String, agree: Boolean): Nothing =
        NO("Setter.setGroupAnonymous")

    override fun setGroupBan(groupCode: String, memberCode: String, time: Long, timeUnit: TimeUnit): Nothing =
        NO("Setter.setGroupBan")

    override fun setGroupWholeBan(groupCode: String, ban: Boolean): Nothing =
        NO("Setter.setGroupWholeBan")

    override fun setGroupRemark(groupCode: String, memberCode: String, remark: String?): Nothing =
        NO("Setter.setGroupRemark")

    override fun setGroupQuit(groupCode: String, forcibly: Boolean): Nothing =
        NO("Setter.setGroupLeave")

    override fun setGroupMemberKick(
        groupCode: String,
        memberCode: String,
        why: String?,
        blackList: Boolean
    ): Nothing = NO("Setter.setGroupMemberKick")



    override fun setGroupMemberSpecialTitle(groupCode: String, memberCode: String, title: String?): Nothing =
        NO("Setter.setGroupMemberSpecialTitle")

    override fun setMsgRecall(flag: Flag<MessageGet.MessageFlagContent>): Nothing =
        NO("Setter.setMsgRecall")

    override fun setGroupName(groupCode: String, name: String): Nothing =
        NO("Setter.setGroupName")
}



/**
 * [ErrorGetter] 的构建工厂，得到的 [Getter] 实例的所有方法均会抛出异常。
 */
@get:JvmName("getErrorSenderFactory")
public val ErrorSetterFactory : SetterFactory = object : SetterFactory {
    override fun getOnMsgSetter(msg: MsgGet): Setter = ErrorSetter
    override fun getOnBotSetter(bot: BotContainer): Setter = ErrorSetter
}


