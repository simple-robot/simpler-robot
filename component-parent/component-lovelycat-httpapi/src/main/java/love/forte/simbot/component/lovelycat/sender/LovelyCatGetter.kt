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

import javafx.concurrent.ScheduledService
import love.forte.simbot.api.message.containers.BotInfo
import love.forte.simbot.api.message.results.*
import love.forte.simbot.api.sender.Getter
import love.forte.simbot.component.lovelycat.LovelyCatApiTemplate
import love.forte.simbot.component.lovelycat.message.event.lovelyCatBotInfo


/**
 * 可爱猫取信器。
 */
public class LovelyCatGetter(
    private val botId: String,
    private val api: LovelyCatApiTemplate
) : Getter {
    /**
     * 得到当前bot的权限信息。
     */
    override val authInfo: AuthInfo
        get() = EmptyAuthInfo

    /**
     * 获取当前bot的基础信息。
     */
    override val botInfo: BotInfo
        get() = lovelyCatBotInfo(botId, api)


    /**
     * 获取一个好友的信息。
     */
    override fun getFriendInfo(code: String): FriendInfo {
        val friendList = api.getFriendList(botId, true)
        return friendList.find { it.wxid == code } ?: throw NoSuchElementException("friend: $code")
    }

    /**
     * 获取一个群友信息。
     */
    override fun getMemberInfo(group: String, code: String): GroupMemberInfo {
        // val groupMemberInfo = api.getGroupMemberDetailInfo(botId, group, code, true)
        // val groupInfo = api.getGroupList(botId, true).find { it.groupCode == group }
        //     ?: throw NoSuchElementException("group: $group")

        TODO("Not yet implemented")
    }

    /**
     * 获取一个群详细信息
     */
    override fun getGroupInfo(group: String): GroupFullInfo {
        TODO("Not yet implemented")
    }

    /**
     * 获取好友列表
     * @param cache 是否使用缓存。
     */
    override fun getFriendList(cache: Boolean, limit: Int): FriendList {
        TODO("Not yet implemented")
    }

    /**
     * 获取群列表
     * @param cache 是否使用缓存。
     */
    override fun getGroupList(cache: Boolean, limit: Int): GroupList {
        TODO("Not yet implemented")
    }

    /**
     * 获取群成员列表
     */
    override fun getGroupMemberList(group: String, cache: Boolean, limit: Int): GroupMemberList {
        TODO("Not yet implemented")
    }

    /**
     * 获取某群的被禁言人列表。
     * @param group 群号
     * @param cache 是否使用缓存
     */
    override fun getBanList(group: String, cache: Boolean, limit: Int): BanList {
        TODO("Not yet implemented")
    }

    /**
     * 获取群公告列表
     */
    override fun getGroupNoteList(group: String, cache: Boolean, limit: Int): GroupNoteList {
        TODO("Not yet implemented")
    }
}