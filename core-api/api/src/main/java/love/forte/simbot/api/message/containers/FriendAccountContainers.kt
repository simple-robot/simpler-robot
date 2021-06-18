/*
 *
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  component-onebot
 * File     AccountContainerBranches.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 *
 *
 *
 */
@file:JvmName("Containers")
@file:JvmMultifileClass

package love.forte.simbot.api.message.containers

import love.forte.simbot.annotation.ContainerType


/**
 * 好友用户名称信息容器
 */
@ContainerType("好友用户名称信息容器")
public interface FriendAccountNameContainer : AccountNameContainer

/**
 * 好友用户头衔信息容器
 */
@ContainerType("好友用户头衔信息容器")
public interface FriendAccountAvatarContainer : AccountAvatarContainer

/**
 * 好友用户账号信息容器
 */
@ContainerType("好友用户账号信息容器")
public interface FriendAccountCodeContainer : AccountCodeContainer


/**
 * 一个好友用户账号信息容器, 继承了
 * - [好友用户名称容器][FriendAccountNameContainer],
 * - [好友用户头像容器][FriendAccountAvatarContainer],
 * - [好友用户账号容器][FriendAccountCodeContainer]
 */
@ContainerType("好友用户信息容器")
public interface FriendAccountInfo : Container, AccountInfo,
    FriendAccountNameContainer, FriendAccountAvatarContainer,
    FriendAccountCodeContainer


/**
 * 好友账号信息。
 */
@ContainerType("好友账号信息")
public interface FriendAccountContainer : Container, AccountContainer {
    /**
     * 获取一个好友账号信息。
     */
    override val accountInfo: FriendAccountInfo
}


/**
 * 获取一个 [FriendAccountContainer] 数据实例。
 */
@JvmName("getFriendAccountContainer")
@Suppress("FunctionName")
public fun FriendAccountContainer(accountInfo: FriendAccountInfo): FriendAccountContainer =
    FriendAccountContainerData(accountInfo)

/**
 * 获取一个 [FriendAccountContainer] 数据实例。
 * java直接使用上面那个。
 */
@Suppress("FunctionName")
public inline fun FriendAccountContainer(accountInfo: () -> FriendAccountInfo): FriendAccountContainer =
    FriendAccountContainer(accountInfo())


/** [FriendAccountContainer] 数据类实现。 */
private data class FriendAccountContainerData(override val accountInfo: FriendAccountInfo) : FriendAccountContainer




