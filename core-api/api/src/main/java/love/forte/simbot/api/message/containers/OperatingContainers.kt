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
 * 操作者账号容器
 */
@ContainerType("操作者账号容器")
public interface OperatorCodeContainer : Container {
    /**
     * 操作者的账号
     */
    val operatorCode: String

    /**
     * 操作者的code number
     */
    val operatorCodeNumber: Long
        get() = operatorCode.toLong()
}

/**
 * 被操作者账号容器
 */
@ContainerType("被操作者账号容器")
public interface BeOperatorCodeContainer : Container {
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
public interface OperatorNicknameContainer : Container {
    /**
     * 操作者的昵称
     */
    val operatorNickname: String?
}

/**
 * 被操作者昵称容器，定义可以得到被操作者的昵称信息.
 */
@ContainerType("被操作者昵称容器")
public interface BeOperatorNicknameContainer : Container {
    /**
     * 被操作者的昵称
     */
    val beOperatorNickname: String?
}


/**
 * 操作者备注容器，定义可以得到操作者的备注信息.
 */
@ContainerType("操作者备注容器")
public interface OperatorRemarkContainer : Container {
    /**
     * 操作者的备注
     */
    val operatorRemark: String?
}

/**
 * 被操作者备注容器，定义可以得到被操作者的备注信息.
 */
@ContainerType("操作者备注容器")
public interface BeOperatorRemarkContainer : Container {
    /**
     * 被操作者的备注
     */
    val beOperatorRemark: String?
}


/**
 * 操作者名称容器，定义可以得到操作者的昵称与备注信息.
 */
@ContainerType("操作者名称容器")
public interface OperatorNameContainer : Container, OperatorNicknameContainer, OperatorRemarkContainer {
    /**
     * 操作者
     *
     * 如果有备注则得到备注，否则得到昵称
     */
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
    val operatorNicknameAndRemark: String
        get() = "$operatorNickname${operatorRemark?.let { "($it)" } ?: ""}"
}


/**
 * 被操作者名称容器，定义可以得到被操作者的昵称与备注信息.
 */
@ContainerType("被操作者名称容器")
public interface BeOperatorNameContainer : Container, BeOperatorNicknameContainer, BeOperatorRemarkContainer {
    /**
     * 被操作者
     *
     * 如果有备注则得到备注，否则得到昵称
     */
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
    val beOperatorNicknameAndRemark: String
        get() = "$beOperatorNickname${beOperatorRemark?.let { "($it)" } ?: ""}"
}


/**
 * 操作者头像容器，定义可以得到一个头像链接。
 * 头像不是必须的，可能会不存在。
 */
@ContainerType("操作者头像容器")
public interface OperatorAvatarContainer : Container {
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
public interface BeOperatorAvatarContainer : Container {
    /**
     * 得到被操作者的头像地址. 一般来讲为`null`的可能性很小
     */
    val beOperatorAvatar: String?
}

/**
 * 操作者信息容器。
 *
 * 同时，[操作者][OperatorInfo] 也属于一个 [账号信息][AccountInfo].
 */
@ContainerType("操作者信息容器")
public interface OperatorInfo : Container, OperatorCodeContainer, OperatorNameContainer, OperatorAvatarContainer, AccountInfo {
    override val accountCode: String
        get() = operatorCode
    override val accountNickname: String?
        get() = operatorNickname
    override val accountRemark: String?
        get() = operatorRemark
    override val accountAvatar: String?
        get() = operatorAvatar
}

/**
 * 可以得到操作者信息的容器
 * @property operatorInfo OperatorInfoContainer
 */
@ContainerType("操作者信息")
public interface OperatorContainer : Container {
    /**
     * 得到一个操作者信息 可能会是null
     */
    val operatorInfo: OperatorInfo?
}

/**
 * 构建一个默认数据 [OperatorContainer] 实例。
 */
@JvmName("getOperatorContainer")
@Suppress("FunctionName")
public fun OperatorContainer(operatorInfo: OperatorInfo?): OperatorContainer =
    operatorInfo?.let { OperatorContainerData(it) } ?: EmptyOperatorContainer


/** [OperatorContainer] 数据类实现。 */
private data class OperatorContainerData(override val operatorInfo: OperatorInfo) : OperatorContainer

/** [OperatorContainer] 空实现。 */
private object EmptyOperatorContainer : OperatorContainer {
    override val operatorInfo: OperatorInfo? = null
}


/**
 * 被操作者信息容器。
 * 同时，[被操作者][BeOperatorInfo] 也属于一个 [账号信息][AccountInfo].
 */
@ContainerType("被操作者信息")
public interface BeOperatorInfo : Container, BeOperatorCodeContainer, BeOperatorNameContainer, BeOperatorAvatarContainer, AccountInfo {
    override val accountCode: String
        get() = beOperatorCode
    override val accountNickname: String?
        get() = beOperatorNickname
    override val accountRemark: String?
        get() = beOperatorRemark
    override val accountAvatar: String?
        get() = beOperatorAvatar
}


/**
 * 可以得到被操作者信息的容器
 */
@ContainerType("被操作者容器")
public interface BeOperatorContainer : Container {
    /**
     * 得到一个被操作者信息  可能会是null
     */
    val beOperatorInfo: BeOperatorInfo?
}


/** 获取普通 [BeOperatorContainer] 实例。*/
@JvmName("getBeOperatorContainer")
@Suppress("FunctionName")
public fun BeOperatorContainer(beOperatorInfo: BeOperatorInfo?): BeOperatorContainer =
    beOperatorInfo?.let { BeOperatorContainerData(it) } ?: EmptyBeOperatorContainer


/** [BeOperatorContainer] 数据类实现。 */
private data class BeOperatorContainerData(override val beOperatorInfo: BeOperatorInfo) : BeOperatorContainer
/** [BeOperatorContainer] 空实现。 */
private object EmptyBeOperatorContainer : BeOperatorContainer {
    override val beOperatorInfo: BeOperatorInfo? = null
}


/**
 * 针对一个存在 **操作** 内容的事件,
 * 此容器提供了在 **操作** 事件中的 **操作者** 与 **被操作者** 的相关信息容器
 *
 */
@ContainerType("操作者容器")
public interface OperatingContainer : Container, OperatorContainer, BeOperatorContainer


/**
 * 获取数据 [OperatingContainer] 实例。
 */
@JvmName("getOperatingContainer")
@Suppress("FunctionName")
public fun OperatingContainer(
    operatorInfo: OperatorInfo?,
    beOperatorInfo: BeOperatorInfo?
) : OperatingContainer =
    if (operatorInfo == null && beOperatorInfo == null) {
        OperatingContainerData(operatorInfo, beOperatorInfo)
    } else EmptyOperatingContainer



/** [OperatingContainer] 数据类实现。 */
private data class OperatingContainerData(
    override val operatorInfo: OperatorInfo?,
    override val beOperatorInfo: BeOperatorInfo?
) : OperatingContainer

/** [OperatingContainer] 空实现。 */
private object EmptyOperatingContainer : OperatingContainer {
    override val operatorInfo: OperatorInfo? = null
    override val beOperatorInfo: BeOperatorInfo? = null
}


/**
 * 将账户作操作者。
 */
public fun AccountInfo.asOperator(): OperatorInfo = AccountAsOperator(this)
/**
 * 将操作者作账户。
 */
@Deprecated("Just use itself.", ReplaceWith("this"))
public fun OperatorInfo.asAccount(): AccountInfo = this


/**
 * 将账户作操作者。一般用于那些可以将当前事件的
 * [账户信息][AccountInfo] 作为 [操作者][OperatorInfo] 而使用的地方
 */
private data class AccountAsOperator(private val account: AccountInfo) : Container, OperatorInfo {
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
 * 将账户作为被操作者。
 */
public fun AccountInfo.asBeOperator(): BeOperatorInfo = AccountAsBeOperator(this)
/**
 * 将被操作者作为账户。
 */
@Deprecated("Just use itself.", ReplaceWith("this"))
public fun BeOperatorInfo.asAccount(): AccountInfo = this


/**
 * 将账户作为被操作者。一般用于那些可以将当前事件的
 * [账户信息][AccountInfo] 作为 [被操作者][BeOperatorInfo] 而使用的地方
 */
private data class AccountAsBeOperator(private val account: AccountInfo) : Container, BeOperatorInfo {
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
     * 被操作者的昵称与备注, 返回一个`账号(备注)?`格式的字符串。
     * 例如：
     * - `张三(张三的备注)`
     * - `李四`
     */
    override val beOperatorNicknameAndRemark: String
        get() = account.accountNicknameAndRemark
}



