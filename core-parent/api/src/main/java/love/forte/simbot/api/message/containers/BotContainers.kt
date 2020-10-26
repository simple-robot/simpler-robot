/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     BotContainers.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */
@file:JvmName("Containers")
@file:JvmMultifileClass
package love.forte.simbot.api.message.containers

import love.forte.simbot.annotation.ContainerType


/**
 * 机器人自身的账号容器。
 */
@ContainerType("机器人账号容器")
public interface BotCodeContainer : Container {
    /** 当前的bot的账号 */
    val botCode: String

    /** 得到[botCode]的[Long]类型。如果可以作为数字的话。 */
    val botCodeNumber: Long get() = botCode.toLong()
}

/**
 * 机器人自身的名称容器。
 */
@ContainerType("机器人名称容器")
public interface BotNameContainer : Container {
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
public interface BotAvatarContainer : Container {
    /** 机器人的头像 */
    val botAvatar: String?
}


/**
 * 机器人等级容器。
 * 等级不是必然存在的，如果无法获取，则可能返回 `-1`。
 */
@ContainerType("机器人等级容器")
public interface BotLevelContainer : Container {
    /** 机器人等级。 */
    val botLevel: Long get() = -1
}


/**
 * 机器人基础信息容器, 其实现了：
 * - [机器人账号容器][BotCodeContainer],
 * - [机器人名称容器][BotNameContainer],
 * - [机器人头像容器][BotAvatarContainer]
 */
@ContainerType("机器人信息容器")
public interface BotInfo : Container, BotCodeContainer, BotNameContainer, BotAvatarContainer, BotLevelContainer

/**
 * bot容器，可以得到一个 [bot信息][BotInfo]。
 *
 * @property botInfo BotInfoContainer
 */
@ContainerType("bot容器")
public interface BotContainer : Container {
    /**
     * bot信息
     */
    val botInfo: BotInfo
}


/**
 * 获取一个 [BotContainer] 数据实例。
 */
@JvmName("getBotContainer")
@Suppress("FunctionName")
public fun BotContainer(botInfo: BotInfo): BotContainer = BotContainerData(botInfo)

/**
 * 获取一个 [BotContainer] 数据实例。
 * java直接使用上面那个。
 */
@JvmName("__getBotContainer")
@Suppress("FunctionName")
public inline fun BotContainer(botInfo: () -> BotInfo): BotContainer = BotContainer(botInfo())


/** [BotContainer] 数据类实现。 */
private data class BotContainerData(override val botInfo: BotInfo) : BotContainer

