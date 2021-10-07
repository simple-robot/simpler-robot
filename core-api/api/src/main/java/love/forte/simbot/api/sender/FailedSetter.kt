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
public object FailedSetter : Setter.Def {
    override suspend fun friendAddRequest(
        flag: Flag<FriendAddRequest.FlagContent>,
        friendRemark: String?,
        agree: Boolean,
        blackList: Boolean,
    ): Carrier<Boolean> = FalseCarrier

    override suspend fun groupAddRequest(
        flag: Flag<GroupAddRequest.FlagContent>,
        agree: Boolean,
        blackList: Boolean,
        why: String?,
    ): Carrier<Boolean> = FalseCarrier

    override suspend fun groupAdmin(groupCode: String, memberCode: String, promotion: Boolean): Carrier<Boolean> =
        FalseCarrier

    override suspend fun groupAnonymous(group: String, agree: Boolean): Carrier<Boolean> = FalseCarrier

    override suspend fun groupBan(groupCode: String, memberCode: String, time: Long, timeUnit: TimeUnit): Carrier<Boolean> =
        FalseCarrier

    override suspend fun groupWholeBan(groupCode: String, mute: Boolean): Carrier<Boolean> = FalseCarrier

    override suspend fun groupRemark(groupCode: String, memberCode: String, remark: String?): Carrier<String> =
        Carrier.empty()

    override suspend fun groupQuit(groupCode: String, forcibly: Boolean): Carrier<Boolean> = FalseCarrier

    override suspend fun groupMemberKick(
        groupCode: String,
        memberCode: String,
        why: String?,
        blackList: Boolean,
    ): Carrier<Boolean> = FalseCarrier

    override suspend fun groupMemberSpecialTitle(groupCode: String, memberCode: String, title: String?): Carrier<String> =
        Carrier.empty()

    override suspend fun msgRecall(flag: MessageGet.MessageFlag<MessageGet.MessageFlagContent>): Carrier<Boolean> = FalseCarrier

    override suspend fun groupName(groupCode: String, name: String): Carrier<String> = Carrier.empty()

    /**
     * 删除好友
     */
    override suspend fun friendDelete(friend: String): Carrier<Boolean> = FalseCarrier
}



/**
 * 获取一个总是返回失败默认值的实现类。
 */
@get:JvmName("getFailedSetterFactory")
public val FailedSetterFactory : DefaultSetterFactory = object: DefaultSetterFactory {
    override fun getOnMsgSetter(msg: MsgGet): Setter.Def = FailedSetter
    override fun getOnBotSetter(bot: BotContainer): Setter.Def = FailedSetter
}

