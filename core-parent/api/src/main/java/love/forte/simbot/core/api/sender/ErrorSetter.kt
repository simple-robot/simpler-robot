/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     ErrorSetter.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

@file:JvmName("ErrorSetterFactories")

package love.forte.simbot.core.api.sender

import love.forte.common.utils.Carrier
import love.forte.simbot.core.api.message.MessageEventGet
import love.forte.simbot.core.api.message.MsgGet
import love.forte.simbot.core.api.message.assists.Flag
import love.forte.simbot.core.api.message.containers.BotContainer
import love.forte.simbot.core.api.message.events.FriendAddRequest
import love.forte.simbot.core.api.message.events.GroupAddRequest


/**
 * [Setter] 的 无效化实现，所有的方法均会抛出异常。
 */
object ErrorSetter : Setter {
    override fun setFriendAddRequest(
        flag: Flag<FriendAddRequest.FlagContent>,
        friendRemark: String?,
        agree: Boolean
    ): Carrier<Boolean> =
        NO("Setter.setFriendAddRequest")

    override fun setGroupAddRequest(flag: Flag<GroupAddRequest.FlagContent>, agree: Boolean): Carrier<Boolean>  =
        NO("Setter.setGroupAddRequest")

    override fun setGroupAdmin(groupCode: String, memberCode: String, promotion: Boolean): Carrier<Boolean> =
        NO("Setter.setGroupAdmin")

    override fun setGroupAnonymous(group: String, agree: Boolean): Carrier<Boolean> =
        NO("Setter.setGroupAnonymous")

    override fun setGroupBan(groupCode: String, memberCode: String, time: Long): Carrier<Boolean> =
        NO("Setter.setGroupBan")

    override fun setGroupWholeBan(groupCode: String, ban: Boolean): Carrier<Boolean> =
        NO("Setter.setGroupWholeBan")

    override fun setGroupRemark(groupCode: String, memberCode: String, remark: String?): Carrier<Boolean> =
        NO("Setter.setGroupRemark")

    override fun setGroupLeave(groupCode: String, forcibly: Boolean): Carrier<Boolean> =
        NO("Setter.setGroupLeave")

    override fun setGroupMemberKick(groupCode: String, memberCode: String, blackList: Boolean): Carrier<Boolean> =
        NO("Setter.setGroupMemberKick")

    override fun setGroupMemberSpecialTitle(groupCode: String, memberCode: String, title: String?): Carrier<Boolean> =
        NO("Setter.setGroupMemberSpecialTitle")

    override fun setMsgRecall(flag: Flag<MessageEventGet.MessageFlagContent>): Carrier<Boolean> =
        NO("Setter.setMsgRecall")

    override fun setGroupName(groupCode: String, name: String): Carrier<Boolean> =
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


