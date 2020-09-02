/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot-S
 *  * File     OperatingContainers.kt
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

package love.forte.simbot.common.api.message.containers

import love.forte.simbot.common.annotations.ContainerType

/**
 * 操作者code容器
 */
@ContainerType("操作者账号容器")
public interface OperatorCodeContainer {
    /**
     * 操作者的code
     */
    val operatorCode: String

    /**
     * 操作者的code number
     */
    @JvmDefault
    val operatorCodeNumber: Long
        get() = operatorCode.toLong()
}

/**
 * 被操作者code容器
 */
@ContainerType("被操作者账号容器")
public interface BeOperatorCodeContainer {
    /**
     * 被操作者的Code
     */
    val beOperatorCode: String

    /**
     * 被操作者的code number
     */
    val beOperatorCodeNumber: Long get() = beOperatorCode.toLong()
}

/**
 * 操作者昵称容器，定义可以得到操作者的昵称信息.
 */
@ContainerType("操作者昵称容器")
public interface OperatorNicknameContainer {
    /**
     * 操作者的昵称
     */
    val operatorNickname: String?
}

/**
 * 被操作者昵称容器，定义可以得到被操作者的昵称信息.
 */
@ContainerType("被操作者昵称容器")
public interface BeOperatorNicknameContainer {
    /**
     * 被操作者的昵称
     */
    val beOperatorNickname: String?
}


/**
 * 操作者备注容器，定义可以得到操作者的备注信息.
 */
@ContainerType("操作者备注容器")
public interface OperatorRemarkContainer {
    /**
     * 操作者的备注
     */
    val operatorRemark: String?
}

/**
 * 被操作者备注容器，定义可以得到被操作者的备注信息.
 */
@ContainerType("操作者备注容器")
public interface BeOperatorRemarkContainer {
    /**
     * 被操作者的备注
     */
    val beOperatorRemark: String?
}


/**
 * 操作者名称容器，定义可以得到操作者的昵称与备注信息.
 */
@ContainerType("操作者名称容器")
public interface OperatorNameContainer : OperatorNicknameContainer, OperatorRemarkContainer {
    /**
     * 操作者
     *
     * 如果有备注则得到备注，否则得到昵称
     */
    @JvmDefault
    val operatorRemarkOrNickname: String?
        get() = operatorRemark ?: operatorNickname

    /**
     * 操作者
     *
     * 昵称与备注, 返回一个`账号(备注)?`格式的字符串.
     * 例如：
     * - `张三(张三的备注)`
     * - `李四` (没有备注)
     */
    @JvmDefault
    val operatorNicknameAndRemark: String
        get() = "$operatorNickname${operatorRemark?.let { "($it)" } ?: ""}"
}


/**
 * 被操作者名称容器，定义可以得到被操作者的昵称与备注信息.
 */
@ContainerType("被操作者名称容器")
public interface BeOperatorNameContainer : BeOperatorNicknameContainer, BeOperatorRemarkContainer {
    /**
     * 被操作者
     *
     * 如果有备注则得到备注，否则得到昵称
     */
    @JvmDefault
    val beOperatorRemarkOrNickname: String?
        get() = beOperatorRemark ?: beOperatorNickname

    /**
     * 被操作者
     *
     * 昵称与备注, 返回一个`账号(备注)?`格式的字符串.
     * 例如：
     * - `张三(张三的备注)`
     * - `李四` (没有备注)
     */
    @JvmDefault
    val beOperatorNicknameAndRemark: String
        get() = "$beOperatorNickname${beOperatorRemark?.let { "($it)" } ?: ""}"
}


/**
 * 操作者头像容器，定义可以得到一个头像链接。
 * 头像不是必须的，可能会不存在。
 */
@ContainerType("操作者头像容器")
public interface OperatorAvatarContainer {
    /**
     * 得到操作者的头像地址. 一般来讲为`null`的可能性很小
     */
    val operatorAvatar: String?
}


/**
 * 被操作者头像容器，定义可以得到一个头像链接。
 * 头像不是必须的，可能会不存在。
 */
@ContainerType("被操作者头像容器")
public interface BeOperatorAvatarContainer {
    /**
     * 得到被操作者的头像地址. 一般来讲为`null`的可能性很小
     */
    val beOperatorAvatar: String?
}

