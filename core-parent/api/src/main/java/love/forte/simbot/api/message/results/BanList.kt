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

@file:JvmMultifileClass
@file:JvmName("Results")
package love.forte.simbot.api.message.results

import love.forte.simbot.api.message.assists.Permissions
import love.forte.simbot.api.message.containers.AccountContainer
import love.forte.simbot.api.message.containers.GroupAccountInfo
import love.forte.simbot.api.message.containers.PermissionContainer
import love.forte.simbot.api.message.containers.emptyGroupAccountInfo


/**
 *
 * 被禁言人列表。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
public interface BanList : MultipleResults<BanInfo>





/**
 * 被禁言者的信息。其中包括 [账号信息][AccountContainer]、[权限信息][PermissionContainer]
 */
public interface BanInfo : Result, PermissionContainer, GroupAccountInfo {

    /**
     * 剩余禁言时间。如果不支持则有可能为 -1。
     * 而没有被禁言一般代表为 `lastTime == 0`。
     */
    val lastTime: Long
}



/**
 * [BanInfo] 空值实现。
 */
public fun emptyBanInfo(): BanInfo = EmptyBanInfo


private object EmptyBanInfo : BanInfo, GroupAccountInfo by emptyGroupAccountInfo() {
    override val originalData: String
        get() = "{}"
    override val permission: Permissions
        get() = Permissions.MEMBER
    override val lastTime: Long
        get() = -1

    override fun toString(): String {
        return "EmptyBanInfo"
    }
}


/**
 * [BanList] 的空值实现。
 */
public fun emptyBanList(): BanList = EmptyBanList


private object EmptyBanList : BanList {
    override val originalData: String
        get() = "[]"
    override val results: List<BanInfo>
        get() = emptyList()

    override fun toString(): String {
        return "EmptyBanList"
    }
}
