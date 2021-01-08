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

@file:JvmName("FailedGetterFactories")
package love.forte.simbot.api.sender

import love.forte.simbot.api.message.containers.BotContainer
import love.forte.simbot.api.message.containers.BotInfo
import love.forte.simbot.api.message.containers.emptyBotInfo
import love.forte.simbot.api.message.events.MsgGet
import love.forte.simbot.api.message.results.*

/**
 * [Getter] 的无效失败实例，总是返回默认的空内容值实例。
 * 在返回的实体类中：
 * - 如果返回值可能为null的，返回`null`;
 * - 如果返回值为布尔类型的，返回`false`;
 * - 如果返回值为数值类型的，返回 `-1`;
 * - 如果返回值为字符串类型的，返回空字符串（`""`）;
 * - 如果返回值为列表/数组类型的，返回元素为空的实例;
 */
public object FailedGetter : Getter.Def {
    override val authInfo: AuthInfo
        get() = emptyAuthInfo()
    override val botInfo: BotInfo
        get() = emptyBotInfo()
    override fun getFriendInfo(code: String): FriendInfo = emptyFriendInfo()
    override fun getMemberInfo(group: String, code: String): GroupMemberInfo = emptyGroupMemberInfo()
    override fun getGroupInfo(group: String): GroupFullInfo = emptyGroupInfo()
    override fun getFriendList(cache: Boolean, limit: Int): FriendList = emptyFriendList()
    override fun getGroupList(cache: Boolean, limit: Int): GroupList = emptyGroupList()
    override fun getGroupMemberList(group: String, cache: Boolean, limit: Int): GroupMemberList = emptyGroupMemberList()
    override fun getBanList(group: String, cache: Boolean, limit: Int): BanList = emptyBanList()
    override fun getGroupNoteList(group: String, cache: Boolean, limit: Int): GroupNoteList = emptyGroupNoteList()
}




/**
 * 获取一个总是返回失败默认值的实现类。
 */
@get:JvmName("getFailedGetterFactory")
public val FailedGetterFactory : DefaultGetterFactory = object: DefaultGetterFactory {
    override fun getOnMsgGetter(msg: MsgGet): Getter.Def = FailedGetter
    override fun getOnBotGetter(bot: BotContainer): Getter.Def = FailedGetter
}
