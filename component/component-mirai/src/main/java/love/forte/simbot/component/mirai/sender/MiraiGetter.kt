/*
 *
 *  * Copyright (c) 2021. ForteScarlet All rights reserved.
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

package love.forte.simbot.component.mirai.sender

import love.forte.simbot.api.SimbotExperimentalApi
import love.forte.simbot.api.message.containers.AccountCodeContainer
import love.forte.simbot.api.message.containers.BotContainer
import love.forte.simbot.api.message.containers.BotInfo
import love.forte.simbot.api.message.containers.GroupCodeContainer
import love.forte.simbot.api.message.events.MsgGet
import love.forte.simbot.api.message.results.*
import love.forte.simbot.api.sender.AdditionalApi
import love.forte.simbot.api.sender.Getter
import love.forte.simbot.api.sender.GetterFactory
import love.forte.simbot.component.mirai.MiraiBotAccountInfo
import love.forte.simbot.component.mirai.additional.GetterInfo
import love.forte.simbot.component.mirai.additional.MiraiGetterAdditionalApi
import love.forte.simbot.component.mirai.message.result.*
import love.forte.simbot.http.template.HttpTemplate
import net.mamoe.mirai.Bot


public class MiraiGetterFactory(private val http: HttpTemplate) : GetterFactory {
    override fun getOnMsgGetter(msg: MsgGet, def: Getter.Def): Getter =
        MiraiGetter(Bot.getInstance(msg.botInfo.botCodeNumber), http, defGetter = def)

    override fun getOnBotGetter(bot: BotContainer, def: Getter.Def): Getter =
        MiraiGetter(Bot.getInstance(bot.botInfo.botCodeNumber), http, defGetter = def)
}

public class MiraiGetter(
    private val bot: Bot,
    private val http: HttpTemplate,
    private val defGetter: Getter,
) : Getter {

    private lateinit var _getterInfo: GetterInfo
    private val getterInfo: GetterInfo
    get() {
        if (!::_getterInfo.isInitialized) {
            _getterInfo = GetterInfo(bot, http)
            // synchronized(this) {
            //     if (!::_getterInfo.isInitialized) {
            //         _getterInfo = GetterInfo(bot, http)
            //     }
            // }
        }
        return _getterInfo
    }

    @OptIn(SimbotExperimentalApi::class)
    override val authInfo: AuthInfo
        get() = MiraiAuthInfo(UnsafeViolenceAndroidBotCookieUtils.cookies(bot))

    override val botInfo: BotInfo get() = MiraiBotAccountInfo.getInstance(bot)


    /**
     * mirai-获取好友信息。
     */
    private fun getFriendInfo0(code: Long): FriendInfo {
        return bot.friendOrNull(code)?.let { f -> MiraiFriendInfo(f) }
            ?: bot.getStranger(code)?.let { s -> MiraiStrangerInfo(s) }
            ?: throw NoSuchElementException("No such friend or stranger $code from bot ${bot.id}")
    }

    override suspend fun friendInfo(code: String): FriendInfo = getFriendInfo0(code.toLong())
    override suspend fun friendInfo(code: Long): FriendInfo = getFriendInfo0(code)
    override suspend fun friendInfo(code: AccountCodeContainer): FriendInfo = friendInfo(code.accountCodeNumber)


    /**
     * mirai - 群友信息。
     */
    private fun getMemberInfo0(group: Long, code: Long): GroupMemberInfo {
        return MiraiGroupMemberInfo(bot.member(group, code))
    }

    override suspend fun memberInfo(group: String, code: String): GroupMemberInfo =
        getMemberInfo0(group.toLong(), code.toLong())

    override suspend fun memberInfo(group: Long, code: Long): GroupMemberInfo = getMemberInfo0(group, code)
    override suspend fun memberInfo(group: GroupCodeContainer, code: AccountCodeContainer): GroupMemberInfo =
        memberInfo(group.groupCodeNumber, code.accountCodeNumber)

    /**
     * mirai - 群信息
     */
    private fun getGroupInfo0(group: Long): GroupFullInfo = MiraiGroupFullInfo(bot.group(group))
    override suspend fun groupInfo(group: String): GroupFullInfo = getGroupInfo0(group.toLong())
    override suspend fun groupInfo(group: Long): GroupFullInfo = getGroupInfo0(group)
    override suspend fun groupInfo(group: GroupCodeContainer): GroupFullInfo = groupInfo(group.groupCodeNumber)


    override suspend fun friendList(cache: Boolean, limit: Int): FriendList = MiraiFriendList(bot, limit)


    override suspend fun groupList(cache: Boolean, limit: Int): GroupList = MiraiGroupList(bot, limit)


    /**
     * mirai - group member list.
     */
    private fun getGroupMemberList0(group: Long, limit: Int): GroupMemberList =
        MiraiGroupMemberList(bot.group(group), limit)

    override suspend fun groupMemberList(group: String, cache: Boolean, limit: Int): GroupMemberList =
        getGroupMemberList0(group.toLong(), limit)

    override suspend fun groupMemberList(group: Long, cache: Boolean, limit: Int): GroupMemberList =
        getGroupMemberList0(group, limit)

    override suspend fun groupMemberList(group: GroupCodeContainer, cache: Boolean, limit: Int): GroupMemberList =
        groupMemberList(group.groupCodeNumber, cache, limit)


    /**
     * mirai - ban list.
     */
    private fun getBanList0(group: Long, limit: Int): MuteList = MiraiMuteList(bot.group(group), limit)
    override suspend fun banList(group: String, cache: Boolean, limit: Int): MuteList = getBanList0(group.toLong(), limit)
    override suspend fun banList(group: Long, cache: Boolean, limit: Int): MuteList = getBanList0(group, limit)
    override suspend fun banList(group: GroupCodeContainer, cache: Boolean, limit: Int): MuteList =
        banList(group.groupCodeNumber, cache, limit)


    /**
     * mirai - group note list.
     */
    private suspend fun getGroupNoteList0(group: Long, limit: Int = -1): GroupNoteList =
        MiraiGroupNoteList(bot.group(group), limit)

    override suspend fun groupNoteList(group: String, cache: Boolean, limit: Int): GroupNoteList =
        getGroupNoteList0(group.toLong(), limit)

    override suspend fun groupNoteList(group: Long, cache: Boolean, limit: Int): GroupNoteList =
        getGroupNoteList0(group, limit)

    override suspend fun groupNoteList(group: GroupCodeContainer, cache: Boolean, limit: Int): GroupNoteList =
        groupNoteList(group.groupCodeNumber, cache, limit)


    /**
     * 额外API
     */
    override suspend fun <R : Result> execute(additionalApi: AdditionalApi<R>): R {
        if (additionalApi is MiraiGetterAdditionalApi) {
            return additionalApi.execute(getterInfo)
        }

        return super.additionalExecute(additionalApi)
    }


}

