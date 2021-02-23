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


/**
 * 可爱猫置信器。
 */
public class LovelyCatSetter(
    private val botId: String,
    private val api: LovelyCatApiTemplate,
    private val def: Setter
) : Setter {
    override fun setFriendAddRequest(
        flag: Flag<FriendAddRequest.FlagContent>,
        friendRemark: String?,
        agree: Boolean,
        blackList: Boolean,
    ): Carrier<Boolean> {
        api.agreeFriendVerify(botId, flag.flag.id)
        return true.toCarrier()
    }

    override fun setGroupAddRequest(
        flag: Flag<GroupAddRequest.FlagContent>,
        agree: Boolean,
        blackList: Boolean,
        why: String?,
    ): Carrier<Boolean> {
        api.agreeGroupInvite(botId, flag.flag.id)
        return true.toCarrier()
    }

    override fun setGroupAdmin(groupCode: String, memberCode: String, promotion: Boolean): Carrier<Boolean> {
        return def.setGroupAdmin(groupCode, memberCode, promotion)
    }

    override fun setGroupAnonymous(group: String, agree: Boolean): Carrier<Boolean> {
        return def.setGroupAnonymous(group, agree)
    }

    override fun setGroupAnonymous(group: Long, agree: Boolean): Carrier<Boolean> =
        setGroupAnonymous("$group$GROUP_SUFFIX", agree)

    override fun setGroupBan(groupCode: String, memberCode: String, time: Long, timeUnit: TimeUnit): Carrier<Boolean> {
        return def.setGroupBan(groupCode, memberCode, time, timeUnit)
    }


    override fun setGroupWholeBan(groupCode: String, mute: Boolean): Carrier<Boolean> {
        return def.setGroupWholeBan(groupCode, mute)
    }

    override fun setGroupWholeBan(groupCode: Long, mute: Boolean): Carrier<Boolean> =
        setGroupWholeBan("$groupCode$GROUP_SUFFIX", mute)


    override fun setGroupRemark(groupCode: String, memberCode: String, remark: String?): Carrier<String> {
        return def.setGroupRemark(groupCode, memberCode, remark)
    }

    override fun setGroupQuit(groupCode: String, forcibly: Boolean): Carrier<Boolean> {
        api.quitGroup(botId, groupCode)
        // check success?
        return true.toCarrier()
    }

    override fun setGroupQuit(group: GroupCodeContainer, forcibly: Boolean): Carrier<Boolean> =
        setGroupQuit("$group$GROUP_SUFFIX", forcibly)


    override fun setGroupMemberKick(
        groupCode: String,
        memberCode: String,
        why: String?,
        blackList: Boolean,
    ): Carrier<Boolean> {
        api.removeGroupMember(botId, groupCode, memberCode)
        // check success?
        return true.toCarrier()
    }

    override fun setGroupMemberSpecialTitle(groupCode: String, memberCode: String, title: String?): Carrier<String> {
        return def.setGroupMemberSpecialTitle(groupCode, memberCode, title)
    }

    override fun setMsgRecall(flag: Flag<MessageGet.MessageFlagContent>): Carrier<Boolean> {
        return def.setMsgRecall(flag)
    }

    override fun setGroupName(groupCode: String, name: String): Carrier<String> {
        api.modifyGroupName(botId, groupCode, name)
        return name.toCarrier()
    }

    override fun setGroupName(groupCode: Long, name: String): Carrier<String> =
        setGroupName("$groupCode$GROUP_SUFFIX", name)


    /**
     * 删除好友
     */
    override fun setFriendDelete(friend: String): Carrier<Boolean> {
        api.deleteFriend(botId, friend)
        return true.toCarrier()
    }
}



