/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     MiraiGetter.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.component.mirai.sender

import love.forte.simbot.api.message.containers.AccountCodeContainer
import love.forte.simbot.api.message.containers.BotContainer
import love.forte.simbot.api.message.containers.BotInfo
import love.forte.simbot.api.message.containers.GroupCodeContainer
import love.forte.simbot.api.message.events.MsgGet
import love.forte.simbot.api.message.results.*
import love.forte.simbot.api.sender.Getter
import love.forte.simbot.api.sender.GetterFactory
import love.forte.simbot.component.mirai.message.result.*
import net.mamoe.mirai.Bot


public object MiraiGetterFactory : GetterFactory {
    override fun getOnMsgGetter(msg: MsgGet): Getter = MiraiGetter(Bot.getInstance(msg.botInfo.botCodeNumber))
    override fun getOnBotGetter(bot: BotContainer): Getter = MiraiGetter(Bot.getInstance(bot.botInfo.botCodeNumber))
}

public class MiraiGetter(private val bot: Bot) : Getter {
    override val authInfo: AuthInfo
        get() = MiraiAuthInfo(AndroidBotCookieUtils.cookies(bot))

    override val botInfo: BotInfo
        get() = MiraiBotInfo(bot)


    /**
     * mirai-获取好友信息。
     */
    private fun getFriendInfo0(code: Long): FriendInfo = MiraiFriendInfo(bot.getFriend(code))
    override fun getFriendInfo(code: String): FriendInfo = getFriendInfo0(code.toLong())
    override fun getFriendInfo(code: Long): FriendInfo = getFriendInfo0(code)
    override fun getFriendInfo(code: AccountCodeContainer): FriendInfo = getFriendInfo(code.accountCodeNumber)



    /**
     * mirai - 群友信息。
     */
    private fun getMemberInfo0(group: Long, code: Long): GroupMemberInfo {
        return MiraiGroupMemberInfo(bot.getGroup(group)[code])
    }
    override fun getMemberInfo(group: String, code: String): GroupMemberInfo = getMemberInfo0(group.toLong(), code.toLong())
    override fun getMemberInfo(group: Long, code: Long): GroupMemberInfo = getMemberInfo0(group, code)
    override fun getMemberInfo(group: GroupCodeContainer, code: AccountCodeContainer): GroupMemberInfo =
        getMemberInfo(group.groupCodeNumber, code.accountCodeNumber)

    /**
     * mirai - 群信息
     */
    private fun getGroupInfo0(group: Long): GroupFullInfo = MiraiGroupFullInfo(bot.getGroup(group))
    override fun getGroupInfo(group: String): GroupFullInfo = getGroupInfo0(group.toLong())
    override fun getGroupInfo(group: Long): GroupFullInfo = getGroupInfo0(group)
    override fun getGroupInfo(group: GroupCodeContainer): GroupFullInfo = getGroupInfo(group.groupCodeNumber)


    override fun getFriendList(cache: Boolean, limit: Int): FriendList = MiraiFriendList(bot, limit)


    override fun getGroupList(cache: Boolean, limit: Int): GroupList = MiraiGroupList(bot, limit)


    /**
     * mirai - group member list.
     */
    private fun getGroupMemberList0(group: Long, cache: Boolean, limit: Int): GroupMemberList =
        MiraiGroupMemberList(bot.getGroup(group), limit)
    override fun getGroupMemberList(group: String, cache: Boolean, limit: Int): GroupMemberList =
        getGroupMemberList0(group.toLong(), cache, limit)
    override fun getGroupMemberList(group: Long, cache: Boolean, limit: Int): GroupMemberList =
        getGroupMemberList0(group, cache, limit)
    override fun getGroupMemberList(group: GroupCodeContainer, cache: Boolean, limit: Int): GroupMemberList =
        getGroupMemberList(group.groupCodeNumber, cache, limit)


    /**
     * mirai - ban list.
     */
    private fun getBanList0(group: Long, cache: Boolean, limit: Int): BanList = MiraiBanList(bot.getGroup(group), limit)
    override fun getBanList(group: String, cache: Boolean, limit: Int): BanList = getBanList0(group.toLong(), cache, limit)
    override fun getBanList(group: Long, cache: Boolean, limit: Int): BanList = getBanList0(group, cache, limit)
    override fun getBanList(group: GroupCodeContainer, cache: Boolean, limit: Int): BanList =
        getBanList(group.groupCodeNumber, cache, limit)


    /**
     * mirai - group note list.
     * 注：mirai仅支持获取入群公告。(mirai 1.3.2)
     */
    private fun getGroupNoteList0(group: Long, cache: Boolean, limit: Int): GroupNoteList = MiraiGroupNoteList(bot.getGroup(group))
    override fun getGroupNoteList(group: String, cache: Boolean, limit: Int): GroupNoteList =
        getGroupNoteList0(group.toLong(), cache, limit)
    override fun getGroupNoteList(group: Long, cache: Boolean, limit: Int): GroupNoteList =
        getGroupNoteList0(group, cache, limit)

    override fun getGroupNoteList(group: GroupCodeContainer, cache: Boolean, limit: Int): GroupNoteList =
        getGroupNoteList(group.groupCodeNumber, cache, limit)



}

