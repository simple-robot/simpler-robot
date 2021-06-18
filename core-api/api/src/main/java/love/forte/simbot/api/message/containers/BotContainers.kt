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
public interface BotLevelContainer : Container, LevelContainer {
    /** 机器人等级。 */
    val botLevel: Long get() = -1

    override val level: Long
        get() = botLevel

}


/**
 * 机器人基础信息容器, 其实现了：
 * - [机器人账号容器][BotCodeContainer],
 * - [机器人名称容器][BotNameContainer],
 * - [机器人头像容器][BotAvatarContainer],
 * - [机器人等级容器][BotLevelContainer]
 *
 * 同时，[bot信息][BotInfo]也属于一种 [账号信息][AccountInfo] (since `2.0.0-BETA.9`).
 *
 */
@ContainerType("机器人信息容器")
public interface BotInfo : Container,
    BotCodeContainer, BotNameContainer, BotAvatarContainer, BotLevelContainer,
    AccountInfo {

    override val accountCode: String
        get() = botCode
    override val accountNickname: String
        get() = botName

    /** bot对于自己没有昵称。 */
    override val accountRemark: String?
        get() = null
    override val accountAvatar: String?
        get() = botAvatar
}


/**
 * 在群中，一个Bot的信息。其相当于一个 [GroupAccountInfo].
 */
public interface GroupBotInfo : BotInfo, GroupAccountInfo



/**
 * 提供一个内容为空的bot信息实例。
 */
public fun emptyBotInfo(): BotInfo = EmptyBotInfo

/**
 * 信息内容为空的 [BotInfo] 实例。
 */
private object EmptyBotInfo : BotInfo {
    override val botCode: String
        get() = ""
    override val botName: String
        get() = ""
    override val botAvatar: String?
        get() = null

    override fun toString(): String {
        return "EmptyBotInfo(Nothing here)"
    }
}


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
 * 获取一个 [botContainer] 数据实例。
 */
@JvmName("getBotContainer")
@Suppress("FunctionName")
public fun botContainer(botInfo: BotInfo): BotContainer = BotContainerData(botInfo)

/**
 * 获取一个 [botContainer] 数据实例。
 * java直接使用上面那个。
 */
@JvmName("__getBotContainer")
@Suppress("FunctionName")
public inline fun botContainer(botInfo: () -> BotInfo): BotContainer = botContainer(botInfo())


/** [botContainer] 数据类实现。 */
private data class BotContainerData(override val botInfo: BotInfo) : BotContainer

