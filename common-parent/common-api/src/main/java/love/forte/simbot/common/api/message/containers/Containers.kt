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


/*
    container
    容器接口，定义容器接口。
    一个容器代表他可以从中得到什么数据。
 */

/**
 * 原始数据容器。
 * 定义可以得到原始数据的字符串信息。
 */
public interface OriginalDataContainer {
    /**
     * 得到原始数据字符串。
     * 数据不应该为null。
     */
    val originalData: String
}

/**
 * 账户号码容器。定义可以得到一个账号的号码
 * 其中,[codeNumber]默认为[code].[toLong]
 */
public interface AccountCodeContainer {
    /** 账号 */
    val code: String
    /** 账号的数字值。（如果能作为数字的话） */
    @JvmDefault
    val codeNumber: Long get() = code.toLong()
}

/**
 * 账户昵称容器。定义可以得到一个账号的昵称
 * 昵称可能不存在。
 */
public interface AccountNicknameContainer {
    /** 昵称 */
    val nickname: String?
}

/**
 * 账户备注容器。定义可以得到一个账号的备注信息。可能是好友备注或者群名片。
 * 备注可能不存在
 */
public interface AccountRemarkContainer {
    /** 好友备注或群名片 */
    val remark: String?
}

/**
 * 账户名称容器，即 [账号昵称容器][AccountNicknameContainer] 与 [账号备注容器][AccountRemarkContainer]
 * 并提供几个默认的整合值:
 * - [备注或昵称][remarkOrNickname]
 * - [昵称与备注][nicknameAndRemark]
 */
public interface AccountNameContainer: AccountNicknameContainer, AccountRemarkContainer {
    /**
     * 如果有备注则得到备注，否则得到昵称
     */
    @JvmDefault
    val remarkOrNickname: String? get() = nickname ?: remark

    /**
     * 昵称与备注, 返回一个`账号(备注)?`格式的字符串.
     * 例如：
     * - `张三(张三的备注)`
     * - `李四` (没有备注)
     */
    @JvmDefault
    val nicknameAndRemark: String get() = "$nickname${remark?.let { "($it)" }?:""}"
}


/**
 * 账户头像容器，定义可以得到一个头像链接。
 * 头像不是必须的，可能会不存在。
 */
public interface AccountAvatarContainer {
    /**
     * 得到账号的头像地址. 一般来讲为`null`的可能性很小
     */
    val avatar: String?
}

/**
 * 一个账号信息容器, 继承了
 * [用户名称容器][AccountNameContainer] 
 * [用户头像容器][AccountAvatarContainer] 
 * [用户账号容器][AccountCodeContainer]
 */
public interface AccountContainer: AccountNameContainer, AccountAvatarContainer, AccountCodeContainer


/**
 * 群号容器。定义可以得到一个群的群号
 */
public interface GroupCodeContainer {
    /** 群号 */
    val groupCode: String

    /** 群号的[Long]类型。如果可以作为数字的话。 */
    @JvmDefault
    val groupCodeNumber: Long get() = groupCode.toLong()
}

/**
 * 群头像容器。定义可以得到群头像
 * 头像不是必须的，可能会不存在
 */
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
 * 群信息容器，实现
 * [群头像容器][GroupAvatarContainer] 
 * [群账号容器][GroupCodeContainer]
 * [群名称容器][GroupNameContainer]
 */
public interface GroupContainer: GroupAvatarContainer, GroupCodeContainer, GroupNameContainer


/**
 * 机器人自身的账号容器
 */
public interface BotCodeContainer {
    /** 当前的bot的账号 */
    val botCode: String
    /** 得到[botCode]的[Long]类型。如果可以作为数字的话。 */
    val botCodeNumber: Long get() = botCode.toLong()
}

/**
 * 机器人自身的名称容器
 */
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
public interface BotAvatarContainer {
    /** 机器人的头像 */
    val botAvatar: String?
}

/**
 * 机器人基础信息容器, 其实现了
 * [机器人账号容器][BotCodeContainer]
 * [机器人名称容器][BotNameContainer]
 * [机器人头像容器][BotAvatarContainer]
 */
public interface BotContainer: BotCodeContainer, BotNameContainer, BotAvatarContainer



/**
 * 标识容器。定义可以得到一个字符串标识。
 * 但是一般来讲, [MsgGet]的[FlagContainer]实现容器基本上都可以通过[MsgGet.id]来实现
 */
public interface FlagContainer {
    /** 标识 */
    val flag: String
}


















