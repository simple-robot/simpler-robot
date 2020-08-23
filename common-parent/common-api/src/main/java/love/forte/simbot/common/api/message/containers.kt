/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot-S
 *  * File     containers.kt
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

package love.forte.simbot.common.api.message


/*
    container
    容器接口，定义容器接口。
    一个容器代表他可以从中得到什么数据。
 */

/**
 * 原始数据容器。
 * 定义可以得到原始数据的字符串信息。
 */
interface OriginalDataContainer {
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
interface AccountCodeContainer {
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
interface AccountNicknameContainer {
    /** 昵称 */
    val nickname: String?
}

/**
 * 账户备注容器。定义可以得到一个账号的备注信息。可能是好友备注或者群名片。
 * 备注可能不存在
 */
interface AccountRemarkContainer {
    /** 好友备注或群名片 */
    val remark: String?
}

/**
 * 账户名称容器，即[AccountNicknameContainer] 与 [AccountRemarkContainer]
 * 并提供一个默认的整合值[remarkOrNickname]
 */
interface AccountNameContainer: AccountNicknameContainer, AccountRemarkContainer {
    /**
     * 如果有备注则得到备注，否则得到昵称
     */
    @JvmDefault
    val remarkOrNickname: String? get() = nickname ?: remark
}


/**
 * 账户头像容器，定义可以得到一个头像链接。
 * 头像不是必须的，可能会不存在。
 */
interface AccountAvatarContainer {
    /**
     * 得到账号的头像地址
     */
    val avatar: String?
}

/**
 * 一个账号信息容器, 继承了[AccountNameContainer] [AccountAvatarContainer] [AccountCodeContainer]
 */
interface AccountContainer: AccountNameContainer, AccountAvatarContainer, AccountCodeContainer


/**
 * 群号容器。定义可以得到一个群的群号
 */
interface GroupCodeContainer {
    /** 群号 */
    val groupCode: String

    /** 群号的[Long]类型。如果可以作为数字的话。 */
    @JvmDefault
    val groupCodeNumber: Long get() = groupCode.toLong()
}

/**
 * 群头像容器。定义可以得到群头像
 * 头像不是必须的，可能会不存在。
 */
interface GroupAvatarContainer {
    /** 群头像 */
    val groupAvatar: String?
}

/**
 * 群信息容器，继承了[AccountContainer] [GroupAvatarContainer] [GroupCodeContainer]
 */
interface GroupContainer: AccountContainer, GroupAvatarContainer, GroupCodeContainer


/**
 * 机器人自身的账号容器
 */
interface BotCodeContainer {
    /** 当前的bot的账号 */
    val botCode: String
    /** 得到[botCode]的[Long]类型。如果可以作为数字的话。 */
    val botCodeNumber: Long get() = botCode.toLong()
}


/**
 * 标识容器。定义可以得到一个字符串标识。
 */
interface FlagContainer {
    /** 标识 */
    val flag: String
}


















