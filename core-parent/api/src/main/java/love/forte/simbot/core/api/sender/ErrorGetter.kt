/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     ErrorGetter.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

@file:JvmName("ErrorGetterFactories")
package love.forte.simbot.core.api.sender

import love.forte.simbot.core.api.message.MsgGet
import love.forte.simbot.core.api.message.containers.BotContainer
import love.forte.simbot.core.api.message.containers.BotInfo
import love.forte.simbot.core.api.message.results.*


/**
 * [Getter] 的 无效化实现，所有的方法均会抛出异常。
 *
 * @see ErrorGetterFactory
 *
 */
object ErrorGetter : Getter {
    override val authInfo: AuthInfo
        get() = NO("Getter.authInfo")
    override val botInfo: BotInfo
        get() = NO("Getter.botInfo")

    override fun getFriendInfo(code: String): FriendInfo =
        NO("Getter.getFriendInfo")

    override fun getMemberInfo(group: String, code: String): GroupMemberInfo =
        NO("Getter.getMemberInfo")

    override fun getGroupInfo(group: String): GroupFullInfo =
        NO("Getter.getGroupInfo")

    override fun getFriendList(cache: Boolean, limit: Int): FriendList =
        NO("Getter.getFriendList")

    override fun getGroupList(cache: Boolean, limit: Int): GroupList =
        NO("Getter.getGroupList")

    override fun getGroupMemberList(cache: Boolean, limit: Int): GroupMemberList =
        NO("Getter.getGroupMemberList")

    override fun getBanList(group: String, cache: Boolean, limit: Int): BanList =
        NO("Getter.getBanList")

    override fun getGroupNoteList(group: String, cache: Boolean, limit: Int): GroupNoteList =
        NO("Getter.getGroupNoteList")
}


/**
 * [ErrorGetter] 的构建工厂，得到的 [Getter] 实例的所有方法均会抛出异常。
 */
@get:JvmName("getErrorGetterFactory")
public val ErrorGetterFactory : GetterFactory = object : GetterFactory {
    override fun getOnMsgGetter(msg: MsgGet): Getter = ErrorGetter
    override fun getOnBotGetter(bot: BotContainer): Getter = ErrorGetter
}
