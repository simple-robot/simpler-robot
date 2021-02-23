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
 * 简单的群信息，只包含了[群基础信息][GroupInfo]
 */
public interface SimpleGroupInfo : Result, GroupInfo


/**
 *
 * 获取到的群详细信息。除了 [群简单信息][SimpleGroupInfo]以外，还有一些其他的信息。
 *
 * 其中，如果返回值为数字类型的，获取不到均会返回一个默认值，例如 `-1`。
 *
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
public interface GroupFullInfo : SimpleGroupInfo {

    /**
     * 群人数上限。
     * 有可能会获取不到，获取不到的时候返回一个-1。
     */
    val maximum: Int

    /**
     * 当前群人数。
     */
    val total: Int

    /**
     * 建群时间。
     * 有可能会获取不到，获取不到的时候返回一个-1。
     */
    val createTime: Long

    /**
     * 简略介绍
     */
    val simpleIntroduction: String?

    /**
     * 完整介绍
     */
    val fullIntroduction: String?


    /**
     * 群主信息
     */
    val owner: GroupOwner


    /**
     * 群管理员列表，不包含上述的群主。
     */
    val admins: List<GroupAdmin>
}

public val GroupFullInfo.adminsAndOwner: List<GroupAdmin>
    get() {
        return admins.takeIf { it.isNotEmpty() }
            ?.let {
                it + owner
            } ?: listOf(owner)
    }


/**
 * 群列表，得到一些群的基础信息
 */
public interface GroupList : MultipleResults<SimpleGroupInfo>


/**
 * 管理员信息
 * @see GroupAdminImpl
 */
public interface GroupAdmin : GroupAccountInfo, PermissionContainer {


    @JvmDefault
    override val permission: Permissions
        get() = Permissions.ADMINISTRATOR
}


/**
 * 通过一个 [AccountContainer] 来构建 [GroupAdmin] 实例
 */
public data class GroupAdminImpl(private val account: GroupAccountInfo) : GroupAdmin, GroupAccountInfo by account


/**
 * 群主信息。群主也是一个管理员。
 * @see GroupOwnerImpl
 */
public interface GroupOwner : GroupAdmin, GroupAccountInfo, PermissionContainer {


    @JvmDefault
    override val permission: Permissions
        get() = Permissions.OWNER
}


/**
 * 通过一个 [AccountContainer] 来构建 [GroupOwner] 实例
 */
public data class GroupOwnerImpl(private val account: GroupAccountInfo) : GroupOwner, GroupAccountInfo by account


private object EmptyGroupOwner : GroupOwner, GroupAccountInfo by emptyGroupAccountInfo() {
    override fun toString(): String {
        return "EmptyGroupOwner"
    }
}


/**
 * [GroupInfo] 的空值实现。所有属性均为空或无效默认值。
 */
public fun emptyGroupInfo(): GroupFullInfo = object : GroupFullInfo {
    override val originalData: String
        get() = "{}"
    override val groupCode: String
        get() = ""
    override val groupAvatar: String?
        get() = null
    override val groupName: String?
        get() = null
    override val maximum: Int
        get() = -1
    override val total: Int
        get() = -1
    override val createTime: Long
        get() = -1
    override val simpleIntroduction: String?
        get() = null
    override val fullIntroduction: String?
        get() = null
    override val owner: GroupOwner
        get() = EmptyGroupOwner
    override val admins: List<GroupAdmin>
        get() = emptyList()

    override fun toString(): String {
        return "EmptyGroupInfo"
    }
}


/**
 * [GroupList] 无效化实现。
 */
public fun emptyGroupList(): GroupList = object : GroupList {
    override val originalData: String
        get() = "[]"
    override val results: List<SimpleGroupInfo>
        get() = emptyList()

    override fun toString(): String {
        return "EmptyGroupList"
    }
}