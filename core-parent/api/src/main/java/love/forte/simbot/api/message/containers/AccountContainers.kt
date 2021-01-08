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

@file:JvmName("Containers")
@file:JvmMultifileClass
package love.forte.simbot.api.message.containers

import love.forte.simbot.annotation.ContainerType


/**
 * 账户号码容器。定义可以得到一个账号的号码。
 *
 */
@ContainerType("账户号码容器")
public interface AccountCodeContainer : Container {
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
public interface AccountNicknameContainer : Container {
    /**
     * 昵称。
     * 可能会出现为null的情况，但是一般情况下不会。
     */
    val accountNickname: String?
}

/**
 * 账户备注容器。定义可以得到一个账号的备注信息。可能是好友备注或者群名片。
 * 备注可能不存在。
 */
@ContainerType("账户备注容器")
public interface AccountRemarkContainer : Container {
    /** 好友备注或群名片。可能为null。 */
    val accountRemark: String?
}

/**
 * 账户名称容器，即 [账号昵称容器][AccountNicknameContainer] 与 [账号备注容器][AccountRemarkContainer]
 * 并提供几个默认的整合值:
 * - [备注或昵称][accountRemarkOrNickname]
 * - [昵称与备注][accountNicknameAndRemark]
 */
@ContainerType("账户名称容器")
public interface AccountNameContainer : Container, AccountNicknameContainer, AccountRemarkContainer {
    /**
     * 如果有备注则得到备注，否则得到昵称。
     */
    @JvmDefault
    val accountRemarkOrNickname: String?
        get() = accountRemark ?: accountNickname

    /**
     * 昵称与备注, 返回一个`(账号(备注)?)?`格式的字符串。
     * 例如：
     * - `“张三(张三的备注)”`
     * - `“李四”` (没有备注)
     * - `“”` (空)
     */
    @JvmDefault
    val accountNicknameAndRemark: String
        get() = accountRemark?.let { "$accountNickname($it)" } ?: accountNickname ?: ""
}


/**
 * 账户头像容器，定义可以得到一个头像链接。
 * 头像不是必须的，可能会不存在。
 */
@ContainerType("账户头像容器")
public interface AccountAvatarContainer : Container {
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
public interface AccountInfo : Container, AccountNameContainer, AccountAvatarContainer, AccountCodeContainer


public fun emptyAccountInfo() : AccountInfo = EmptyAccountInfo


private object EmptyAccountInfo : AccountInfo {
    override val accountCode: String
        get() = ""
    override val accountNickname: String?
        get() = null
    override val accountRemark: String?
        get() = null
    override val accountAvatar: String?
        get() = null
    override fun toString(): String {
        return "EmptyAccountInfo"
    }
}





/**
 * 账号容器，可以得到一个账号的[信息][AccountInfo]。
 */
@ContainerType("账号容器")
public interface AccountContainer : Container {
    /**
     * 账号的信息。一般来讲是不可能为null的，但是其中的信息就不一定了
     */
    val accountInfo: AccountInfo
}


/**
 * 获取一个 [AccountContainer] 数据实例。
 */
@JvmName("getAccountContainer")
@Suppress("FunctionName")
public fun AccountContainer(accountInfo: AccountInfo): AccountContainer = AccountContainerData(accountInfo)

/**
 * 获取一个 [AccountContainer] 数据实例。
 * java直接使用上面那个。
 */
@JvmName("__getAccountContainer")
@Suppress("FunctionName")
public inline fun AccountContainer(accountInfo: () -> AccountInfo): AccountContainer = AccountContainer(accountInfo())


/** [AccountContainer] 数据类实现。 */
private data class AccountContainerData(override val accountInfo: AccountInfo) : AccountContainer



public fun BotInfo.botAsAccountInfo() : AccountInfo = BotAccountInfo(this)

/**
 * 将 [BotInfo] 作为一个 [AccountInfo]
 */
private data class BotAccountInfo(private val botInfo: BotInfo) : AccountInfo {
    /**
     * 昵称。
     * 可能会出现为null的情况，但是一般情况下不会。
     */
    override val accountNickname: String?
        get() = botInfo.botName

    /** 好友备注或群名片。可能为null。 */
    override val accountRemark: String?
        get() = null

    /**
     * 得到账号的头像地址. 一般来讲为`null`的可能性很小
     */
    override val accountAvatar: String?
        get() = botInfo.botAvatar

    /**
     * 账号
     */
    override val accountCode: String
        get() = botInfo.botCode

}