/**
 * 操作者信息容器
 */
@ContainerType("操作者头像容器")
public interface OperatorInfoContainer : OperatorCodeContainer, OperatorNameContainer, OperatorAvatarContainer

/**
 * 可以得到操作者信息的容器
 * @property operatorInfo OperatorInfoContainer
 */
@ContainerType("操作者容器")
public interface OperatorContainer {
    /**
     * 得到一个操作者信息 可能会是null
     */
    val operatorInfo: OperatorInfoContainer?
}

/**
 * 被操作者信息容器
 */
@ContainerType("被操作者头像容器")
public interface BeOperatorInfoContainer : BeOperatorCodeContainer, BeOperatorNameContainer, BeOperatorAvatarContainer


/**
 * 可以得到被操作者信息的容器
 */
@ContainerType("被操作者容器")
public interface BeOperatorContainer {
    /**
     * 得到一个被操作者信息  可能会是null
     */
    val beOperatorInfo: BeOperatorInfoContainer?
}


/**
 * 针对一个存在 **操作** 内容的事件,
 * 此容器提供了在 **操作** 事件中的 **操作者** 与 **被操作者** 的相关信息容器
 */
@ContainerType("操作者容器")
public interface OperatingContainer : OperatorContainer, BeOperatorContainer


/**
 * 将账户作操作者。一般用于那些可以将当前事件的
 * [账户信息][AccountInfoContainer] 作为 [操作者][OperatorInfoContainer] 而使用的地方
 */
public data class AccountAsOperator(private val account: AccountInfoContainer) : OperatorInfoContainer {
    /**
     * 被操作者的Code
     */
    override val operatorCode: String
        get() = account.accountCode

    /**
     * 被操作者的昵称
     */
    override val operatorNickname: String?
        get() = account.accountNickname

    /**
     * 被操作者的备注
     */
    override val operatorRemark: String?
        get() = account.accountRemark

    /**
     * 得到被操作者的头像地址. 一般来讲为`null`的可能性很小
     */
    override val operatorAvatar: String?
        get() = account.accountAvatar

    /**
     * 被操作者的code number
     */
    override val operatorCodeNumber: Long
        get() = account.accountCodeNumber

    /**
     * 被操作者
     *
     * 如果有备注则得到备注，否则得到昵称
     */
    override val operatorRemarkOrNickname: String?
        get() = account.accountRemarkOrNickname

    /**
     * 被操作者
     *
     * 昵称与备注, 返回一个`账号(备注)?`格式的字符串.
     * 例如：
     * - `张三(张三的备注)`
     * - `李四` (没有备注)
     */
    override val operatorNicknameAndRemark: String
        get() = account.accountNicknameAndRemark
}


/**
 * 将账户作为被操作者。一般用于那些可以将当前事件的
 * [账户信息][AccountInfoContainer] 作为 [被操作者][BeOperatorInfoContainer] 而使用的地方
 */
public data class AccountAsBeOperator(private val account: AccountInfoContainer) : BeOperatorInfoContainer {
    /**
     * 被操作者的Code
     */
    override val beOperatorCode: String
        get() = account.accountCode

    /**
     * 被操作者的昵称
     */
    override val beOperatorNickname: String?
        get() = account.accountNickname

    /**
     * 被操作者的备注
     */
    override val beOperatorRemark: String?
        get() = account.accountRemark

    /**
     * 得到被操作者的头像地址. 一般来讲为`null`的可能性很小
     */
    override val beOperatorAvatar: String?
        get() = account.accountAvatar

    /**
     * 被操作者的code number
     */
    override val beOperatorCodeNumber: Long
        get() = account.accountCodeNumber

    /**
     * 被操作者
     *
     * 如果有备注则得到备注，否则得到昵称
     */
    override val beOperatorRemarkOrNickname: String?
        get() = account.accountRemarkOrNickname

    /**
     * 被操作者
     *
     * 昵称与备注, 返回一个`账号(备注)?`格式的字符串.
     * 例如：
     * - `张三(张三的备注)`
     * - `李四` (没有备注)
     */
    override val beOperatorNicknameAndRemark: String
        get() = account.accountNicknameAndRemark
}

