/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     AccountContainers.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.common.api.message.containers

import love.forte.simbot.common.annotations.ContainerType


/**
 * 账户号码容器。定义可以得到一个账号的号码。
 *
 */
@ContainerType("账户号码容器")
public interface AccountCodeContainer {
    /**
     * 账号
     */
    val accountCode: String

    /**
     * 账号的数字值(如果能作为数字的话)。
     * 默认实现为 [accountCode].[toLong]
     */
    @JvmDefault
    val accountCodeNumber: Long
        get() = accountCode.toLong()
}

/**
 * 账户昵称容器。定义可以得到一个账号的昵称。
 */
@ContainerType("账户昵称容器")
public interface AccountNicknameContainer {
    /** 昵称。可能会出现为null的情况 */
    val accountNickname: String?
}

/**
 * 账户备注容器。定义可以得到一个账号的备注信息。可能是好友备注或者群名片。
 * 备注可能不存在。
 */
@ContainerType("账户备注容器")
public interface AccountRemarkContainer {
    /** 好友备注或群名片。可能为null */
    val accountRemark: String?
}

/**
 * 账户名称容器，即 [账号昵称容器][AccountNicknameContainer] 与 [账号备注容器][AccountRemarkContainer]
 * 并提供几个默认的整合值:
 * - [备注或昵称][accountRemarkOrNickname]
 * - [昵称与备注][accountNicknameAndRemark]
 */
@ContainerType("账户名称容器")
public interface AccountNameContainer : AccountNicknameContainer, AccountRemarkContainer {
    /**
     * 如果有备注则得到备注，否则得到昵称。
     */
    @JvmDefault
    val accountRemarkOrNickname: String?
        get() = accountRemark ?: accountNickname

    /**
     * 昵称与备注, 返回一个`账号(备注)?`格式的字符串。
     * 例如：
     * - `张三(张三的备注)`
     * - `李四` (没有备注)
     */
    @JvmDefault
    val accountNicknameAndRemark: String
        get() = "$accountNickname${accountRemark?.let { "($it)" } ?: ""}"
}


/**
 * 账户头像容器，定义可以得到一个头像链接。
 * 头像不是必须的，可能会不存在。
 */
@ContainerType("账户头像容器")
public interface AccountAvatarContainer {
    /**
     * 得到账号的头像地址. 一般来讲为`null`的可能性很小
     */
    val accountAvatar: String?
}

/**
 * 一个账号信息容器, 继承了
 * - [用户名称容器][AccountNameContainer],
 * - [用户头像容器][AccountAvatarContainer],
 * - [用户账号容器][AccountCodeContainer]
 */
@ContainerType("账户信息容器")
public interface AccountInfo : AccountNameContainer, AccountAvatarContainer, AccountCodeContainer


/**
 * 账号容器，可以得到一个账号的[信息][AccountInfo]。
 */
@ContainerType("账号容器")
public interface AccountContainer {
    /**
     * 账号的信息。一般来讲是不可能为null的，但是其中的信息就不一定了
     */
    val accountInfo: AccountInfo
}

