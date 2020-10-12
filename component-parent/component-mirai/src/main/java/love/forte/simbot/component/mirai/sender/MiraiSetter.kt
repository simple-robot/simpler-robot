/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     MiraiSetter.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.component.mirai.sender

import love.forte.common.utils.Carrier
import love.forte.simbot.core.api.message.MessageEventGet
import love.forte.simbot.core.api.message.assists.Flag
import love.forte.simbot.core.api.message.events.FriendAddRequest
import love.forte.simbot.core.api.message.events.GroupAddRequest
import love.forte.simbot.core.api.sender.Setter
import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.checkBotPermission

/**
 *
 * mirai [Setter] 实现。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
public class MiraiSetter(private val bot: Bot) : Setter {
    /**
     * 通过好友申请。
     */
    override fun setFriendAddRequest(
        flag: Flag<FriendAddRequest.FlagContent>,
        friendRemark: String?,
        agree: Boolean
    ): Carrier<Boolean> {
        TODO("Not yet implemented")
    }

    /**
     * 通过群申请。
     */
    override fun setGroupAddRequest(flag: Flag<GroupAddRequest.FlagContent>, agree: Boolean): Carrier<Boolean> {
        TODO("Not yet implemented")
    }

    /**
     * 设置群管理。
     */
    @Deprecated("Api not supported by mirai: changeGroupAdmin")
    private fun changeGroupAdmin0(groupCode: Long, memberCode: Long, promotion: Boolean): Carrier<Boolean> {
        throw IllegalStateException("Api not supported by mirai: changeGroupAdmin")
    }
    @Deprecated("Api not supported by mirai: changeGroupAdmin")
    override fun changeGroupAdmin(groupCode: String, memberCode: String, promotion: Boolean): Carrier<Boolean> =
        changeGroupAdmin0(groupCode.toLong(), memberCode.toLong(), promotion)
    @Deprecated("Api not supported by mirai: changeGroupAdmin")
    override fun changeGroupAdmin(groupCode: Long, memberCode: Long, promotion: Boolean): Carrier<Boolean> =
        changeGroupAdmin0(groupCode, memberCode, promotion)

    override fun setGroupAnonymous(group: String, agree: Boolean): Carrier<Boolean> {
        TODO("Not yet implemented")
    }

    override fun setGroupBan(groupCode: String, memberCode: String, time: Long): Carrier<Boolean> {
        TODO("Not yet implemented")
    }

    override fun setGroupWholeBan(groupCode: String, ban: Boolean): Carrier<Boolean> {
        TODO("Not yet implemented")
    }

    override fun setGroupRemark(groupCode: String, memberCode: String, remark: String?): Carrier<Boolean> {
        TODO("Not yet implemented")
    }

    override fun setGroupLeave(groupCode: String, forcibly: Boolean): Carrier<Boolean> {
        TODO("Not yet implemented")
    }

    override fun setGroupMemberKick(groupCode: String, memberCode: String, blackList: Boolean): Carrier<Boolean> {
        TODO("Not yet implemented")
    }

    override fun setGroupMemberSpecialTitle(groupCode: String, memberCode: String, title: String?): Carrier<Boolean> {
        TODO("Not yet implemented")
    }

    override fun setMsgRecall(flag: Flag<MessageEventGet.MessageFlagContent>): Carrier<Boolean> {
        TODO("Not yet implemented")
    }

    override fun setGroupName(groupCode: String, name: String): Carrier<Boolean> {
        TODO("Not yet implemented")
    }
}