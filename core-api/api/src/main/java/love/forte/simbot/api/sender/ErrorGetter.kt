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

@file:JvmName("ErrorGetterFactories")
package love.forte.simbot.api.sender

import love.forte.simbot.api.message.containers.BotContainer
import love.forte.simbot.api.message.events.MsgGet
import love.forte.simbot.api.message.results.Result


/**
 * [Getter] 的 无效化实现，所有的方法均会抛出异常。
 *
 * @see ErrorGetterFactory
 *
 */
public object ErrorGetter : Getter.Def {
    override val authInfo: Nothing
        get() = NO("Getter.authInfo")
    override val botInfo: Nothing
        get() = NO("Getter.botInfo")

    override suspend fun friendInfo(code: String): Nothing =
        NO("Getter.getFriendInfo")

    override suspend fun memberInfo(group: String, code: String): Nothing =
        NO("Getter.getMemberInfo")

    override suspend fun groupInfo(group: String): Nothing =
        NO("Getter.getGroupInfo")

    override suspend fun friendList(cache: Boolean, limit: Int): Nothing =
        NO("Getter.getFriendList")

    override suspend fun groupList(cache: Boolean, limit: Int): Nothing =
        NO("Getter.getGroupList")

    override suspend fun groupMemberList(group: String, cache: Boolean, limit: Int): Nothing =
        NO("Getter.getGroupMemberList")

    override suspend fun banList(group: String, cache: Boolean, limit: Int): Nothing =
        NO("Getter.getBanList")

    override suspend fun groupNoteList(group: String, cache: Boolean, limit: Int): Nothing =
        NO("Getter.getGroupNoteList")

    override suspend fun <R : Result> execute(additionalApi: AdditionalApi<R>): Nothing = NO("Getter.additionalApi.${additionalApi.additionalApiName}")
}


/**
 * [ErrorGetter] 的构建工厂，得到的 [Getter] 实例的所有方法均会抛出异常。
 */
@get:JvmName("getErrorGetterFactory")
public val ErrorGetterFactory : DefaultGetterFactory = object : DefaultGetterFactory {
    override fun getOnMsgGetter(msg: MsgGet): Getter.Def = ErrorGetter
    override fun getOnBotGetter(bot: BotContainer): Getter.Def = ErrorGetter
}
