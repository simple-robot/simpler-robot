/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot-S
 *  * File     GroupContainers.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *  *
 *  *
 *
 */

package love.forte.simbot.common.api.messages.containers

import love.forte.simbot.common.annotations.ContainerType
import love.forte.simbot.common.api.messages.results.Result


/**
 * 群号容器。定义可以得到一个群的群号
 */
@ContainerType("群号容器")
public interface GroupCodeContainer {
    /** 群号 */
    val groupCode: String

    /** 群号的[Long]类型。如果可以作为数字的话。 */
    @JvmDefault
    val groupCodeNumber: Long
        get() = groupCode.toLong()
}

/**
 * 群头像容器。定义可以得到群头像
 * 头像不是必须的，可能会不存在
 */
@ContainerType("群头像容器")
public interface GroupAvatarContainer {
    /** 群头像. 可能为null。但是一般来讲为`null`的可能性比较小 */
    val groupAvatar: String?
}

/**
 * 群名称容器，定义可以得到群的群名称
 */
public interface GroupNameContainer {
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
public interface GroupInfo : GroupAvatarContainer, GroupCodeContainer, GroupNameContainer

/**
 * 可以得到一个[群信息][GroupInfo]容器
 */
@ContainerType("群容器")
public interface GroupContainer {
    val groupInfo: GroupInfo
}
