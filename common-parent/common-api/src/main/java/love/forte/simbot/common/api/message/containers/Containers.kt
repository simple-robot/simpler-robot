/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot-S
 *  * File     Containers.kt
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

@file:Suppress("unused")

package love.forte.simbot.common.api.message.containers

import love.forte.simbot.common.annotations.ContainerType
import love.forte.simbot.common.api.message.assists.ActionMotivations
import love.forte.simbot.common.api.message.assists.Flag
import love.forte.simbot.common.api.message.assists.FlagContent
import love.forte.simbot.common.api.message.assists.Permissions


/*
    container
    容器接口，定义容器接口。
    一个容器代表他可以从中得到什么数据。
 */

/**
 * 原始数据容器。
 * 定义可以得到原始数据的字符串信息。
 */
@ContainerType("原始数据容器")
public interface OriginalDataContainer {
    /**
     * 得到原始数据字符串。
     * 数据不应该为null。
     */
    val originalData: String
}

/**
 * 账户号码容器。定义可以得到一个账号的号码
 *
 */
@ContainerType("账户号码容器")
public interface AccountCodeContainer {
    /**
     * 账号
     */
    val accountCode: String

    /**
     * 账号的数字值。(如果能作为数字的话)
     * 默认实现为 [accountCode].[toLong]
     */
    @JvmDefault
    val accountCodeNumber: Long
        get() = accountCode.toLong()
}

/**
 * 账户昵称容器。定义可以得到一个账号的昵称
 */
@ContainerType("账户昵称容器")
public interface AccountNicknameContainer {
    /** 昵称 */
    val accountNickname: String
}

/**
 * 账户备注容器。定义可以得到一个账号的备注信息。可能是好友备注或者群名片。
 * 备注可能不存在
 */
@ContainerType("账户备注容器")
public interface AccountRemarkContainer {
    /** 好友备注或群名片 */
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
     * 如果有备注则得到备注，否则得到昵称
     */
    @JvmDefault
    val accountRemarkOrNickname: String
        get() = accountRemark ?: accountNickname

    /**
     * 昵称与备注, 返回一个`账号(备注)?`格式的字符串.
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
 * [用户名称容器][AccountNameContainer],
 * [用户头像容器][AccountAvatarContainer],
 * [用户账号容器][AccountCodeContainer]
 */
@ContainerType("账户信息容器")
public interface AccountContainer : AccountNameContainer, AccountAvatarContainer, AccountCodeContainer


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
    /** 群头像. 一般来讲为`null`的可能性很小 */
    val groupAvatar: String?
}

/**
 * 群名称容器，定义可以得到群的群名称
 */
public interface GroupNameContainer {
    /**
     * 群名称
     */
    val groupName: String
}

/**
 * 权限容器，定义可以得到一个人的[权限][Permissions]。
 *
 * 一般代表这个人在群里的权限
 */
@ContainerType("权限容器")
public interface PermissionContainer {
    val permission: Permissions
}

/**
 * 群信息容器，实现
 * [群头像容器][GroupAvatarContainer],
 * [群账号容器][GroupCodeContainer],
 * [群名称容器][GroupNameContainer]
 */
@ContainerType("群信息容器")
public interface GroupContainer : GroupAvatarContainer, GroupCodeContainer, GroupNameContainer


/**
 * 机器人自身的账号容器
 */
@ContainerType("机器人账号容器")
public interface BotCodeContainer {
    /** 当前的bot的账号 */
    val botCode: String

    /** 得到[botCode]的[Long]类型。如果可以作为数字的话。 */
    val botCodeNumber: Long get() = botCode.toLong()
}

/**
 * 机器人自身的名称容器
 */
@ContainerType("机器人名称容器")
public interface BotNameContainer {
    /**
     * 机器人的名称
     */
    val botName: String
}

/**
 * 机器人自身的头像容器
 * 头像不是必须的，可能不存在
 */
@ContainerType("机器人头像容器")
public interface BotAvatarContainer {
    /** 机器人的头像 */
    val botAvatar: String?
}

/**
 * 机器人基础信息容器, 其实现了
 * [机器人账号容器][BotCodeContainer],
 * [机器人名称容器][BotNameContainer],
 * [机器人头像容器][BotAvatarContainer]
 */
@ContainerType("机器人信息容器")
public interface BotContainer : BotCodeContainer, BotNameContainer, BotAvatarContainer


/**
 * 标识容器。定义可以得到一个标识。
 */
@ContainerType("标识容器")
public interface FlagContainer<out T: FlagContent> {
    /** 标识 */
    val flag: Flag<T>
}


/**
 * 操作者code容器，定义可以得到操作者与被操作者的code信息.
 */
@ContainerType("操作者账号容器容器")
public interface OperatingCodeContainer {
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
 * 操作者昵称容器，定义可以得到操作者与被操作者的昵称信息.
 */
@ContainerType("操作者昵称容器")
public interface OperatingNicknameContainer {
    /**
     * 操作者的昵称
     */
    val operatorNickname: String

    /**
     * 被操作者的昵称
     */
    val beOperatorNickname: String
}

/**
 * 操作者备注容器，定义可以得到操作者与被操作者的备注信息.
 */
@ContainerType("操作者备注容器")
public interface OperatingRemarkContainer {
    /**
     * 操作者的备注
     */
    val operatorRemark: String?

    /**
     * 被操作者的备注
     */
    val beOperatorRemark: String?
}

/**
 * 操作者名称容器，定义可以得到操作者与被操作者的昵称与备注信息.
 */
@ContainerType("操作者名称容器")
public interface OperatingNameContainer : OperatingNicknameContainer, OperatingRemarkContainer {
    /**
     * 操作者
     *
     * 如果有备注则得到备注，否则得到昵称
     */
    @JvmDefault
    val operatorRemarkOrNickname: String
        get() =  operatorRemark ?: operatorNickname

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
    /**
     * 被操作者
     *
     * 如果有备注则得到备注，否则得到昵称
     */
    @JvmDefault
    val beOperatorRemarkOrNickname: String
        get() =  beOperatorRemark ?: beOperatorNickname

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
public interface OperatingAvatarContainer {
    /**
     * 得到操作者的头像地址. 一般来讲为`null`的可能性很小
     */
    val operatorAvatar: String?

    /**
     * 得到被操作者的头像地址. 一般来讲为`null`的可能性很小
     */
    val beOperatorAvatar: String?
}

/**
 * 针对一个存在 **操作** 内容的事件,
 * 此容器提供了在 **操作** 事件中的 **操作者** 与 **被操作者** 的相关信息容器
 */
@ContainerType("操作者信息容器")
public interface OperatingContainer:
    OperatingCodeContainer,
    OperatingNameContainer,
    OperatingAvatarContainer {

}


/**
 * [行动动机][ActionMotivations]容器, 定义可以得到当前类型的动机类型。
 *
 * 一般来讲，此容器使用在枚举类上，例如消息事件中特有的类型枚举.
 *
 * @property actionMotivations ActionMotivations 得到对应的 [行动动机][ActionMotivations]
 */
@ContainerType("行动动机容器")
public interface ActionMotivationContainer {
    val actionMotivations: ActionMotivations
}













