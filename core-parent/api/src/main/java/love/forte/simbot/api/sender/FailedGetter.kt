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

import love.forte.simbot.api.message.containers.BotInfo
import love.forte.simbot.api.message.containers.emptyBotInfo
import love.forte.simbot.api.message.results.*

/**
 *
 */
public object FailedGetter : Getter {
    override val authInfo: AuthInfo
        get() = emptyAuthInfo()
    override val botInfo: BotInfo
        get() = emptyBotInfo()

    override fun getFriendInfo(code: String): FriendInfo {
        TODO("Not yet implemented")
    }

    override fun getMemberInfo(group: String, code: String): GroupMemberInfo {
        TODO("Not yet implemented")
    }

    override fun getGroupInfo(group: String): GroupFullInfo {
        TODO("Not yet implemented")
    }

    override fun getFriendList(cache: Boolean, limit: Int): FriendList {
        TODO("Not yet implemented")
    }

    override fun getGroupList(cache: Boolean, limit: Int): GroupList {
        TODO("Not yet implemented")
    }

    override fun getGroupMemberList(group: String, cache: Boolean, limit: Int): GroupMemberList {
        TODO("Not yet implemented")
    }

    override fun getBanList(group: String, cache: Boolean, limit: Int): BanList {
        TODO("Not yet implemented")
    }

    override fun getGroupNoteList(group: String, cache: Boolean, limit: Int): GroupNoteList {
        TODO("Not yet implemented")
    }
}