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
import love.forte.simbot.api.message.assists.Flag
import love.forte.simbot.api.message.containers.BotInfo
import love.forte.simbot.api.message.events.FriendAddRequest
import love.forte.simbot.api.message.events.GroupAddRequest
import love.forte.simbot.api.message.events.MessageGet
import love.forte.simbot.api.message.results.*
import love.forte.simbot.api.sender.Getter
import love.forte.simbot.api.sender.Setter
import love.forte.simbot.component.lovelycat.LovelyCatApiTemplate
import love.forte.simbot.component.lovelycat.message.CatGroupInfo
import love.forte.simbot.component.lovelycat.message.event.lovelyCatBotInfo
import love.forte.simbot.component.lovelycat.message.result.*
import java.util.concurrent.TimeUnit


/**
 * 可爱猫置信器。
 */
public class LovelyCatSetter(
    private val botId: String,
    private val api: LovelyCatApiTemplate
) : Setter {
    override fun setFriendAddRequest(
        flag: Flag<FriendAddRequest.FlagContent>,
        friendRemark: String?,
        agree: Boolean,
        blackList: Boolean,
    ): Carrier<Boolean> {
        TODO("Not yet implemented")
    }

    override fun setGroupAddRequest(
        flag: Flag<GroupAddRequest.FlagContent>,
        agree: Boolean,
        blackList: Boolean,
        why: String?,
    ): Carrier<Boolean> {
        TODO("Not yet implemented")
    }

    override fun setGroupAdmin(groupCode: String, memberCode: String, promotion: Boolean): Carrier<Boolean> {
        TODO("Not yet implemented")
    }

    override fun setGroupAnonymous(group: String, agree: Boolean): Carrier<Boolean> {
        TODO("Not yet implemented")
    }

    override fun setGroupBan(groupCode: String, memberCode: String, time: Long, timeUnit: TimeUnit): Carrier<Boolean> {
        TODO("Not yet implemented")
    }

    override fun setGroupWholeBan(groupCode: String, ban: Boolean): Carrier<Boolean> {
        TODO("Not yet implemented")
    }

    override fun setGroupRemark(groupCode: String, memberCode: String, remark: String?): Carrier<String> {
        TODO("Not yet implemented")
    }

    override fun setGroupQuit(groupCode: String, forcibly: Boolean): Carrier<Boolean> {
        TODO("Not yet implemented")
    }

    override fun setGroupMemberKick(
        groupCode: String,
        memberCode: String,
        why: String?,
        blackList: Boolean,
    ): Carrier<Boolean> {
        TODO("Not yet implemented")
    }

    override fun setGroupMemberSpecialTitle(groupCode: String, memberCode: String, title: String?): Carrier<String> {
        TODO("Not yet implemented")
    }

    override fun setMsgRecall(flag: Flag<MessageGet.MessageFlagContent>): Carrier<Boolean> {
        TODO("Not yet implemented")
    }

    override fun setGroupName(groupCode: String, name: String): Carrier<String> {
        TODO("Not yet implemented")
    }
}



