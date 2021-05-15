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

package love.forte.simbot.api.sender

import love.forte.simbot.LogAble
import love.forte.simbot.api.message.containers.BotInfo
import love.forte.simbot.api.message.containers.emptyBotInfo
import love.forte.simbot.api.message.results.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory


/**
 * 一个通过Logger输出警告信息的 [Getter.Def] 送信器。
 * @author ForteScarlet
 * @since 2.0.0-BETA.9
 */
@Suppress("ComplexRedundantLet")
public class WarnGetter(
    override val log: Logger = LoggerFactory.getLogger(WarnGetter::class.java),
) : LogAble, Getter.Def {

    private inline fun apiWarn(name: String, def: () -> Any?) {
        log.warn("Getter api {} is not supported. Will return to the default value {}", name, def())
    }

    override val authInfo: AuthInfo
        get() = apiWarn("authInfo") { "EmptyAuthInfo" }.let { emptyAuthInfo() }

    override val botInfo: BotInfo
        get() = apiWarn("botInfo") { "EmptyBotInfo" }.let { emptyBotInfo() }

    override fun getFriendInfo(code: String): FriendInfo =
        apiWarn("getFriendInfo") { "EmptyFriendInfo" }.let { emptyFriendInfo() }

    override fun getMemberInfo(group: String, code: String): GroupMemberInfo =
        apiWarn("getMemberInfo") { "EmptyMemberInfo" }.let { emptyGroupMemberInfo() }

    override fun getGroupInfo(group: String): GroupFullInfo =
        apiWarn("getGroupInfo") { "EmptyGroupInfo" }.let { emptyGroupInfo() }

    override fun getFriendList(cache: Boolean, limit: Int): FriendList =
        apiWarn("getFriendList") { "EmptyFriendList" }.let { emptyFriendList() }

    override fun getGroupList(cache: Boolean, limit: Int): GroupList =
        apiWarn("getGroupList") { "EmptyGroupList" }.let { emptyGroupList() }

    override fun getGroupMemberList(group: String, cache: Boolean, limit: Int): GroupMemberList =
        apiWarn("getGroupMemberList") { "EmptyGroupMemberList" }.let { emptyGroupMemberList() }

    override fun getBanList(group: String, cache: Boolean, limit: Int): MuteList =
        apiWarn("getBanList") { "EmptyBanList" }.let { emptyMuteList() }

    override fun getGroupNoteList(group: String, cache: Boolean, limit: Int): GroupNoteList =
        apiWarn("getGroupNoteList") { "EmptyGroupNoteList" }.let { emptyGroupNoteList() }
}