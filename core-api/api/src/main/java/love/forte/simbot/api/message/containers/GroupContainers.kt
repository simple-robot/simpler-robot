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

@file:JvmName("Containers")
@file:JvmMultifileClass

package love.forte.simbot.api.message.containers

import love.forte.simbot.annotation.ContainerType


/**
 * 群号容器。定义可以得到一个群的群号
 */
@ContainerType("群号容器")
public interface GroupCodeContainer : Container {
    /** 群号 */
    val groupCode: String

    /** 群号的[Long]类型。如果可以作为数字的话。 */
    val groupCodeNumber: Long
        get() = groupCode.toLong()
}

/**
 * 群头像容器。定义可以得到群头像
 * 头像不是必须的，可能会不存在
 */
@ContainerType("群头像容器")
public interface GroupAvatarContainer : Container {
    /** 群头像. 可能为null。但是一般来讲为`null`的可能性比较小 */
    val groupAvatar: String?
}

/**
 * 群名称容器，定义可以得到群的群名称
 */
public interface GroupNameContainer : Container {
    /**
     * 群名称 可能出现无法获取的情况
     */
    val groupName: String?
}

/**
 * 群信息容器，实现
 * [群头像容器][GroupAvatarContainer],
 * [群账号容器][GroupCodeContainer],
 * [群名称容器][GroupNameContainer]
 */
@ContainerType("群信息容器")
public interface GroupInfo : Container, GroupAvatarContainer, GroupCodeContainer, GroupNameContainer


@JvmOverloads
public fun simpleGroupInfo(
    groupCode: String,
    groupName: String? = null,
    groupAvatar: String? = null,
): GroupInfo = SimpleGroupInfo(groupCode, groupName, groupAvatar)


private data class SimpleGroupInfo(
    override val groupCode: String,
    override val groupName: String?,
    override val groupAvatar: String?,
) : GroupInfo


/**
 * 可以得到一个[群信息][GroupInfo]容器
 */
@ContainerType("群容器")
public interface GroupContainer : Container {
    val groupInfo: GroupInfo
}


/**
 * 获取一个 [GroupContainer] 数据实例。
 */
@JvmName("getGroupContainer")
@Suppress("FunctionName")
public fun GroupContainer(groupInfo: GroupInfo): GroupContainer = GroupContainerData(groupInfo)

/**
 * 获取一个 [GroupContainer] 数据实例。
 * java用上面那个。
 */
@JvmName("__getGroupContainer")
@Suppress("FunctionName")
public inline fun GroupContainer(groupInfo: () -> GroupInfo): GroupContainer = GroupContainer(groupInfo())


/** [GroupContainer] 数据类实现。 */
private data class GroupContainerData(override val groupInfo: GroupInfo) : GroupContainer
