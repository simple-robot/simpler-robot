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
import love.forte.simbot.api.message.results.Result
import java.util.concurrent.TimeUnit


/**
 * [Setter] 的 无效化实现，所有的方法均会抛出异常。
 */
object ErrorSetter : Setter.Def {
    override suspend fun friendAddRequest(
        flag: Flag<FriendAddRequest.FlagContent>,
        friendRemark: String?,
        agree: Boolean,
        blackList: Boolean,
    ): Carrier<Boolean> =
        NO("Setter.setFriendAddRequest")

    override suspend fun groupAddRequest(
        flag: Flag<GroupAddRequest.FlagContent>,
        agree: Boolean,
        blackList: Boolean,
        why: String?,
    ): Nothing =
        NO("Setter.setGroupAddRequest")

    override suspend fun groupAdmin(groupCode: String, memberCode: String, promotion: Boolean): Nothing =
        NO("Setter.setGroupAdmin")

    override suspend fun groupAnonymous(group: String, agree: Boolean): Nothing =
        NO("Setter.setGroupAnonymous")

    override suspend fun groupBan(groupCode: String, memberCode: String, time: Long, timeUnit: TimeUnit): Nothing =
        NO("Setter.setGroupBan")

    override suspend fun groupWholeBan(groupCode: String, mute: Boolean): Nothing =
        NO("Setter.setGroupWholeBan")

    override suspend fun groupRemark(groupCode: String, memberCode: String, remark: String?): Nothing =
        NO("Setter.setGroupRemark")

    override suspend fun groupQuit(groupCode: String, forcibly: Boolean): Nothing =
        NO("Setter.setGroupLeave")

    override suspend fun groupMemberKick(
        groupCode: String,
        memberCode: String,
        why: String?,
        blackList: Boolean,
    ): Nothing = NO("Setter.setGroupMemberKick")


    override suspend fun groupMemberSpecialTitle(groupCode: String, memberCode: String, title: String?): Nothing =
        NO("Setter.setGroupMemberSpecialTitle")

    override suspend fun msgRecall(flag: MessageGet.MessageFlag<MessageGet.MessageFlagContent>): Nothing =
        NO("Setter.setMsgRecall")

    override suspend fun groupName(groupCode: String, name: String): Nothing =
        NO("Setter.setGroupName")

    /**
     * 删除好友
     */
    override suspend fun friendDelete(friend: String): Nothing =
        NO("Setter.setFriendDelete")

    override suspend fun <R : Result> execute(additionalApi: AdditionalApi<R>): Nothing =
        NO("Setter.additionalApi.${additionalApi.additionalApiName}")
}


/**
 * [ErrorGetter] 的构建工厂，得到的 [Getter] 实例的所有方法均会抛出异常。
 */
@get:JvmName("getErrorSenderFactory")
public val ErrorSetterFactory: DefaultSetterFactory = object : DefaultSetterFactory {
    override fun getOnMsgSetter(msg: MsgGet): Setter.Def = ErrorSetter
    override fun getOnBotSetter(bot: BotContainer): Setter.Def = ErrorSetter
}


