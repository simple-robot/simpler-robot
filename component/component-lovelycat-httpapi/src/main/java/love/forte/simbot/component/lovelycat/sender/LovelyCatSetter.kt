/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot-S
 *  * File     LovelyCatGetter.kt
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

package love.forte.simbot.component.lovelycat.sender

import love.forte.common.utils.Carrier
import love.forte.common.utils.toCarrier
import love.forte.simbot.api.message.assists.Flag
import love.forte.simbot.api.message.containers.GroupCodeContainer
import love.forte.simbot.api.message.events.FriendAddRequest
import love.forte.simbot.api.message.events.GroupAddRequest
import love.forte.simbot.api.message.events.MessageGet
import love.forte.simbot.api.sender.Setter
import love.forte.simbot.component.lovelycat.LovelyCatApiTemplate
import love.forte.simbot.component.lovelycat.message.event.GROUP_SUFFIX
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext


/**
 * 可爱猫置信器。
 */
public class LovelyCatSetter(
    private val botId: String,
    private val api: LovelyCatApiTemplate,
    private val def: Setter
) : Setter {
    override val coroutineContext: CoroutineContext
        get() = EmptyCoroutineContext

    override suspend fun friendAddRequest(
        flag: Flag<FriendAddRequest.FlagContent>,
        friendRemark: String?,
        agree: Boolean,
        blackList: Boolean,
    ): Carrier<Boolean> {
        api.agreeFriendVerify(botId, flag.flag.id)
        return true.toCarrier()
    }

    override suspend fun groupAddRequest(
        flag: Flag<GroupAddRequest.FlagContent>,
        agree: Boolean,
        blackList: Boolean,
        why: String?,
    ): Carrier<Boolean> {
        api.agreeGroupInvite(botId, flag.flag.id)
        return true.toCarrier()
    }

    override suspend fun groupAdmin(groupCode: String, memberCode: String, promotion: Boolean): Carrier<Boolean> {
        return def.setGroupAdmin(groupCode, memberCode, promotion)
    }

    override suspend fun groupAnonymous(group: String, agree: Boolean): Carrier<Boolean> {
        return def.setGroupAnonymous(group, agree)
    }

    override suspend fun groupAnonymous(group: Long, agree: Boolean): Carrier<Boolean> =
        setGroupAnonymous("$group$GROUP_SUFFIX", agree)

    override suspend fun groupBan(groupCode: String, memberCode: String, time: Long, timeUnit: TimeUnit): Carrier<Boolean> {
        return def.setGroupBan(groupCode, memberCode, time, timeUnit)
    }


    override suspend fun groupWholeBan(groupCode: String, mute: Boolean): Carrier<Boolean> {
        return def.setGroupWholeBan(groupCode, mute)
    }

    override suspend fun groupWholeBan(groupCode: Long, mute: Boolean): Carrier<Boolean> =
        setGroupWholeBan("$groupCode$GROUP_SUFFIX", mute)


    override suspend fun groupRemark(groupCode: String, memberCode: String, remark: String?): Carrier<String> {
        return def.setGroupRemark(groupCode, memberCode, remark)
    }

    override suspend fun groupQuit(groupCode: String, forcibly: Boolean): Carrier<Boolean> {
        api.quitGroup(botId, groupCode)
        // check success?
        return true.toCarrier()
    }

    override suspend fun groupQuit(group: GroupCodeContainer, forcibly: Boolean): Carrier<Boolean> =
        setGroupQuit("$group$GROUP_SUFFIX", forcibly)


    override suspend fun groupMemberKick(
        groupCode: String,
        memberCode: String,
        why: String?,
        blackList: Boolean,
    ): Carrier<Boolean> {
        api.removeGroupMember(botId, groupCode, memberCode)
        // check success?
        return true.toCarrier()
    }

    override suspend fun groupMemberSpecialTitle(groupCode: String, memberCode: String, title: String?): Carrier<String> {
        return def.setGroupMemberSpecialTitle(groupCode, memberCode, title)
    }

    override suspend fun msgRecall(flag: MessageGet.MessageFlag<MessageGet.MessageFlagContent>): Carrier<Boolean> {
        return def.msgRecall(flag)
    }

    override suspend fun groupName(groupCode: String, name: String): Carrier<String> {
        api.modifyGroupName(botId, groupCode, name)
        return name.toCarrier()
    }

    override suspend fun groupName(groupCode: Long, name: String): Carrier<String> =
        setGroupName("$groupCode$GROUP_SUFFIX", name)


    /**
     * 删除好友
     */
    override suspend fun friendDelete(friend: String): Carrier<Boolean> {
        api.deleteFriend(botId, friend)
        return true.toCarrier()
    }
}



