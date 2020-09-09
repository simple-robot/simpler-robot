/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot-S
 *  * File     BotContainers.kt
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


/**
 * 机器人自身的账号容器。
 */
@ContainerType("机器人账号容器")
public interface BotCodeContainer {
    /** 当前的bot的账号 */
    val botCode: String

    /** 得到[botCode]的[Long]类型。如果可以作为数字的话。 */
    val botCodeNumber: Long get() = botCode.toLong()
}

/**
 * 机器人自身的名称容器。
 */
@ContainerType("机器人名称容器")
public interface BotNameContainer {
    /**
     * 机器人的名称
     */
    val botName: String
}

/**
 * 机器人自身的头像容器。
 * 头像不是必须的，可能不存在。
 */
@ContainerType("机器人头像容器")
public interface BotAvatarContainer {
    /** 机器人的头像 */
    val botAvatar: String?
}

/**
 * 机器人基础信息容器, 其实现了：
 * - [机器人账号容器][BotCodeContainer],
 * - [机器人名称容器][BotNameContainer],
 * - [机器人头像容器][BotAvatarContainer]
 */
@ContainerType("机器人信息容器")
public interface BotInfo : BotCodeContainer, BotNameContainer, BotAvatarContainer

/**
 * bot容器，可以得到一个 [bot信息][BotInfo]。
 *
 * @property botInfo BotInfoContainer
 */
@ContainerType("bot容器")
public interface BotContainer {
    /**
     * bot信息
     */
    val botInfo: BotInfo
}
