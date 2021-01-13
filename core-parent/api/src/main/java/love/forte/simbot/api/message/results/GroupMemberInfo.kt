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
import love.forte.simbot.api.message.containers.*


/**
 *
 * 群成员的信息。其中包含了[群信息][GroupContainer] 与 这个人的 [账号信息][AccountContainer] 与 [权限信息][PermissionContainer]
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
public interface GroupMemberInfo : Result, GroupAccountInfo, GroupContainer, PermissionContainer,
    GroupAccountContainer {
    /**
     * TODO 2.0.0-BETA.10删除。
     */
    @JvmDefault
    @Deprecated("Use self.", ReplaceWith("this"), DeprecationLevel.ERROR)
    override val accountInfo: GroupAccountInfo
        get() = this
}


/**
 * 群成员列表。
 */
public interface GroupMemberList : MultipleResults<GroupMemberInfo>, GroupContainer


/**
 * [GroupMemberInfo] 的无效化实现。
 */
public fun emptyGroupMemberInfo(): GroupMemberInfo =
    object : GroupMemberInfo, GroupAccountInfo by emptyGroupAccountInfo() {
        override val originalData: String
            get() = "{}"
        override val permission: Permissions
            get() = Permissions.MEMBER
        override val groupInfo: GroupInfo
            get() = emptyGroupInfo()

        override fun toString(): String {
            return "EmptyGroupMemberInfo"
        }
    }


/**
 * [GroupMemberList] 的无效化实现。
 */
public fun emptyGroupMemberList(): GroupMemberList = object : GroupMemberList {
    override val originalData: String
        get() = "[]"
    override val groupInfo: GroupInfo
        get() = emptyGroupInfo()
    override val results: List<GroupMemberInfo>
        get() = emptyList()

    override fun toString(): String {
        return "EmptyGroupMemberList"
    }
}
